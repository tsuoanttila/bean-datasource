package org.vaadin.teemusa.beangrid;

import org.vaadin.teemusa.beangrid.interfaces.ValueProvider;
import org.vaadin.teemusa.beangrid.renderer.HtmlRenderer;

public class HtmlColumn<BEANTYPE> extends Column<BEANTYPE, String> {

	public HtmlColumn(String caption, ValueProvider<BEANTYPE, String> valueProvider) {
		super(caption, valueProvider);
		setRenderer(new HtmlRenderer<BEANTYPE>());
	}

}
