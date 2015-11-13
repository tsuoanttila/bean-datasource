package org.vaadin.teemusa.beandatasource.communication;

import java.util.Collections;

import org.vaadin.teemusa.beandatasource.ContainerDataProvider;
import org.vaadin.teemusa.beandatasource.client.DataRequestRpc;
import org.vaadin.teemusa.beandatasource.interfaces.CollectionContainer;
import org.vaadin.teemusa.beandatasource.interfaces.CollectionContainer.HasPaging;

import com.vaadin.shared.ui.grid.Range;

public class PagedCollectionDataProvider<T> extends ContainerDataProvider<T> {

	private HasPaging<T> paging;
	private CollectionContainer<T> container;

	public PagedCollectionDataProvider(CollectionContainer<T> container) {
		this(container, new KeyMapper<T>());
	}

	@SuppressWarnings("unchecked")
	public PagedCollectionDataProvider(CollectionContainer<T> container, BeanKeyMapper<T> beanKeyMapper) {
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
			updateSize(container.size());
			pushRowData(0, 40, 0, 0);
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
