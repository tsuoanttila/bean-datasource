package org.vaadin.teemusa.beangrid.interfaces;

public interface ValueProvider<BEANTYPE, VALUETYPE> {

    public VALUETYPE getColumnValue(BEANTYPE bean);

}
