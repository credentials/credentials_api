/**
 * LogEntry.java
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
 * Copyright (C) Pim Vullers, Radboud University Nijmegen, October 2012.
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, March 2013.
 */

package org.irmacard.credentials.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.irmacard.credentials.info.CredentialDescription;

public class LogEntry {
	private Date timestamp;
	private CredentialDescription credential;

	/**
	 * Create a new log entry for a card transaction.
	 * 
	 * @param timestamp at which the transaction occurred.
	 * @param credential the credential that was operated upon
	 */
	public LogEntry(Date timestamp, CredentialDescription credential) {
		this.timestamp = timestamp;
		this.credential = credential;
	}

	/**
	 * Get the date and time at which this transaction occurred.
	 * 
	 * @return timestamp of the tranasction.
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Get the credential involved in this transaction.
	 * 
	 * @return identifier of the credential.
	 */
	public CredentialDescription getCredential() {
		return credential;
	}

	public String toString() {
		return baseString();
	}

	protected String baseString() {
		return credential.getName() + "(" + credential.getId()
				+ ") on " + (new SimpleDateFormat()).format(timestamp);
	}
}
