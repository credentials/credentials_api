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

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return name + ": " + description;
	}
}
