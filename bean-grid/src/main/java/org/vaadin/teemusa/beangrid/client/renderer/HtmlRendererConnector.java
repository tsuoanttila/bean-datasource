package org.vaadin.teemusa.beangrid.client.renderer;

import org.vaadin.teemusa.beangrid.client.ColumnConnector;
import org.vaadin.teemusa.beangrid.renderer.HtmlRenderer;

import com.vaadin.client.ServerConnector;
import com.vaadin.shared.ui.Connect;

@Connect(HtmlRenderer.class)
public class HtmlRendererConnector extends com.vaadin.client.connectors.UnsafeHtmlRendererConnector {

	@Override
	protected void extend(ServerConnector target) {
		super.extend(target);
		getParent().setRendererConnector(this);
	}

	@Override
	public ColumnConnector getParent() {
		return (ColumnConnector) super.getParent();
	}

}
