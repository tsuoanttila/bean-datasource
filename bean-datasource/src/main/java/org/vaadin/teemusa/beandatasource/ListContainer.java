package org.vaadin.teemusa.beandatasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource.HasPaging;

public class ListContainer<T> implements DataSource<T>, HasPaging<T> {

	protected List<T> repo;

	private DataProvider<T> dataProvider;

	public ListContainer(Collection<T> beans) {
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
	public Iterable<T> getPage(int start, int end) {
		return repo.subList(start, end);
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
