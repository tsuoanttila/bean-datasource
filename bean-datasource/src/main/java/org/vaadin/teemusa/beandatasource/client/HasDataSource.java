package org.vaadin.teemusa.beandatasource.client;

import com.vaadin.client.data.DataSource;

import elemental.json.JsonObject;

public interface HasDataSource {

	void setDataSource(DataSource<JsonObject> rpcDataSource);

}
