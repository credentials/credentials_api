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

import java.io.InputStream;

public class TreeWalker {
	FileReader fileReader;
	DescriptionStoreDeserializer deserializer;

	public TreeWalker(DescriptionStoreDeserializer deserializer) {
		this.deserializer = deserializer;
		this.fileReader = deserializer.getFileReader();
	}

	public void parseConfiguration(DescriptionStore store) throws InfoException {
		String[] files = fileReader.list("");

		for (String issuerPath : files) {
			if (issuerPath.startsWith(".") || fileReader.isEmpty(issuerPath))
				continue;

			IssuerIdentifier issuer = new IssuerIdentifier(issuerPath);
			if (!deserializer.containsIssuerDescription(issuer))
				continue;

			// Since issuerPath contains description.xml, it is an issuer
			store.addIssuerDescription(deserializer.loadIssuerDescription(issuer));

			tryProcessVerifications(issuerPath, store);

			// Load any credential types it might have
			String[] credentialTypePaths = fileReader.list(issuerPath + "/Issues");
			if (credentialTypePaths == null)
				continue;

			for (String credTypePath : credentialTypePaths) {
				if (credTypePath.startsWith("."))
					continue;
				CredentialIdentifier identifier = new CredentialIdentifier(issuer, credTypePath);
				if (!deserializer.containsCredentialDescription(identifier))
					continue;
				store.addCredentialDescription(deserializer.loadCredentialDescription(identifier));
			}
		}
	}

	private void tryProcessVerifications(String issuerPath, DescriptionStore store) throws InfoException {
		String path = issuerPath + "/Verifies";
		String[] verifications = fileReader.list(path);
		if (verifications == null || verifications.length == 0)
			return;

		for (String verification : verifications) {
			if (!deserializer.containsVerificationDescription(issuerPath, verification))
				continue;
			InputStream stream = fileReader.retrieveFile(path + "/" + verification + "/description.xml");
			store.addVerificationDescription(new VerificationDescription(stream));
		}
	}
}
