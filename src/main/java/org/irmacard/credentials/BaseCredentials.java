/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.credentials;

import java.util.List;
import java.util.Vector;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

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
