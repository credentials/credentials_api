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

package org.irmacard.credentials.info;

/**
 * Identifies a credential type.
 */
@SuppressWarnings("unused")
public class CredentialIdentifier extends ObjectIdentifier {
	private static final long serialVersionUID = -7562306005187222195L;

	private transient IssuerIdentifier issuer;

	public CredentialIdentifier(String identifier) throws IllegalArgumentException {
		super(identifier);
	}

	public CredentialIdentifier(IssuerIdentifier issuer, String credential) throws IllegalArgumentException {
		this(issuer + "." + credential);
	}

	public CredentialIdentifier(String schemeManager, String issuer, String credential)
	throws IllegalArgumentException {
		this(schemeManager + "." + issuer + "." + credential);
	}

	@Override
	protected int minSize() {
		return 3;
	}

	@Override
	protected int maxSize() {
		return 3;
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

	public String getCredentialName() {
		return split()[2];
	}

	/**
	 * Get the (relative) path to storage where the description for this credential type should be stored.
	 * @param includeXml Whether or not to append description.xml
	 */
	public String getPath(boolean includeXml) {
		String path = getIssuerIdentifier().getPath(false) + "/Issues/" + getCredentialName();
		if (includeXml)
			return path + "/description.xml";
		else
			return path;
	}

	public CredentialDescription getCredentialDescription() {
		try {
			return DescriptionStore.getInstance().getCredentialDescription(this);
		} catch (InfoException e) {
			throw new RuntimeException(e);
		}
	}
}
