package org.vaadin.teemusa.beandatasource.communication;

public interface BeanKeyMapper<T> {

	String key(T bean);

	T get(String key);

	void remove(T bean);

	void removeAll();
}
