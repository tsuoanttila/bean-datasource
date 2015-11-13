package org.vaadin.teemusa.beangrid;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.teemusa.beangrid.interfaces.ValueProvider;
import org.vaadin.teemusa.beangrid.renderer.ComponentRenderer;

import com.vaadin.ui.Component;

public class ComponentColumn<BEANTYPE> extends Column<BEANTYPE, Component> {

	public ComponentColumn(String caption, ValueProvider<BEANTYPE, Component> valueProvider) {
		super(caption, valueProvider);
		setRenderer(new ComponentRenderer<BEANTYPE>());
	}

	private Map<BEANTYPE, Component> componentMap = new HashMap<BEANTYPE, Component>();

	@Override
	protected Component getColumnValue(BEANTYPE bean) {

		Component columnValue = super.getColumnValue(bean);

		if (componentMap.containsKey(bean)) {
			Component old = componentMap.remove(bean);
			removeComponent(old);
		}

		componentMap.put(bean, (Component) columnValue);
		addComponent((Component) columnValue);

		return columnValue;
	}

	@Override
	public void destroyData(BEANTYPE bean) {
		super.destroyData(bean);

		if (componentMap.containsKey(bean)) {
			removeComponent(componentMap.remove(bean));
		}
	}

}
