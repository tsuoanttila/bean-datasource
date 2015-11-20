package org.vaadin.teemusa.beangrid.renderer;

import org.vaadin.teemusa.beangrid.AbstractRenderer;

import com.vaadin.ui.Component;

import elemental.json.Json;
import elemental.json.JsonValue;

public class ComponentRenderer<BEANTYPE> extends AbstractRenderer<BEANTYPE, Component> {

	public ComponentRenderer() {
		super(Component.class);
	}

	@Override
	public JsonValue encode(Component value) {
		return value != null ? Json.create(value.getConnectorId()) : Json.createNull();
	}
}
