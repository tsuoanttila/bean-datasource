package org.vaadin.teemusa.beandatasource.client;

import java.util.ArrayList;
import java.util.Collections;

import org.vaadin.teemusa.beandatasource.communication.PagedDataProvider;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@Connect(PagedDataProvider.class)
public class PagedDataSourceConnector extends AbstractExtensionConnector {

	public class RpcDataSource extends AbstractRemoteDataSource<JsonObject> {

		protected RpcDataSource() {
			registerRpc(DataProviderRpc.class, new DataProviderRpc() {
				@Override
				public void setRowData(long firstRow, JsonArray rowArray) {
					if (firstRow + rowArray.length() > size()) {
						insertRowData(size(), (int) (firstRow + rowArray.length() - size()));
					}

					ArrayList<JsonObject> rows = new ArrayList<JsonObject>(rowArray.length());
					for (int i = 0; i < rowArray.length(); i++) {
						JsonObject rowObject = rowArray.getObject(i);
						rows.add(rowObject);
					}

					RpcDataSource.this.setRowData((int) firstRow, rows);
				}

				@Override
				public void resetDataAndSize(long size) {
					RpcDataSource.this.resetDataAndSize((int) size);
				}

				@Override
				public void updateRow(JsonObject row) {
					RpcDataSource.this.updateRowData(row);
				}

				@Override
				public void dropRow(JsonObject row) {
					int index = indexOfKey(getRowKey(row));
					if (index >= 0) {
						removeRowData(index, 1);
						if (handler != null) {
							handler.dataUpdated(index, size() - index);
						}
					} else {
						resetDataAndSize(size() - 1);
					}
				}
			});

			registerRpc(PagedDataProviderRpc.class, new PagedDataProviderRpc() {

				@Override
				public void removeRowData(int firstRowIndex, int count) {
					RpcDataSource.this.removeRowData(firstRowIndex, count);
				}

				@Override
				public void insertRowData(int firstRowIndex, int count) {
					RpcDataSource.this.insertRowData(firstRowIndex, count);
				}

			});
		}

		private DataDropRpc dropRpc = getRpcProxy(DataDropRpc.class);
		private DataRequestRpc requestRpc = getRpcProxy(DataRequestRpc.class);
		private JsonArray droppedRowKeys = Json.createArray();
		private DataChangeHandler handler;

		@Override
		protected void requestRows(int firstRowIndex, int numberOfRows, RequestRowsCallback<JsonObject> callback) {
			if (droppedRowKeys.length() > 0) {
				dropRpc.dropRows(droppedRowKeys);
				droppedRowKeys = Json.createArray();
			}

			/*
			 * If you're looking at this code because you want to learn how to
			 * use AbstactRemoteDataSource, please look somewhere else instead.
			 * 
			 * We're not doing things in the conventional way with the callback
			 * here since Vaadin doesn't directly support RPC with return
			 * values. We're instead asking the server to push us some data, and
			 * when we receive pushed data, we just push it along to the
			 * underlying cache in the same way no matter if it was a genuine
			 * push or just a result of us requesting rows.
			 */

			Range cached = getCachedRange();

			requestRpc.requestRows(firstRowIndex, numberOfRows, cached.getStart(), cached.length());

			/*
			 * Show the progress indicator if there is a pending data request
			 * and some of the visible rows are being requested. The RPC in
			 * itself will not trigger the indicator since it might just fetch
			 * some rows in the background to fill the cache.
			 * 
			 * The indicator will be hidden by the framework when the response
			 * is received (unless another request is already on its way at that
			 * point).
			 */
			if (getRequestedAvailability().intersects(Range.withLength(firstRowIndex, numberOfRows))) {
				getConnection().getLoadingIndicator().ensureTriggered();
			}
		}

		@Override
		public void ensureAvailability(int firstRowIndex, int numberOfRows) {
			super.ensureAvailability(firstRowIndex, numberOfRows);

			/*
			 * We trigger the indicator already at this point since the actual
			 * RPC will not be sent right away when waiting for the response to
			 * a previous request.
			 * 
			 * Only triggering here would not be enough since the check that
			 * sets isWaitingForData is deferred. We don't want to trigger the
			 * loading indicator here if we don't know that there is actually a
			 * request going on since some other bug might then cause the
			 * loading indicator to not be hidden.
			 */
			if (isWaitingForData() && !Range.withLength(firstRowIndex, numberOfRows).isSubsetOf(getCachedRange())) {
				getConnection().getLoadingIndicator().ensureTriggered();
			}
		}

		@Override
		public String getRowKey(JsonObject row) {
			if (row.hasKey(GridState.JSONKEY_ROWKEY)) {
				return row.getString(GridState.JSONKEY_ROWKEY);
			} else {
				return null;
			}
		}

		public RowHandle<JsonObject> getHandleByKey(Object key) {
			JsonObject row = Json.createObject();
			row.put(GridState.JSONKEY_ROWKEY, (String) key);
			return new RowHandleImpl(row, key);
		}

		@Override
		protected void unpinHandle(RowHandleImpl handle) {
			// Row data is no longer available after it has been unpinned.
			String key = getRowKey(handle.getRow());
			super.unpinHandle(handle);
			if (!handle.isPinned()) {
				if (indexOfKey(key) == -1) {
					// Row out of view has been unpinned. drop it
					droppedRowKeys.set(droppedRowKeys.length(), key);
				}
			}
		}

		/**
		 * Updates row data based on row key.
		 * 
		 * @since 7.6
		 * @param row
		 *            new row object
		 */
		protected void updateRowData(JsonObject row) {
			int index = indexOfKey(getRowKey(row));
			if (index >= 0) {
				setRowData(index, Collections.singletonList(row));
			}
		}

		@Override
		protected void onDropFromCache(int rowIndex, JsonObject row) {
			if (!isPinned(row)) {
				droppedRowKeys.set(droppedRowKeys.length(), getRowKey(row));
			}
		}

		@Override
		public void setDataChangeHandler(DataChangeHandler dataChangeHandler) {
			super.setDataChangeHandler(dataChangeHandler);

			handler = dataChangeHandler;
		}

	}

	@Override
	protected void extend(ServerConnector target) {
		if (getParent() instanceof HasDataSource) {
			((HasDataSource) getParent()).setDataSource(new RpcDataSource());
		}
	}
}
