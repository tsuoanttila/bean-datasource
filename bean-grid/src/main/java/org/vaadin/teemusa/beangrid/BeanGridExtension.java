package org.vaadin.teemusa.beangrid;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Component;

/**
 * Abstract base class to be used with Extensions for BeanGrid.
 * 
 * @param <T>
 *            bean type
 */
public abstract class BeanGridExtension<T> extends AbstractExtension {

    public BeanGridExtension() {
    }

    public BeanGridExtension(BeanGrid<T> parent) {
        super(parent);
    }

    @Override
    public BeanGrid<T> getParent() {
        return (BeanGrid<T>) super.getParent();
    }

    public void addComponent(Component c) {
        if (c != null) {
            getParent().addExtensionComponent(c);
        }
    }

    public void removeComponent(Component c) {
        if (c != null) {
            getParent().removeExtensionComponent(c);
        }
    }

}
