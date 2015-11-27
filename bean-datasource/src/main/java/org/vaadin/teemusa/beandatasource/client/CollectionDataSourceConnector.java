package org.vaadin.teemusa.beandatasource.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.teemusa.beandatasource.communication.CollectionDataProvider;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@Connect(CollectionDataProvider.class)
public class CollectionDataSourceConnector extends AbstractExtensionConnector {

	public class RpcListDataSource implements DataSource<JsonObject> {

		private class ListRowHandle extends RowHandle<JsonObject> {

			private JsonObject row;

			public ListRowHandle(JsonObject row) {
				this.row = row;
			}

			@Override
			public JsonObject getRow() throws IllegalStateException {
				return row;
			}

			@Override
			public void pin() {
			}

			@Override
			public void unpin() throws IllegalStateException {
			}

			@Override
			public void updateRow() {
				changeHandler.dataUpdated(ds.indexOf(this), 1);
			}

			public void updateRow(JsonObject row) {
				this.row = row;
				updateRow();
			}

			@Override
			protected boolean equalsExplicit(Object obj) {
				JsonObject rowKey = null;
				if (obj instanceof JsonObject) {
					rowKey = getRowKey((JsonObject) obj);
				} else if (obj instanceof ListRowHandle) {
					rowKey = getRowKey(((ListRowHandle) obj).getRow());
				}

				if (getRowKey(row).equals(rowKey)) {
					return true;
				}
				return false;
			}

			@Override
			protected int hashCodeExplicit() {
				return row.hashCode();
			}

		}

		private DataChangeHandler changeHandler;
		private List<ListRowHandle> ds = new ArrayList<ListRowHandle>();
		private Map<JsonObject, ListRowHandle> keyToHandle = new HashMap<JsonObject, ListRowHandle>();
		private DataDropRpc dropRpc = getRpcProxy(DataDropRpc.class);

		@Override
		public void ensureAvailability(int firstRowIndex, int numberOfRows) {
			if (firstRowIndex >= ds.size()) {
				throw new IllegalStateException("Trying to fetch rows outside of array");
			}

			if (changeHandler != null) {
				changeHandler.dataAvailable(firstRowIndex, numberOfRows);
			}
		}

		public JsonObject getRowKey(JsonObject row) {
			if (row.hasKey(GridState.JSONKEY_ROWKEY)) {
				JsonObject rowKey = row.getObject(GridState.JSONKEY_ROWKEY);
				if (rowKey != null) {
					return rowKey;
				}
			}

			throw new IllegalArgumentException("No row key on object.");
		}

		@Override
		public JsonObject getRow(int rowIndex) {
			return ds.get(rowIndex).getRow();
		}

		@Override
		public int size() {
			return ds.size();
		}

		@Override
		public void setDataChangeHandler(DataChangeHandler dataChangeHandler) {
			this.changeHandler = dataChangeHandler;
		}

		@Override
		public ListRowHandle getHandle(JsonObject row) {
			JsonObject rowKey = getRowKey(row);
			if (keyToHandle.containsKey(rowKey)) {
				return keyToHandle.get(rowKey);
			}
			return null;
		}

		public void add(JsonObject object) {
			ListRowHandle e = new ListRowHandle(object);
			ds.add(e);
			keyToHandle.put(getRowKey(object), e);
		}

		public void replace(long i, JsonObject object) {
			ListRowHandle listRowHandle = ds.get((int) i);
			if (listRowHandle.equals(object)) {
				listRowHandle.updateRow(object);
			} else {
				throw new UnsupportedOperationException("Overriding unexpectedly!");
			}
		}

		public void remove(JsonObject row) {
			JsonObject rowKey = getRowKey(row);
			ListRowHandle e = keyToHandle.remove(rowKey);
			int index = ds.indexOf(e);
			ds.remove(index);
			changeHandler.dataRemoved(index, 1);
			changeHandler.dataUpdated(index, size() - index);

			JsonArray droppedKeys = Json.createArray();
			droppedKeys.set(0, rowKey);
			dropRpc.dropRows(droppedKeys);
			getConnection().sendPendingVariableChanges();
		}

		public void setRowData(long firstRowIndex, JsonArray rowDataJson) {
			int updated = 0, added = 0;
			for (int i = 0; i < rowDataJson.length(); ++i) {
				if (i + firstRowIndex == size()) {
					add(rowDataJson.getObject(i));
					++added;
				} else {
					replace(i + firstRowIndex, rowDataJson.getObject(i));
					++updated;
				}
			}

			if (updated > 0) {
				changeHandler.dataUpdated((int) firstRowIndex, updated);
			}
			if (added > 0) {
				changeHandler.dataAdded((int) (firstRowIndex + updated), added);
			}
		}

	}

	@Override
	protected void extend(ServerConnector target) {
		if (getParent() instanceof HasDataSource) {
			final RpcListDataSource rpcDataSource = new RpcListDataSource();
			((HasDataSource) getParent()).setDataSource(rpcDataSource);

			registerRpc(DataProviderRpc.class, new DataProviderRpc() {

				@Override
				public void updateRow(JsonObject row) {
					rpcDataSource.getHandle(row).updateRow(row);
				}

				@Override
				public void setRowData(long firstRowIndex, JsonArray rowDataJson) {
					rpcDataSource.setRowData(firstRowIndex, rowDataJson);
				}

				@Override
				public void dropRow(JsonObject row) {
					rpcDataSource.remove(row);
				}

				@Override
				public void resetDataAndSize(long newSize) {
					throw new UnsupportedOperationException();
				}
			});
		}
	}

}
