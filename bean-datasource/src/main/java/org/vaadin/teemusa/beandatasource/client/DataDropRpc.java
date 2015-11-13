package org.vaadin.teemusa.beandatasource.client;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

import elemental.json.JsonArray;

public interface DataDropRpc extends ServerRpc {

	@Delayed
	void dropRows(JsonArray rowKeys);

}
