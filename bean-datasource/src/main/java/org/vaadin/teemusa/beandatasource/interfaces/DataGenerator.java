/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.teemusa.beandatasource.interfaces;

import java.io.Serializable;

import org.vaadin.teemusa.beandatasource.ContainerDataProvider;

import com.vaadin.ui.Grid.AbstractRenderer;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.JsonObject;

public interface DataGenerator<T> extends Serializable {

	/**
	 * Adds data to row object for given bean being sent to client.
	 * 
	 * @param bean
	 *            bean being sent to client
	 * @param rowData
	 *            row object being sent to client
	 */
	public void generateData(T bean, JsonObject rowData);

	/**
	 * Informs the DataGenerator that given bean has been dropped and is no
	 * longer needed.
	 * 
	 * @param bean
	 *            removed bean
	 */
	public void destroyData(T bean);

}
