package org.vaadin.teemusa.beangrid.client;

import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid;

import elemental.json.JsonObject;

public abstract class BeanGridExtensionConnector extends AbstractExtensionConnector {

	@Override
	public BeanGridConnector getParent() {
		return (BeanGridConnector) super.getParent();
	}

	public Grid<JsonObject> getGrid() {
		return getParent().getWidget();
	}
}
