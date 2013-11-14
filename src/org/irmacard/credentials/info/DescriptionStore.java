/**
 * DescriptionStore.java
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * TODO: Change print statements to proper Logging statements
 */
public class DescriptionStore {
	static URI CORE_LOCATION;
	static TreeWalkerI treeWalker;
	
	static DescriptionStore ds;
	
	HashMap<Integer,CredentialDescription> credentialDescriptions = new HashMap<Integer, CredentialDescription>();
	HashMap<String,IssuerDescription> issuerDescriptions = new HashMap<String, IssuerDescription>();
	HashMap<Integer,VerificationDescription> verificationDescriptions = new HashMap<Integer, VerificationDescription>();

	/**
	 * Define the CoreLocation. This has to be set before using the 
	 * DescriptionStore or define a TreeWalker instead.
	 * @param coreLocation Location of configuration files.
	 */
	public static void setCoreLocation(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}
	
	/**
	 * Define the TreeWalker. This allows crawling more difficult storage systems,
	 * like Android's. This has to be set before using the DescriptionStore or define
	 * a coreLocation instead.
	 * @param treeWalker
	 */
	public static void setTreeWalker(TreeWalkerI treeWalker) {
		DescriptionStore.treeWalker = treeWalker;
	}

	/**
	 * Get DescriptionStore instance
	 * 
	 * @return The DescriptionStore instance
	 * @throws Exception if CoreLocation has not been set
	 */
	public static DescriptionStore getInstance() throws InfoException {		
		if(CORE_LOCATION == null && treeWalker == null) {
			// TODO: Improve exception type
			throw new InfoException(
					"Please set CoreLocation before using the DescriptionStore");
		}

		if(ds == null) {
			ds = new DescriptionStore();
		}

		return ds;
	}

	private DescriptionStore() throws InfoException {
		if(CORE_LOCATION != null) {
			treeWalker = new TreeWalker(CORE_LOCATION);
		}

		treeWalker.parseConfiguration(this);
	}
	
	public CredentialDescription getCredentialDescription(short id) {
		return credentialDescriptions.get(new Integer(id));
	}

	public CredentialDescription getCredentialDescriptionByName(String issuer,
			String credID) {
		for (CredentialDescription cd : credentialDescriptions.values()) {
			if (cd.getIssuerID().equals(issuer)
					&& cd.getCredentialID().equals(credID)) {
				return cd;
			}
		}

		return null;
	}

	public VerificationDescription getVerificationDescriptionByName(
			String verifier, String verificationID) {
		for (VerificationDescription vd : verificationDescriptions.values()) {
			if (vd.getVerifierID().equals(verifier)
					&& vd.getVerificationID().equals(verificationID)) {
				return vd;
			}
		}

		System.out.println("Cannot find verifier with this id");
		return null;
	}

	public void addCredentialDescription(CredentialDescription cd) {
		// TODO Add check here to see if ID was already used
		credentialDescriptions.put(new Integer(cd.getId()), cd);
	}
	
	public IssuerDescription getIssuerDescription(String name) {
		// TODO Add check here to see if ID was already used 
		return issuerDescriptions.get(name);
	}
	
	public void addIssuerDescription(IssuerDescription id) {
		// TODO Add check here to see if ID was already used
		issuerDescriptions.put(id.getID(), id);
	}

	public void addVerificationDescription(VerificationDescription vd) {
		// TODO Add check here to see if ID was already used
		verificationDescriptions.put(new Integer(vd.getID()), vd);
	}
	
	public Collection<IssuerDescription> getIssuerDescriptions() {
		return issuerDescriptions.values();
	}
	
	public Collection<VerificationDescription> getVerificationDescriptionsForVerifier(String verifierID) {
		ArrayList<VerificationDescription> result = new ArrayList<VerificationDescription>();
		for (VerificationDescription vd : verificationDescriptions.values()) {
			if (vd.getVerifierID().equals(verifierID)) {
				result.add(vd);
			}
		}
		return result;
	}
	
	public Collection<VerificationDescription> getVerificationDescriptionsForVerifier(IssuerDescription verifier) {
		ArrayList<VerificationDescription> result = new ArrayList<VerificationDescription>();
		for (VerificationDescription vd : verificationDescriptions.values()) {
			if (vd.getVerifierID().equals(verifier.getID())) {
				result.add(vd);
			}
		}
		return result;
	}
}
