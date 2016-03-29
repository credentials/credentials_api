/*
 * AttributeIdentifier.java
 *
 * Copyright (c) 2015, Sietse Ringers, Radboud University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the IRMA project nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.credentials.info;

/**
 * Identifies attributes.
 */
@SuppressWarnings("unused")
public class AttributeIdentifier extends ObjectIdentifier {
	private static final long serialVersionUID = 1158661160715464298L;

	private transient IssuerIdentifier issuer;
	private transient CredentialIdentifier credential;

	public AttributeIdentifier(String value) throws IllegalArgumentException {
		super(value);
	}

	public AttributeIdentifier(CredentialIdentifier credential, String name) {
		super(credential + "." + name);
	}

	@Override
	protected int minSize() {
		return 3;
	}

	@Override
	protected int maxSize() {
		return 4;
	}

	public IssuerIdentifier getIssuerIdentifier() {
		if (issuer == null)
			issuer = new IssuerIdentifier(getSchemeManagerName(), getIssuerName());
		return issuer;
	}

	public String getSchemeManagerName() {
		return split()[0];
	}

	public String getIssuerName() {
		return split()[1];
	}

	public CredentialIdentifier getCredentialIdentifier() {
		if (credential == null)
			credential = new CredentialIdentifier(getIssuerIdentifier(), getCredentialName());
		return credential;
	}

	public String getCredentialName() {
		return split()[2];
	}

	public String getAttributeName() {
		if (!isCredential())
			return split()[3];

		return null;
	}

	public boolean isCredential() {
		return split().length == 3;
	}
}
