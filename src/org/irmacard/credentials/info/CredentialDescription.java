/**
 * CredentialDescription.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
 */

package org.irmacard.credentials.info;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CredentialDescription extends ConfigurationParser implements Serializable {
	private static final long serialVersionUID = -8465573145896355885L;
	String description;
	String name;
	String shortName;
	String issuerID;
	String credentialID;
	short id;

	URI path;

	ArrayList<AttributeDescription> attributes;
	private IssuerDescription issuerDescription;
	
	/**
	 * Create new credential description from file
	 * @param file Input XML file.
	 * @throws InfoException
	 */
	public CredentialDescription(URI file) throws InfoException {
		super();
		Document d = parse(file);
		init(d);
	}
	
	/**
	 * Create new credential description from InputStream.
	 * @param stream InputStream for XML file.
	 * @throws InfoException
	 */
	public CredentialDescription(InputStream stream) throws InfoException {
		super();
		Document d = parse(stream);
		init(d);
	}

	private void init(Document d) throws InfoException {
		description = getFirstTagText(d, "Description");
		name = getFirstTagText(d, "Name");
		shortName = getFirstTagText(d, "ShortName");
		issuerID = getFirstTagText(d, "IssuerID");
		credentialID = getFirstTagText(d, "CredentialID");
		id = (short) Integer.parseInt(getFirstTagText(d, "Id"));

		NodeList attrList = ((Element) d.getElementsByTagName("Attributes")
				.item(0)).getElementsByTagName("Attribute");
		attributes = new ArrayList<AttributeDescription>();
		for (int i = 0; i < attrList.getLength(); i++) {
			attributes
					.add(new AttributeDescription((Element) attrList.item(i)));
		}
	}

	/**
	 * FIXME: Nicer string representation would be nice
	 */
	public String toString() {
		return name + "(" + id + "): " + attributes;
	}

	/**
	 * Get the name of the credential. For example this can be
	 * "Studentcard". Note that it is not qualified.
	 * 
	 * @return the name of the credential
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the short name of the credential. This name is short, but displayable,
	 * for example it could be "Student card".
	 * @return
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Get the id of the issuer. For example this can be "Surfnet"
	 * or "RU". This corresponds to the name in the irma_configuration
	 * directory structure.
	 * 
	 * @return the name of the issuer
	 */
	public String getIssuerID() {
		return issuerID;
	}
	
	/**
	 * Get the name of this credential in the directory structure. For example,
	 * this can be "root" or "ageLower". This should be primarily for internal use
	 * to locate files in the directory structure.
	 * @return
	 */
	public String getCredentialID() {
		return credentialID;
	}

	/**
	 * Get a more eleborate description of the credential.
	 * 
	 * @return credential description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the internal identifier used to denote this credential
	 * TODO: make naming consistent with other getID's
	 */
	public short getId() {
		return id; 
	}

	/**
	 * Get the list of attribute names.
	 * 
	 * @return list of attributes names
	 */
	public List<String> getAttributeNames() {
		List<String> ret = new LinkedList<String>();
		for(AttributeDescription a : attributes) {
			ret.add(a.getName());
		}
		return ret;
	}
	
	/**
	 * Get the list of attribute descriptions.
	 * 
	 * @return list of attributes names
	 */
	public List<String> getAttributeDescriptions() {
		List<String> ret = new LinkedList<String>();
		for(AttributeDescription a : attributes) {
			ret.add(a.getDescription());
		}
		return ret;
	}
	
	/**
	 * Get the attribute descriptions themselves
	 * 
	 * @return list of attributes
	 */
	public List<AttributeDescription> getAttributes() {
		return attributes;
	}
	
	/**
	 * Get the issuer description. If necessary it is loaded from the DescriptionStore.
	 * @return the issuer description.
	 */
	public IssuerDescription getIssuerDescription() {
		if(issuerDescription == null) {
			try {
				issuerDescription = DescriptionStore.getInstance().getIssuerDescription(issuerID);
			} catch (InfoException e) {
				// FIXME: for now ignore errors due to missing DescriptionStore
				e.printStackTrace();
			}
		}
		return issuerDescription;
	}
}
