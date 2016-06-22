/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.credentials.info;

import java.io.ByteArrayInputStream;
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
	private String description;
	private String name;
	private String shortName;
	private String issuerID;
	private String credentialID;
	private String schemeManager;
	private CredentialIdentifier identifier;

	private ArrayList<AttributeDescription> attributes;
	private transient IssuerDescription issuerDescription;
	
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

	public CredentialDescription(String xml) throws InfoException {
		this(new ByteArrayInputStream(xml.getBytes()));
	}

	private void init(Document d) throws InfoException {
		description = getFirstTagText(d, "Description");
		name = getFirstTagText(d, "Name");
		shortName = getFirstTagText(d, "ShortName");
		issuerID = getFirstTagText(d, "IssuerID");
		credentialID = getFirstTagText(d, "CredentialID");
		schemeManager = getFirstTagText(d, "SchemeManager");

		identifier = new CredentialIdentifier(new IssuerIdentifier(schemeManager, issuerID), credentialID);

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
		return identifier.toString() + ": " + attributes;
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
	 * Get the identifier for this credential type.
	 */
	public CredentialIdentifier getIdentifier() {
		return identifier;
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
			issuerDescription = getIdentifier().getIssuerIdentifier().getIssuerDescription();
		}
		return issuerDescription;
	}
}
