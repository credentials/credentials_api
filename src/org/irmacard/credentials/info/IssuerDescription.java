/**
 * IssuerDescription.java
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
import org.w3c.dom.Document;

public class IssuerDescription extends ConfigurationParser implements Serializable {
	private static final long serialVersionUID = 1640325096236188409L;
	String name;
	String id;
	String contactAddress;
	String contactEMail;
	String baseURL;
	
	/**
	 * Full human readable name of the issuer. For example this could be "Radboud University".
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get issuer ID, corresponding to the directory name in the configuration structures.
	 * @return
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return Contact address of the issuer
	 */
	public String getContactAddress() {
		return contactAddress;
	}
	
	/**
	 * @return Contact email of the issuer
	 */
	public String getContactEMail() {
		return contactEMail;
	}

	/**
	 * In the Idemix-library system parameters are identified using a base URL together
	 * with a filename to specify the specific parameter types. This function returns this
	 * base URL.
	 * @return The base URL for this issuer
	 */
	public String getBaseURL() {
		return baseURL;
	}

	public IssuerDescription(URI file) throws InfoException {
		super();
		Document d = parse(file);
		init(d);
	}
	
	public IssuerDescription(InputStream stream) throws InfoException {
		super();
		Document d = parse(stream);
		init(d);
	}
	
	private void init(Document d) {
		name = getFirstTagText(d, "Name");
		id = getFirstTagText(d, "ID");
		contactAddress = getFirstTagText(d, "ContactAddress");
		contactEMail = getFirstTagText(d, "ContactEMail");
		baseURL = getFirstTagText(d, "baseURL");
	}
	
	public String toString() {
		return name + ": " + baseURL + " (" + contactAddress + ", " + contactEMail + ")";
	}
}
