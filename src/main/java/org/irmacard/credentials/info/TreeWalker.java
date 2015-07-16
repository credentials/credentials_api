/**
 * TreeWalker.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
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

	public void parseConfiguration(DescriptionStore descriptionStore)
			throws InfoException {
		this.descriptionStore = descriptionStore;
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
