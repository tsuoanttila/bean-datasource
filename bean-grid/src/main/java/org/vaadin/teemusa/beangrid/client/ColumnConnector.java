package org.vaadin.teemusa.beangrid.client;

import org.vaadin.teemusa.beangrid.Column;
import org.vaadin.teemusa.beangrid.client.shared.ColumnState;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

@Connect(Column.class)
public class ColumnConnector extends BeanGridExtensionConnector {

	private AbstractRendererConnector<?> rendererConnector = null;

	private final Grid.Column<Object, JsonObject> column = new Grid.Column<Object, JsonObject>() {

		@Override
		public Object getValue(JsonObject row) {
			final JsonObject rowData = row.getObject(GridState.JSONKEY_DATA);

			if (rowData != null && rowData.hasKey(getConnectorId())) {
				final JsonValue columnValue = rowData.get(getConnectorId());

				return rendererConnector != null ? rendererConnector.decode(columnValue) : columnValue;
			}

			return null;
		}
	};

	private Grid<JsonObject> grid;

	@Override
	protected void extend(ServerConnector target) {
		grid = getGrid();
		grid.addColumn(column);
		column.setSortable(false);
		column.setEditable(false);
	}

	public Grid.Column<Object, JsonObject> getColumn() {
		return column;
	}

	@OnStateChange("hidable")
	void updateColumnHidable() {
		column.setHidable(getState().hidable);
	}

	@OnStateChange("caption")
	void updateColumnCaption() {
		column.setHeaderCaption(getState().caption);
	}

	@Override
	public void onUnregister() {
		super.onUnregister();

		grid.removeColumn(column);
	}

	/*
	 * Call this only from renderer connectors.
	 */
	@Deprecated
	public void setRendererConnector(AbstractRendererConnector<?> rendererConnector) {
		this.rendererConnector = rendererConnector;
		column.setRenderer((Renderer<? super Object>) rendererConnector.getRenderer());
	}

	@Override
	public ColumnState getState() {
		return (ColumnState) super.getState();
	}
}
