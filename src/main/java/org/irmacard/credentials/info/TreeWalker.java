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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class TreeWalker implements TreeWalkerI {
	URI CORE_LOCATION;
	DescriptionStore descriptionStore;
	IssuerDescription currentIssuer;

	// Used for reporting exceptions
	private URI currentFile;

	public TreeWalker(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}

	public InputStream retrieveFile(URI path) throws InfoException {
		try {
			return CORE_LOCATION.resolve(path).toURL().openStream();
		} catch (MalformedURLException e) {
			throw new InfoException("Tried to read file " + path, e);
		} catch (IOException e) {
			throw new InfoException("Tried to read file " + path, e);
		}
	}

	@Override
	public DescriptionStore parseConfiguration()
			throws InfoException {
		descriptionStore = new DescriptionStore();

		File[] files = new File(CORE_LOCATION).listFiles();
		try {
			for (File f : files) {
				if (f.isDirectory()) {
					tryProcessIssuer(f);
				}
			}
		} catch (InfoException e) {
			throw new InfoException("Error processing file: " + currentFile, e);
		}

		return descriptionStore;
	}

	private void tryProcessIssuer(File f) throws InfoException {
		// Determine whether we should process this directory.
		File config = new File(f.toURI().resolve("description.xml"));
		if(config.exists()) {
			currentFile = config.toURI();
			currentIssuer = new IssuerDescription(config.toURI());
			descriptionStore.addIssuerDescription(currentIssuer);

			// Process credentials issued by this issuer
			tryProcessCredentials(f);

			// Process verification descriptions
			tryProcessVerifications(f);
		}
	}

	private void tryProcessCredentials(File f) throws InfoException {
		File credentials = new File(f.toURI().resolve("Issues"));
		if(credentials.exists()) {
			for (File c : credentials.listFiles()) {
				URI credentialspec = c.toURI().resolve("description.xml");
				if((new File(credentialspec)).exists()) {
					currentFile = credentialspec;
					CredentialDescription cd = new CredentialDescription(
							credentialspec);
					descriptionStore.addCredentialDescription(cd);
				} else {
					System.out
							.println("Expected new form credential description");
				}
			}
		}
	}

	private void tryProcessVerifications(File f) throws InfoException {
		File verifications = new File(f.toURI().resolve("Verifies"));
		if(verifications.exists()) {
			for (File v : verifications.listFiles()) {
				URI verification = v.toURI().resolve("description.xml");
				if((new File(verification)).exists()) {
					currentFile = verification;
					VerificationDescription vd = new VerificationDescription(
							verification);
					descriptionStore.addVerificationDescription(vd);
				} else {
					System.out
							.println("Expected new form verification description");
				}
			}
		}
	}
}
