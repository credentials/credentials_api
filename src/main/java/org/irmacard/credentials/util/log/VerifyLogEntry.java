/**
 * VerifyLogEntry.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, March 2013.
 */

package org.irmacard.credentials.util.log;

import java.util.Date;
import java.util.HashMap;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.VerificationDescription;

public class VerifyLogEntry extends LogEntry {
	private static final long serialVersionUID = 1L;
	private VerificationDescription verification;
	private HashMap<String, Boolean> attributeDisclosed;

	public VerifyLogEntry(Date timestamp, CredentialDescription credential, VerificationDescription verification, HashMap<String, Boolean> attributeDisclosed) {
		super(timestamp, credential);

		this.verification = verification;
		this.attributeDisclosed = attributeDisclosed;
	}

	public VerificationDescription getVerificationDescription() {
		return verification;
	}

	public HashMap<String, Boolean> getAttributesDisclosed() {
		return attributeDisclosed;
	}

	public String toString() {
		String ret = "Verified: " + baseString() + " [";
		for(String attr : attributeDisclosed.keySet()) {
			ret += attr + " (" +  
					(attributeDisclosed.get(attr) ? "revealed" : "unrevealed") + "), ";
		}
		return ret + "]";
	}
}
