package org.irmacard.credentials.info;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class TranslatedString {
	private Map<String, String> translations = new HashMap<>(2);

	public TranslatedString(Map<String, String> translations) {
		this.translations = translations;
	}

	public TranslatedString(Node xml) {
		for (int i = 0; i < xml.getChildNodes().getLength(); ++i) {
			Node node = xml.getChildNodes().item(i);
			translations.put(node.getNodeName(), node.getTextContent().trim());
		}
	}

	public String getTranslation(String lang) {
		return translations.get(lang);
	}

	@Override
	public String toString() {
		return "TranslatedString{" +
				"en=" + translations.get("en") +
				'}';
	}
}
