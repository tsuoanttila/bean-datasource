package org.vaadin.teemusa.beangrid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.ContainerDataProvider;
import org.vaadin.teemusa.beandatasource.ListContainer;
import org.vaadin.teemusa.beandatasource.interfaces.CollectionContainer;
import org.vaadin.teemusa.beandatasource.interfaces.DataGenerator;
import org.vaadin.teemusa.beangrid.client.shared.BeanGridState;
import org.vaadin.teemusa.beangrid.interfaces.ValueProvider;

import com.vaadin.server.Extension;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

public class BeanGrid<BEANTYPE> extends AbstractComponent implements HasComponents, TypedComponent<BEANTYPE> {

	private CollectionContainer<BEANTYPE> container;
	ContainerDataProvider<BEANTYPE> containerDataProvider;
	private Map<String, Column<BEANTYPE, ?>> columnMap = new LinkedHashMap<String, Column<BEANTYPE, ?>>();

	private final Set<Component> extensionComponents = new HashSet<Component>();

	public BeanGrid(Collection<BEANTYPE> beans) {
		this(new ListContainer<BEANTYPE>(beans));
	}

	public BeanGrid(CollectionContainer<BEANTYPE> container) {
		this.container = container;
		this.containerDataProvider = container.extend(this);
	}

	public Collection<Column<BEANTYPE, ?>> getColumns() {
		return columnMap.values();
	}

	public CollectionContainer<BEANTYPE> getBeanContainer() {
		return container;
	}

	public Column<BEANTYPE, Component> addComponentColumn(String caption,
			ValueProvider<BEANTYPE, Component> valueProvider) {
		Column<BEANTYPE, Component> column = createComponentColumn(caption, valueProvider);

		internalAddColumn(caption, column);

		return column;
	}

	public Column<BEANTYPE, String> addColumn(String caption, ValueProvider<BEANTYPE, String> valueProvider) {

		Column<BEANTYPE, String> column = createColumn(caption, valueProvider);

		internalAddColumn(caption, column);

		return column;
	}

	protected void internalAddColumn(String caption, Column<BEANTYPE, ?> column) {
		if (columnMap.containsKey(caption)) {
			throw new IllegalStateException("Two columns with same caption");
		}
		column.extend(this);
	}

	public Column<BEANTYPE, ?> removeColumn(Column<BEANTYPE, ?> column) {
		return removeColumn(column.getCaption());
	}

	public Column<BEANTYPE, ?> removeColumn(String caption) {
		return columnMap.remove(caption);
	}

	public Column<BEANTYPE, ?> getColumn(String caption) {
		return columnMap.get(caption);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void addExtension(Extension extension) {
		super.addExtension(extension);

		if (extension instanceof DataGenerator) {
			containerDataProvider.addDataGenerator((DataGenerator<BEANTYPE>) extension);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void removeExtension(Extension extension) {
		super.removeExtension(extension);

		if (extension instanceof DataGenerator) {
			containerDataProvider.removeDataGenerator((DataGenerator<BEANTYPE>) extension);
		}
	}

	protected Column<BEANTYPE, String> createColumn(String caption, ValueProvider<BEANTYPE, String> valueProvider) {
		return new HtmlColumn<BEANTYPE>(caption, valueProvider);
	}

	protected Column<BEANTYPE, Component> createComponentColumn(String caption,
			ValueProvider<BEANTYPE, Component> valueProvider) {
		return new ComponentColumn<BEANTYPE>(caption, valueProvider);
	}

	@Override
	protected BeanGridState getState(boolean markAsDirty) {
		return (BeanGridState) super.getState(markAsDirty);
	}

	@Override
	protected BeanGridState getState() {
		return (BeanGridState) super.getState();
	}

	public BeanGrid<BEANTYPE> setColumnReorderingAllowed(boolean allowReorder) {
		getState().columnReorderingAllowed = allowReorder;
		return this;
	}

	public boolean isColumnReorderingAllowed() {
		return getState(false).columnReorderingAllowed;
	}

	public BeanGrid<BEANTYPE> setHeightMode(HeightMode mode) {
		getState().heightMode = mode;
		return this;
	}

	public HeightMode getHeightMode() {
		return getState(false).heightMode;
	}

	public BeanGrid<BEANTYPE> setHeightByRows(double rows) {
		setHeightMode(HeightMode.ROW);
		getState().heightByRows = rows;
		return this;
	}

	public double getHeightByRows() {
		return getState(false).heightByRows;
	}

	public BeanGrid<BEANTYPE> setFrozenColumnCount(int frozenColumns) {
		getState().frozenColumnCount = frozenColumns;
		return this;
	}

	public int getFrozenColumnCount() {
		return getState(false).frozenColumnCount;
	}

	@Override
	public Iterator<Component> iterator() {
		return extensionComponents.iterator();
	}

	void addExtensionComponent(Component c) {
		extensionComponents.add(c);
		c.setParent(this);
		markAsDirty();
	}

	void removeExtensionComponent(Component c) {
		extensionComponents.remove(c);
		c.setParent(this);
		markAsDirty();
	}
}
