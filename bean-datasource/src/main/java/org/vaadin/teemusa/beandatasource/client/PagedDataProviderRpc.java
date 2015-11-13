package org.vaadin.teemusa.beandatasource.client;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

public interface PagedDataProviderRpc extends ClientRpc {

	/**
	 * Informs the client to remove row data.
	 * 
	 * @param firstRowIndex
	 *            the index of the first removed row
	 * @param count
	 *            the number of rows removed from <code>firstRowIndex</code> and
	 *            onwards
	 */
	@NoLayout
	public void removeRowData(int firstRowIndex, int count);

	/**
	 * Informs the client to insert new row data.
	 * 
	 * @param firstRowIndex
	 *            the index of the first new row
	 * @param count
	 *            the number of rows inserted at <code>firstRowIndex</code>
	 */
	@NoLayout
	public void insertRowData(int firstRowIndex, int count);
}
