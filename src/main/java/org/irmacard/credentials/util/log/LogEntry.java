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

package org.irmacard.credentials.util.log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.irmacard.credentials.info.CredentialDescription;

public class LogEntry implements Serializable{
	private static final long serialVersionUID = 1L;
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
		return credential.getIdentifier().toString()
				+ " on " + (new SimpleDateFormat()).format(timestamp);
	}
}
