package org.vaadin.teemusa.beandatasource.spring;

import java.io.Serializable;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.DataProvider;
import org.vaadin.teemusa.beandatasource.interfaces.DataSource;

import com.vaadin.data.util.AbstractBeanContainer.BeanIdResolver;

@Component
public abstract class SpringDataSource<T, I extends Serializable, R extends CrudRepository<T, I>>
		implements DataSource<T> {

	@Autowired
	protected R repo;

	@Autowired
	private SpringBeanKeyMapper<T, I> keyMapper;

	private DataProvider<T> dataProvider;

	public SpringDataSource() {
	}

	public void setIdResolver(BeanIdResolver<I, T> resolver) {
		keyMapper.setIdResolver(resolver);
	}

	@Override
	public long size() {
		return repo.count();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(T o) {
		return repo.exists(getId(o));
	}

	@Override
	public Iterator<T> iterator() {
		return repo.findAll().iterator();
	}

	@Override
	public boolean add(T e) {
		boolean newElement = true;
		if (getId(e) != null && repo.exists(getId(e))) {
			newElement = false;
		}
		repo.save(e);

		if (newElement) {
			dataProvider.addBean(e);
		}

		return newElement;
	}

	@Override
	public boolean remove(T o) {
		if (getId(o) != null && repo.exists(getId(o))) {
			repo.delete(o);
			dataProvider.removeBean(o);
			return true;
		}
		return false;
	}

	protected I getId(T element) {
		return keyMapper.getId(element);
	}

	@Override
	public void clear() {
		repo.deleteAll();
	}

	@Override
	public DataProvider<T> extend(TypedComponent<T> component) {
		if (keyMapper == null) {
			throw new IllegalStateException("No keymapper set!");
		}

		dataProvider = DataProvider.extend(component, this, keyMapper);
		return dataProvider;
	}

	@Override
	public void refresh(T e) {
		e = repo.save(e);
		dataProvider.updateRowData(e);
	}
}
