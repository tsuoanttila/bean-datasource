package org.vaadin.teemusa.beandatasource;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.vaadin.teemusa.TypedComponent;
import org.vaadin.teemusa.beandatasource.client.DataDropRpc;
import org.vaadin.teemusa.beandatasource.client.DataProviderRpc;
import org.vaadin.teemusa.beandatasource.communication.BeanKeyMapper;
import org.vaadin.teemusa.beandatasource.interfaces.DataGenerator;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.JsonCodec;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

public abstract class ContainerDataProvider<T> extends AbstractExtension {

	/**
	 * Class for keeping track of current items and ValueChangeListeners.
	 * 
	 * @since 7.6
	 */
	private class ActiveItemHandler implements Serializable, DataGenerator<T> {

		private final Set<String> activeBeans = new HashSet<String>();
		private final Set<String> droppedBeans = new HashSet<String>();

		/**
		 * Registers ValueChangeListeners for given item ids.
		 * <p>
		 * Note: This method will clean up any unneeded listeners and key
		 * mappings
		 * 
		 * @param itemIds
		 *            collection of new active item ids
		 */
		public void addActiveItems(Iterable<T> beans) {
			for (T bean : beans) {
				if (!activeBeans.contains(bean)) {
					activeBeans.add(getKeyMapper().key(bean));
				}
			}

			// Remove still active rows that were "dropped"
			droppedBeans.removeAll(activeBeans);
			dropBeans(droppedBeans);
			droppedBeans.clear();
		}

		/**
		 * Marks given item id as dropped. Dropped items are cleared when adding
		 * new active items.
		 * 
		 * @param itemId
		 *            dropped item id
		 */
		public void dropActiveItem(T bean) {
			if (activeBeans.contains(bean)) {
				droppedBeans.add(getKeyMapper().key(bean));
			}
		}

		/**
		 * Gets a collection copy of currently active item ids.
		 * 
		 * @return collection of item ids
		 */
		public Collection<T> getActiveBeans() {
			HashSet<T> hashSet = new HashSet<T>();
			for (String key : activeBeans) {
				hashSet.add(getKeyMapper().get(key));
			}
			return hashSet;
		}

		@Override
		public void generateData(T bean, JsonObject rowData) {
			rowData.put(GridState.JSONKEY_ROWKEY,
					JsonCodec.encode(getKeyMapper().key(bean), null, null, null).getEncodedValue());
		}

		@Override
		public void destroyData(T bean) {
			getKeyMapper().remove(bean);
		}
	}

	private final Set<DataGenerator<T>> dataGenerators = new LinkedHashSet<DataGenerator<T>>();
	private final ActiveItemHandler activeItemHandler = new ActiveItemHandler();
	private final BeanKeyMapper<T> keyMapper;
	protected final DataProviderRpc rpc;

	/** Set of updated item ids */
	private final Set<T> updatedItemIds = new LinkedHashSet<T>();
	private boolean refreshCache;

	public ContainerDataProvider(BeanKeyMapper<T> beanKeyMapper) {
		registerRpc(new DataDropRpc() {

			@Override
			public void dropRows(JsonArray rowKeys) {
				for (int i = 0; i < rowKeys.length(); ++i) {
					activeItemHandler.dropActiveItem(getKeyMapper().get(rowKeys.getString(i)));
				}
			}
		});

		keyMapper = beanKeyMapper;
		rpc = getRpcProxy(DataProviderRpc.class);

		addDataGenerator(activeItemHandler);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * RpcDataProviderExtension makes all actual RPC calls from this function
	 * based on changes in the container.
	 */
	@Override
	public void beforeClientResponse(boolean initial) {

		if (refreshCache) {
			updatedItemIds.addAll(activeItemHandler.getActiveBeans());
		}

		internalUpdateRows(updatedItemIds);

		// Clear all changes.
		refreshCache = false;
		updatedItemIds.clear();

		super.beforeClientResponse(initial);
	}

	public void extend(TypedComponent<T> component) {
		if (component instanceof AbstractClientConnector) {
			super.extend((AbstractClientConnector) component);
		} else {
			throw new IllegalArgumentException("Provided typed component is not an AbstractClientConnector.");
		}
	}

	private void dropBeans(Collection<String> droppedBeans) {
		for (String beanKey : droppedBeans) {
			T bean = getKeyMapper().get(beanKey);
			for (DataGenerator<T> g : dataGenerators) {
				g.destroyData(bean);
			}
		}
	}

	public void addDataGenerator(DataGenerator<T> generator) {
		dataGenerators.add(generator);
	}

	public void removeDataGenerator(DataGenerator<T> generator) {
		dataGenerators.remove(generator);
	}

	protected JsonObject getRowData(T bean) {
		final JsonObject rowObject = Json.createObject();
		for (DataGenerator<T> dg : dataGenerators) {
			dg.generateData(bean, rowObject);
		}

		return rowObject;
	}

	public BeanKeyMapper<T> getKeyMapper() {
		return keyMapper;
	}

	protected void pushRows(long l, Iterable<T> beans) {

		JsonArray rows = Json.createArray();

		int i = 0;
		for (T bean : beans) {
			rows.set(i++, getRowData(bean));
		}

		rpc.setRowData(l, rows);
		activeItemHandler.addActiveItems(beans);
	}

	protected void internalUpdateRows(Set<T> beans) {
		if (beans.isEmpty()) {
			return;
		}

		for (T bean : beans) {
			JsonObject row = getRowData(bean);
			rpc.updateRow(row);
		}
	}

	/**
	 * Informs the client side that data of a row has been modified in the data
	 * source.
	 * 
	 * @param bean
	 *            the bean for updated row
	 */
	public void updateRowData(T bean) {
		if (updatedItemIds.isEmpty()) {
			// At least one new item will be updated. Mark as dirty to actually
			// update before response to client.
			markAsDirty();
		}

		updatedItemIds.add(bean);
	}

	public void refreshCache() {
		if (!refreshCache) {
			refreshCache = true;
			markAsDirty();
		}
	}

	protected void updateSize(long l) {
		rpc.resetDataAndSize(l);
	}

	public abstract void removeBean(T bean);

	public abstract void addBean(T bean);
}
