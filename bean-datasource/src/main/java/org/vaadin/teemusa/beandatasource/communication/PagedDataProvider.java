package org.vaadin.teemusa.beandatasource.communication;

import java.util.Collections;

import org.vaadin.teemusa.beandatasource.DataProvider;
import org.vaadin.teemusa.beandatasource.client.DataRequestRpc;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource.HasPaging;

import com.vaadin.shared.ui.grid.Range;

public class PagedDataProvider<T> extends DataProvider<T> {

	private HasPaging<T> paging;
	private DataSource<T> container;

	public PagedDataProvider(DataSource<T> container) {
		this(container, new KeyMapper<T>());
	}

	@SuppressWarnings("unchecked")
	public PagedDataProvider(DataSource<T> container, BeanKeyMapper<T> beanKeyMapper) {
		super(beanKeyMapper);

		if (container instanceof HasPaging) {
			this.container = container;
			this.paging = (HasPaging<T>) container;
		} else {
			throw new IllegalArgumentException("Container is not implementing HasPaging");
		}

		registerRpc(new DataRequestRpc() {
			@Override
			public void requestRows(int firstRow, int numberOfRows, int firstCachedRowIndex, int cacheSize) {
				pushRowData(firstRow, numberOfRows, firstCachedRowIndex, cacheSize);
			}
		});
	}

	@Override
	public void beforeClientResponse(boolean initial) {
		super.beforeClientResponse(initial);

		if (initial) {
			long size = container.size();
			updateSize(size);
			pushRowData(0, (int) Math.min(size, 40), 0, 0);
		}
	}

	private void pushRowData(int firstRowToPush, int numberOfRows, int firstCachedRowIndex, int cacheSize) {
		Range newRange = Range.withLength(firstRowToPush, numberOfRows);

		Iterable<T> beans = paging.getPage(newRange.getStart(), newRange.getEnd());

		pushRows(firstRowToPush, beans);
	}

	public void removeBean(T bean) {
		if (bean == null) {
			return;
		}

		rpc.dropRow(getRowData(bean));
	}

	public void addBean(T bean) {
		if (bean == null) {
			return;
		}

		pushRows(container.size() - 1, Collections.singleton(bean));
	}

}
