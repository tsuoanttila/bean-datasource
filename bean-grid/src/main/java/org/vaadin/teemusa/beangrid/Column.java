package org.vaadin.teemusa.beangrid;

import org.vaadin.teemusa.beandatasource.interfaces.DataGenerator;
import org.vaadin.teemusa.beangrid.client.shared.ColumnState;
import org.vaadin.teemusa.beangrid.interfaces.ValueProvider;

import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class Column<BEANTYPE, VALUETYPE> extends BeanGridExtension<BEANTYPE> implements DataGenerator<BEANTYPE> {

	private final ValueProvider<BEANTYPE, VALUETYPE> valueProvider;

	public Column(String caption, ValueProvider<BEANTYPE, VALUETYPE> valueProvider) {
		this.valueProvider = valueProvider;

		getState().caption = caption;
	}

	private Renderer<VALUETYPE> renderer;

	void extend(BeanGrid<BEANTYPE> grid) {
		super.extend(grid);
	}

	@Override
	public void generateData(BEANTYPE bean, JsonObject rowData) {
		if (!rowData.hasKey(GridState.JSONKEY_DATA)) {
			rowData.put(GridState.JSONKEY_DATA, Json.createObject());
		}

		VALUETYPE columnValue = getColumnValue(bean);

		if (renderer == null) {
			System.err.println("No renderer set for column " + getCaption() + ". Generating a toString renderer.");
			setRenderer(new AbstractRenderer<BEANTYPE, VALUETYPE>((Class<VALUETYPE>) columnValue.getClass()) {

				@Override
				public JsonValue encode(VALUETYPE value) {
					return Json.create(value.toString());
				}
			});
		}

		rowData.getObject(GridState.JSONKEY_DATA).put(

				getConnectorId(), renderer.encode(columnValue));
	}

	protected VALUETYPE getColumnValue(BEANTYPE bean) {
		VALUETYPE columnValue = valueProvider.getColumnValue(bean);

		return columnValue;
	}

	@Override
	public void destroyData(BEANTYPE bean) {
	}

	public void setRenderer(AbstractRenderer<BEANTYPE, VALUETYPE> renderer) {
		if (this.renderer != null) {
			this.renderer.remove();
		}
		this.renderer = renderer;
		renderer.extend(this);
	}

	public String getCaption() {
		return getState(false).caption;
	}

	public Column<BEANTYPE, VALUETYPE> setHidable(boolean hidable) {
		getState().hidable = hidable;
		return this;
	}

	public boolean isHidable() {
		return getState(false).hidable;
	}

	@Override
	protected ColumnState getState() {
		return (ColumnState) super.getState();
	}

	@Override
	protected ColumnState getState(boolean markAsDirty) {
		return (ColumnState) super.getState(markAsDirty);
	}
}
