package org.irmacard.credentials.info;

public class SchemeManager {
	private String name;
	private String url;

	public SchemeManager(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
