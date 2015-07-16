package org.irmacard.credentials.info;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract public class ConfigurationParser {
	// TODO: need to mark this transient to exclude it from GSON serialization
	// which is annoying.
	protected transient DocumentBuilder db;

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
		return db.parse(inputStream);
	}

	protected String getFirstTagText(Document d, String tag) throws InfoException {
		NodeList all = d.getElementsByTagName(tag);
		if (all.getLength() == 0) {
			throw new InfoException("Expected tag <" + tag + "> is missing.");
		}
		return all.item(0).getTextContent().trim();
	}
}
