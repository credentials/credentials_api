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

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;

public class SchemeManager extends ConfigurationParser implements Serializable {
	private static final long serialVersionUID = -6892307557305396448L;

	private transient Document d;
	private String name;
	private String url;
	private String hrName;
	private String description;
	private String contact;

	public SchemeManager(InputStream stream) throws InfoException {
		super();
		Document d = parse(stream);
		init(d);
	}

	public SchemeManager(String xml) throws InfoException {
		this(new ByteArrayInputStream(xml.getBytes()));
	}

	private void init(Document d) throws InfoException {
		this.d = d;
		name = getFirstTagText(d, "Id");
		url = getFirstTagText(d, "Url");

		if (getSchemaVersion() >= 3) {
			hrName = getFirstTagText(d, "Name");
			description = getFirstTagText(d, "Description");
			contact = getFirstTagText(d, "Contact");
		} else {
			hrName = name;
			description = "";
			contact = "";
		}
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	@SuppressWarnings("unused")
	public String getHumanReadableName() {
		return hrName;
	}

	@SuppressWarnings("unused")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("unused")
	public String getContactInfo() {
		return contact;
	}

	public void setUrl(String url) {
		this.url = url;
		d.getElementsByTagName("Url").item(0).setTextContent(url);
	}

	public String getXml() {
		try {
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(4));

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(d.getDocumentElement());

			trans.transform(source, result);
			return sw.toString();
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
