package org.vaadin.teemusa.beandatasource;

import java.util.Collection;

import org.vaadin.teemusa.beandatasource.interfaces.DataSource.HasPaging;

public class ListDataSource<T> extends CollectionDataSource<T> implements HasPaging<T> {

	private DataProvider<T> dataProvider;

	public ListDataSource(Collection<T> beans) {
		super(beans);
	}

	@Override
	public Iterable<T> getPage(int start, int end) {
		return repo.subList(start, end);
	}

}
