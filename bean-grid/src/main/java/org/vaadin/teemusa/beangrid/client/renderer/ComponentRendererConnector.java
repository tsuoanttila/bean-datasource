package org.vaadin.teemusa.beangrid.client.renderer;

import java.util.List;

import org.vaadin.teemusa.beangrid.client.ColumnConnector;
import org.vaadin.teemusa.beangrid.renderer.ComponentRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;

@Connect(ComponentRenderer.class)
public class ComponentRendererConnector extends AbstractRendererConnector<String> {

	public static class ComponentRenderer extends WidgetRenderer<String, SimplePanel> {

		private ColumnConnector parent = null;

		public void setParent(ColumnConnector c) {
			parent = c;
		}

		private ComponentConnector getWidgetConnectorWithId(String data) {
			if (data == null) {
				return null;
			}
			
			List<ServerConnector> children = parent.getParent().getChildren();
			for (ServerConnector c : children) {
				if (c instanceof ComponentConnector && c.getConnectorId().equals(data)) {
					return ((ComponentConnector) c);
				}
			}
			return null;
		}

		@Override
		public SimplePanel createWidget() {
			SimplePanel panel = GWT.create(SimplePanel.class);

			panel.setHeight("100%");
			panel.setWidth("100%");
			panel.getElement().getStyle().setProperty("box-sizing", "border-box");

			return panel;
		}

		@Override
		public void render(RendererCellReference cell, String connId, SimplePanel panel) {
			ComponentConnector connector = getWidgetConnectorWithId(connId);
			Widget widget = connector != null ? connector.getWidget() : null;
			Widget old = panel.getWidget();
			if (old != widget) {
				if (old != null) {
					panel.remove(old);
				}
				if (widget != null) {
					panel.add(widget);
				}
			}
		}
	}

	@Override
	public ComponentRenderer getRenderer() {
		return (ComponentRenderer) super.getRenderer();
	}

	@Override
	protected void extend(ServerConnector target) {
		super.extend(target);
		getParent().setRendererConnector(this);
		getRenderer().setParent(getParent());
	}

	@Override
	public ColumnConnector getParent() {
		return (ColumnConnector) super.getParent();
	}

}
