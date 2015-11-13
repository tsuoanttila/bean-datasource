package org.vaadin.teemusa.beandatasource.client;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

public interface DataProviderRpc extends ClientRpc {

	@NoLayout
	void updateRow(JsonObject rowArray);

	@NoLayout
	void setRowData(long l, JsonArray rowDataJson);

	@NoLayout
	void dropRow(JsonObject rowArray);

	@NoLayout
	void resetDataAndSize(long l);

}
