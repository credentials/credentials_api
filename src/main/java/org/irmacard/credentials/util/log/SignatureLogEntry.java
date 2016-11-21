package org.irmacard.credentials.util.log;

import org.irmacard.credentials.info.CredentialDescription;

import java.util.Date;
import java.util.HashMap;

public class SignatureLogEntry extends VerifyLogEntry {
	private String message;

	public SignatureLogEntry(Date timestamp,
	                         CredentialDescription credential,
	                         HashMap<String, Boolean> attributeDisclosed,
	                         String message) {
		super(timestamp, credential, attributeDisclosed);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		String ret = "Signed: " + baseString() + " [";
		for(String attr : getAttributesDisclosed().keySet()) {
			ret += attr + " (" +
					(getAttributesDisclosed().get(attr) ? "revealed" : "unrevealed") + "), ";
		}
		return ret + "], message: \"" + message + "\"";
	}
}
