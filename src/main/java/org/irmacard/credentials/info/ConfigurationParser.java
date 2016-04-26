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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

abstract public class ConfigurationParser {
	private transient DocumentBuilder db;
	private transient int schemaVersion = 1;

	public ConfigurationParser() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setNamespaceAware(true);

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	protected Document parse(URI file) throws InfoException {
		InputStream inputStream;
		try {
			inputStream = file.toURL().openStream();
		} catch (IOException e) {
			throw new InfoException("Cannot read input file " + file.toString() + ".", e);
		}

		try {
			return internalParse(inputStream);
		} catch (SAXException e) {
			throw new InfoException("Parsing configuration file " + file.toString() + " failed.", e);
		} catch (IOException e) {
			throw new InfoException("Cannot read configuration file " + file.toString() + ".", e);
		}
	}

	protected Document parse(InputStream inputStream) throws InfoException{
		try {
			return internalParse(inputStream);
		} catch (SAXException e) {
			throw new InfoException("Parsing configuration file failed.", e);
		} catch (IOException e) {
			throw new InfoException("Cannot read configuration file.", e);
		}
	}

	private Document internalParse(InputStream inputStream) throws SAXException, IOException {
		Document d = db.parse(inputStream);
		String versionAttribute = d.getDocumentElement().getAttribute("version");
		schemaVersion = versionAttribute.length()>0 ? Integer.parseInt(versionAttribute) : 1;
		return d;
	}

	protected String getFirstTagText(Document d, String tag) throws InfoException {
		NodeList all = d.getElementsByTagName(tag);
		if (all.getLength() == 0) {
			throw new InfoException("Expected tag <" + tag + "> is missing.");
		}
		return all.item(0).getTextContent().trim();
	}

	protected int getSchemaVersion() {
		return schemaVersion;
	}
}
