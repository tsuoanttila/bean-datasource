package org.vaadin.teemusa.beangrid.client;

import org.vaadin.teemusa.beandatasource.client.HasDataSource;
import org.vaadin.teemusa.beangrid.BeanGrid;
import org.vaadin.teemusa.beangrid.client.shared.BeanGridState;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.SelectionMode;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(BeanGrid.class)
public class BeanGridConnector extends AbstractHasComponentsConnector implements SimpleManagedLayout, HasDataSource {

	@Override
	public Grid<JsonObject> getWidget() {
		return (Grid<JsonObject>) super.getWidget();
	}

	@Override
	public BeanGridState getState() {
		return (BeanGridState) super.getState();
	}

	@Override
	protected void init() {
		super.init();

		getWidget().setSelectionMode(SelectionMode.NONE);
		getWidget().setEditorEnabled(false);
	}

	@OnStateChange("columnReorderingAllowed")
	void updateColumnReorderingAllowed() {
		getWidget().setColumnReorderingAllowed(getState().columnReorderingAllowed);
	}

	@OnStateChange("enabled")
	void updateEnabled() {
		getWidget().setEnabled(getState().enabled);
	}

	@OnStateChange("frozenColumnCount")
	void updateFrozenColumnCount() {
		getWidget().setFrozenColumnCount(getState().frozenColumnCount);
	}

	@Override
	public void updateCaption(ComponentConnector connector) {
	}

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
	}

	@Override
	public void layout() {
		getWidget().onResize();
	}

	@Override
	public void setDataSource(DataSource<JsonObject> rpcDataSource) {
		getWidget().setDataSource(rpcDataSource);
	}
}
