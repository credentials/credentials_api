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

import org.w3c.dom.Document;

public class IssuerDescription extends ConfigurationParser implements Serializable {
	private static final long serialVersionUID = 1640325096236188409L;
	private String name;
	private String id;
	private String contactAddress;
	private String contactEMail;
	private String baseURL;
	private String schemeManager;
	private IssuerIdentifier identifier;
	
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

	public IssuerIdentifier getIdentifier() {
		return identifier;
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
	 * TODO: Remove when ready
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

	public IssuerDescription(String xml) throws InfoException {
		this(new ByteArrayInputStream(xml.getBytes()));
	}

	private void init(Document d) throws InfoException {
		name = getFirstTagText(d, "Name");
		id = getFirstTagText(d, "ID");
		contactAddress = getFirstTagText(d, "ContactAddress");
		contactEMail = getFirstTagText(d, "ContactEMail");
		baseURL = getFirstTagText(d, "baseURL");
		schemeManager = getFirstTagText(d, "SchemeManager");
		identifier = new IssuerIdentifier(schemeManager, id);
	}

	public String toString() {
		return name + ": " + baseURL + " (" + contactAddress + ", " + contactEMail + ")";
	}
}
