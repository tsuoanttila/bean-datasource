package org.vaadin.teemusa.beandatasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource;

public class CollectionDataSource<T> implements DataSource<T> {

	protected List<T> repo;
	protected DataProvider<T> dataProvider;

	public CollectionDataSource(Collection<T> beans) {
		if (beans instanceof List) {
			repo = (List<T>) beans;
		} else {
			repo = new ArrayList<T>();
			repo.addAll(beans);
		}
	}

	@Override
	public long size() {
		return repo.size();
	}

	@Override
	public DataProvider<T> extend(TypedComponent<T> component) {
		dataProvider = DataProvider.extend(component, this);
		return dataProvider;
	}

	@Override
	public boolean isEmpty() {
		return repo.isEmpty();
	}

	@Override
	public boolean contains(T o) {
		return repo.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return repo.iterator();
	}

	@Override
	public boolean add(T e) {
		return repo.add(e);
	}

	@Override
	public boolean remove(T o) {
		return repo.remove(o);
	}

	@Override
	public void clear() {
		repo.clear();
	}

	@Override
	public void refresh(T e) {
		dataProvider.updateRowData(e);
	}

}
