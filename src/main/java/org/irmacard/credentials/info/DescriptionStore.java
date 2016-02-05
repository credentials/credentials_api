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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	static DescriptionStoreSerializer serializer;
	static HttpClient httpClient;

	static DescriptionStore ds;

	HashMap<String,SchemeManager> schemeManagers = new HashMap<>();
	HashMap<String,CredentialDescription> credentialDescriptions = new HashMap<String, CredentialDescription>();
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

	public static void setSerializer(DescriptionStoreSerializer serializer) {
		DescriptionStore.serializer = serializer;
	}

	public static void setHttpClient(HttpClient client) {
		DescriptionStore.httpClient = client;
	}

	/**
	 * Get DescriptionStore instance
	 * 
	 * @return The DescriptionStore instance
	 * @throws InfoException if deserializing the store failed
	 */
	public static DescriptionStore getInstance() throws InfoException {
		if(ds == null) {
			if(CORE_LOCATION != null) {
				treeWalker = new TreeWalker(CORE_LOCATION);
			}

			if (treeWalker != null) {
				ds = treeWalker.parseConfiguration();
			}
		}

		return ds;
	}

	public DescriptionStore() {}

	public static boolean isLocationSet() {
		return CORE_LOCATION != null;
	}
	
	public CredentialDescription getCredentialDescription(short id) {
		for (CredentialDescription cd : credentialDescriptions.values()) {
			if (cd.getId() == id) {
				return cd;
			}
		}

		return null;
	}

	public CredentialDescription getCredentialDescription(String identifier) {
		return credentialDescriptions.get(identifier);
	}

	public CredentialDescription getCredentialDescriptionByName(String issuer, String credID) {
		return getCredentialDescription(issuer + "." + credID);
	}

	public VerificationDescription getVerificationDescriptionByName(
			String verifier, String verificationID) {
		for (VerificationDescription vd : verificationDescriptions.values()) {
			if (vd.getVerifierID().equals(verifier)
					&& vd.getVerificationID().equals(verificationID)) {
				return vd;
			}
		}

		// TODO: error handling? Exception?
		return null;
	}

	public void addCredentialDescription(CredentialDescription cd) throws InfoException {
		if (credentialDescriptions.containsKey(cd.getIdentifier())) {
			throw new InfoException("Cannot add credential " + cd.getIdentifier() + ", already exists");
		}
		credentialDescriptions.put(cd.getIdentifier(), cd);
	}
	
	public IssuerDescription getIssuerDescription(String name) {
		return issuerDescriptions.get(name);
	}

	public void addIssuerDescription(IssuerDescription id) throws InfoException {
		if (issuerDescriptions.containsKey(id.getID())) {
			throw new InfoException("Cannot add issuer " + id.getName()
					+ ". An issuer with the id " + id.getID()
					+ " already exists.");
		}
		issuerDescriptions.put(id.getID(), id);
	}

	public void updateIssuerDescription(IssuerDescription id) {
		if (issuerDescriptions.containsKey(id.getID())) {
			issuerDescriptions.remove(id.getID());
		}
		issuerDescriptions.put(id.getID(), id);
	}

	public void addVerificationDescription(VerificationDescription vd)
			throws InfoException {
		Integer id = new Integer(vd.getID());
		if (verificationDescriptions.containsKey(id)) {
			VerificationDescription other = verificationDescriptions.get(id);
			throw new InfoException("Cannot add verification "
					+ vd.getVerificationID() + " of "
					+ vd.getVerifierID() + ". Verification "
					+ other.getVerificationID() + " of "
					+ other.getVerifierID() + " shares the same id ("
					+ id + ").");
		}
		verificationDescriptions.put(new Integer(vd.getID()), vd);
	}

	public void updateVerificationDescription(VerificationDescription vd)
			throws InfoException {
		Integer id = new Integer(vd.getID());
		if (verificationDescriptions.containsKey(id)) {
			verificationDescriptions.remove(id);
		}
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

	public SchemeManager getSchemeManager(String name) {
		return schemeManagers.get(name);
	}

	public void addSchemeManager(SchemeManager manager) {
		schemeManagers.put(manager.getName(), manager);
	}

	public SchemeManager removeSchemeManager(String name) {
		return schemeManagers.remove(name);
	}

	public CredentialDescription downloadCredentialDescription(String identifier) throws IOException, InfoException {
		return downloadCredentialDescription(identifier, true);
	}

	public CredentialDescription downloadCredentialDescription(String identifier, boolean shouldSave)
	throws IOException, InfoException {
		String[] parts = identifier.split("\\.");
		String issuer = parts[0];
		String credential = parts[1];

		if (ds.getIssuerDescription(issuer) == null)
			downloadIssuerDescription(issuer, false);

		SchemeManager manager = schemeManagers.get("default");
		if (manager == null)
			throw new InfoException("Unknown scheme manager");
		String url = manager.getUrl() + issuer + "/Issues/" + credential + "/description.xml";

		InputStream stream = doHttpRequest(url);

		CredentialDescription cd = new CredentialDescription(stream);
		try {
			ds.addCredentialDescription(cd);
		} catch (InfoException e) { // Thrown if it already exists
			return null;
		}

		if (shouldSave)
			save();

		return cd;
	}

	public IssuerDescription downloadIssuerDescription(String name) throws IOException, InfoException {
		return downloadIssuerDescription(name, true);
	}

	public IssuerDescription downloadIssuerDescription(String name, boolean shouldSave)
	throws IOException, InfoException {
		SchemeManager manager = schemeManagers.get("default");
		if (manager == null)
			throw new InfoException("Unknown scheme manager");
		String url = manager.getUrl() + name + "/description.xml";

		InputStream stream = doHttpRequest(url);

		IssuerDescription issuer = new IssuerDescription(stream);
		try {
			ds.addIssuerDescription(issuer);
		} catch (InfoException e) { // Thrown if it already exists
			return null;
		}

		if (shouldSave)
			save();

		return issuer;
	}

	protected void save() {
		if (serializer != null)
			serializer.saveDescriptionStore(this);
	}

	public static InputStream doHttpRequest(String url) throws IOException {
		HttpResponse response = httpClient.execute(new HttpGet(url));
		int status = response.getStatusLine().getStatusCode();
		if (status == 404)
			return null;

		InputStream stream = response.getEntity().getContent();
		if (status == 200) {
			return stream;
		} else {
			throw new IOException("Server returned "
					+ status + " "
					+ response.getStatusLine().getReasonPhrase() + ":\n"
					+ inputStreamToString(stream));
		}
	}

	private static String inputStreamToString(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");

		br.close();
		is.close();
		return sb.toString();
	}
}
