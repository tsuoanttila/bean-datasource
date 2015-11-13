package org.vaadin.teemusa.beandatasource.communication;

import java.util.Collections;

import org.vaadin.teemusa.beandatasource.ContainerDataProvider;
import org.vaadin.teemusa.beandatasource.interfaces.CollectionContainer;

public class CollectionDataProvider<T> extends ContainerDataProvider<T> {

	private final CollectionContainer<T> container;
	private boolean shouldPushRows;

	public CollectionDataProvider(CollectionContainer<T> container, BeanKeyMapper<T> keyMapper) {
		super(keyMapper);

		this.container = container;
	}

	public CollectionDataProvider(CollectionContainer<T> container) {
		this(container, new KeyMapper<T>());
	}

	@Override
	public void beforeClientResponse(boolean initial) {
		super.beforeClientResponse(initial);

		if (initial || shouldPushRows) {
			pushRows(0, container);
		}
	}

	@Override
	public void updateSize(long newSize) {
		super.updateSize(newSize);

		shouldPushRows = true;
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
