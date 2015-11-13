package org.vaadin.teemusa.beangrid.client.shared;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.ui.grid.HeightMode;

public class BeanGridState extends AbstractComponentState {

	public boolean columnReorderingAllowed = false;

	/** The height of the Grid in terms of body rows. */
	@DelegateToWidget
	public double heightByRows = 10;

	/** The mode by which Grid defines its height. */
	@DelegateToWidget
	public HeightMode heightMode = HeightMode.CSS;

	public int frozenColumnCount = 0;

}
