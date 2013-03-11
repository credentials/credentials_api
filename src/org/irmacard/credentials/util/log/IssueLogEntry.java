/**
 * IssueLogEntry.java
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

import org.irmacard.credentials.info.CredentialDescription;

public class IssueLogEntry extends LogEntry {
	private static final long serialVersionUID = 1L;

	public IssueLogEntry(Date timestamp, CredentialDescription credential) {
		super(timestamp, credential);
	}

	public String toString() {
		return "Issued:   " + baseString();
	}
}
