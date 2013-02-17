package org.irmacard.credentials.info;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

abstract public class ConfigurationParser {
	DocumentBuilder db;

	ConfigurationParser() throws InfoException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setNamespaceAware(true);

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new InfoException(e, "Cannot construct XML parser.");
		}
	}

	protected Document parse(URI file) throws InfoException {
		InputStream inputStream;
		try {
			inputStream = file.toURL().openStream();
		} catch (IOException e) {
			throw new InfoException(e, "Cannot read input file " + file.toString() + ".");
		}
		
		try {
			return internalParse(inputStream);
		} catch (SAXException e) {
			throw new InfoException(e, "Parsing configuration file "
					+ file.toString() + " failed.");
		} catch (IOException e) {
			throw new InfoException(e, "Cannot read configuration file "
					+ file.toString() + ".");
		}
	}

	protected Document parse(InputStream inputStream) throws InfoException{
		try {
			return internalParse(inputStream);
		} catch (SAXException e) {
			throw new InfoException(e, "Parsing configuration file failed.");
		} catch (IOException e) {
			throw new InfoException(e, "Cannot read configuration file.");
		}
	}
	
	private Document internalParse(InputStream inputStream) throws SAXException, IOException {
		return db.parse(inputStream);
	}
	
	protected String getFirstTagText(Document d, String tag) {
		return d.getElementsByTagName(tag).item(0).getTextContent().trim();
	}
}
