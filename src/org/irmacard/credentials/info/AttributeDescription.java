/**
 * AttributeDescription.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, February 2013.
 */

package org.irmacard.credentials.info;

import java.io.Serializable;

import org.w3c.dom.Element;

public class AttributeDescription implements Serializable{
	private static final long serialVersionUID = 4609118645897084209L;
	String name;
	String description;

	AttributeDescription(Element e) {
		name = ((Element) e.getElementsByTagName("Name").item(0)).getTextContent();
		description = ((Element) e.getElementsByTagName("Description").item(0)).getTextContent();
	}
	
	/**
	 * FIXME: constructor for testing
	 */
	@Deprecated
	public AttributeDescription() {
		name = "CoolAttr";
		description = "CoolAttr description";
	}

	public AttributeDescription(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return description of the attribute
	 */
	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return name + ": " + description;
	}
}
