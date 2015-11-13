package org.vaadin.teemusa.beandatasource.client;

import com.vaadin.shared.communication.ServerRpc;

public interface DataRequestRpc extends ServerRpc {

	void requestRows(int firstRow, int numberOfRows, int firstCachedRowIndex, int cacheSize);

}
