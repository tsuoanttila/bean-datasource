package org.vaadin.teemusa.beandatasource.spring;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.vaadin.teemusa.beandatasource.communication.BeanKeyMapper;

import com.vaadin.data.util.AbstractBeanContainer.BeanIdResolver;
import com.vaadin.server.KeyMapper;

@Component
public class SpringBeanKeyMapper<T, I extends Serializable> implements BeanKeyMapper<T> {

	private CrudRepository<T, I> repo;
	private KeyMapper<I> keyMapper = new KeyMapper<I>();
	private BeanIdResolver<I, T> idResolver;

	void setRepository(CrudRepository<T, I> repo) {
		this.repo = repo;
	}

	@Override
	public String key(T bean) {
		return keyMapper.key(getId(bean));
	}

	protected I getId(T bean) {
		return idResolver.getIdForBean(bean);
	}

	@Override
	public T get(String key) {
		try {
			return repo.findOne(keyMapper.get(key));
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void remove(T bean) {
		keyMapper.remove(getId(bean));
	}

	@Override
	public void removeAll() {
		keyMapper.removeAll();
	}

	public void setIdResolver(BeanIdResolver<I, T> idResolver) {
		this.idResolver = idResolver;
	}
}
