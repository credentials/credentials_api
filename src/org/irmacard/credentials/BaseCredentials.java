/**
 * BaseCredentials.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, July 2012.
 */

package org.irmacard.credentials;

import java.util.List;
import java.util.Vector;

import net.sourceforge.scuba.smartcards.CardService;
import net.sourceforge.scuba.smartcards.CardServiceException;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.util.log.LogEntry;

public abstract class BaseCredentials {
	protected CardService cs = null;

	public BaseCredentials() {
	}

	/**
	 * Create a credential class.
	 *
	 * @param cs The cardservice to use when running the protocols.
	 * @throws CredentialsException
	 */
	public BaseCredentials(CardService cs) {
		this.cs = cs;
	}

	/**
	 * Get a list of credentials available on the card.
	 * 
	 * @return list of credential identifiers.
	 * @throws CardServiceException
	 * @throws InfoException 
	 */
	public List<CredentialDescription> getCredentials() throws CardServiceException, InfoException {
		List<CredentialDescription> list = new Vector<CredentialDescription>();
		
		return list;
	}
	
	/**
	 * Get the transaction log from the card.
	 * 
	 * @return list of log entries from the card.
	 * @throws CardServiceException 
	 * @throws InfoException 
	 */
	public List<LogEntry> getLog() throws CardServiceException, InfoException {
		List<LogEntry> log = new Vector<LogEntry>();
		
		return log;
	}
}
