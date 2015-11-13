package org.vaadin.teemusa.beangrid;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.JsonCodec;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.JsonValue;

public abstract class AbstractRenderer<BEANTYPE, VALUETYPE>
        extends AbstractExtension implements Renderer<VALUETYPE> {

    private final Class<VALUETYPE> presentationType;

    private final String nullRepresentation;

    protected AbstractRenderer(Class<VALUETYPE> presentationType,
            String nullRepresentation) {
        this.presentationType = presentationType;
        this.nullRepresentation = nullRepresentation;
    }

    protected AbstractRenderer(Class<VALUETYPE> presentationType) {
        this(presentationType, null);
    }

    @Override
    public Class<VALUETYPE> getPresentationType() {
        return presentationType;
    }

    @Override
    public JsonValue encode(VALUETYPE value) {
        if (value == null) {
            return encode(getNullRepresentation(), String.class);
        } else {
            return encode(value, getPresentationType());
        }
    }

    protected <U> JsonValue encode(U value, Class<U> type) {
        return JsonCodec
                .encode(value, null, type, getUI().getConnectorTracker())
                .getEncodedValue();
    }

    void extend(Column<BEANTYPE, VALUETYPE> column) {
        super.extend(column);
    }

    protected String getNullRepresentation() {
        return nullRepresentation;
    }

    @Override
    public Column<BEANTYPE, VALUETYPE> getParent() {
        return (Column<BEANTYPE, VALUETYPE>) super.getParent();
    }

    protected BEANTYPE getBean(String rowKey) {
        return getParent().getParent().containerDataProvider.getKeyMapper()
                .get(rowKey);
    }
}
