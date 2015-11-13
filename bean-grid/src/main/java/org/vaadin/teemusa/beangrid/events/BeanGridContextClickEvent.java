package org.vaadin.teemusa.beangrid.events;

import org.vaadin.teemusa.beangrid.BeanGrid;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.ui.Grid;

/**
 * ContextClickEvent for the Grid Component.
 * 
 * @since 7.6
 */
public class BeanGridContextClickEvent extends ContextClickEvent {

	private final Object itemId;
	private final int rowIndex;
	private final Object propertyId;
	private final Section section;

	public BeanGridContextClickEvent(BeanGrid<?> source, MouseEventDetails mouseEventDetails, Section section, int rowIndex,
			Object itemId, Object propertyId) {
		super(source, mouseEventDetails);
		this.itemId = itemId;
		this.propertyId = propertyId;
		this.section = section;
		this.rowIndex = rowIndex;
	}

	public Object getItemId() {
		return itemId;
	}

	public Object getPropertyId() {
		return propertyId;
	}

	public Section getSection() {
		return section;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public BeanGrid<?> getComponent() {
		return (BeanGrid<?>) super.getComponent();
	}
}
