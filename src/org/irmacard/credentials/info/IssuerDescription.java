package org.irmacard.credentials.info;

import java.io.InputStream;
import java.net.URI;
import org.w3c.dom.Document;

public class IssuerDescription extends ConfigurationParser {
	String name;
	String id;
	String contactAddress;
	String contactEMail;
	String baseURL;
	
	public String getName() {
		return name;
	}
	
	public String getID() {
		return id;
	}

	public String getContactAddress() {
		return contactAddress;
	}
	
	public String getContactEMail() {
		return contactEMail;
	}

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
