package org.vaadin.teemusa.beandatasource.interfaces;

import java.util.Iterator;

import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.ContainerDataProvider;

public interface CollectionContainer<T> extends Iterable<T> {

	public interface HasPaging<T> {
		Iterable<T> getPage(int firstIndex, int count);
	}

	ContainerDataProvider<T> extend(TypedComponent<T> component);

	long size();

	boolean isEmpty();

	boolean contains(T e);

	Iterator<T> iterator();

	boolean add(T e);

	boolean remove(T e);

	void clear();

	void refresh(T e);
}
