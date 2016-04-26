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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SuppressWarnings("unused")
public class DescriptionStore {
	public static final int httpTimeout = 5000; // milliseconds

	private static DescriptionStoreDeserializer deserializer;
	private static DescriptionStoreSerializer serializer;
	private static HttpClient httpClient;

	private static DescriptionStore ds;

	private HashMap<String,SchemeManager> schemeManagers = new HashMap<>();
	private HashMap<CredentialIdentifier,CredentialDescription> credentialDescriptions = new HashMap<>();
	private HashMap<IssuerIdentifier,IssuerDescription> issuerDescriptions = new HashMap<>();

	private transient HashMap<String, CredentialIdentifier> reverseHashes = new HashMap<>();

	public static void setDeserializer(DescriptionStoreDeserializer deserializer) {
		DescriptionStore.deserializer = deserializer;
	}

	public static void setSerializer(DescriptionStoreSerializer serializer) {
		DescriptionStore.serializer = serializer;
	}

	public static void setHttpClient(HttpClient client) {
		DescriptionStore.httpClient = client;
	}

	public static void initialize(DescriptionStoreDeserializer deserializer,
	                              DescriptionStoreSerializer serializer,
	                              HttpClient client) throws InfoException {
		DescriptionStore.deserializer = deserializer;
		DescriptionStore.serializer = serializer;
		DescriptionStore.httpClient = client;
		initialize();
	}

	public static void initialize(DescriptionStoreDeserializer deserializer) throws InfoException {
		DescriptionStore.deserializer = deserializer;
		initialize();
	}

	public static void initialize() throws InfoException {
		ds = new DescriptionStore();
		if (deserializer != null)
			new TreeWalker(deserializer).parseConfiguration(ds);
		ds.calculateReverseHashes();
	}

	public static boolean isInitialized() {
		return ds != null;
	}

	MessageDigest md;
	private void calculateReverseHashes() {
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		for (CredentialIdentifier cred : ds.credentialDescriptions.keySet())
			calculateReverseHash(cred);
	}

	private void calculateReverseHash(CredentialIdentifier cred) {
		md.update(cred.toString().getBytes());
		String key = new String(Base64.encodeBase64(Arrays.copyOfRange(md.digest(), 0, 16)));
		reverseHashes.put(key, cred);
	}

	/**
	 * Get DescriptionStore instance
	 * 
	 * @return The DescriptionStore instance
	 * @throws InfoException if deserializing the store failed
	 */
	public static DescriptionStore getInstance() throws InfoException {
		if(ds == null)
			initialize();

		return ds;
	}

	private DescriptionStore() {}

	@Deprecated
	/** Use {@link #getCredentialDescription(CredentialIdentifier)} instead */
	public CredentialDescription getCredentialDescription(short id) {
		for (CredentialDescription cd : credentialDescriptions.values()) {
			if (cd.getId() == id) {
				return cd;
			}
		}

		return null;
	}

	public CredentialDescription getCredentialDescription(CredentialIdentifier identifier) {
		return credentialDescriptions.get(identifier);
	}

	/** Use {@link #getCredentialDescription(CredentialIdentifier)} instead */
	@Deprecated
	public CredentialDescription getCredentialDescription(String identifier) {
		return credentialDescriptions.get(new CredentialIdentifier(identifier));
	}

	public CredentialDescription getCredentialDescriptionByName(IssuerIdentifier issuer, String credID) {
		return getCredentialDescription(new CredentialIdentifier(issuer, credID));
	}

	public CredentialDescription getCredentialDescriptionByName(String schemeManager, String issuer, String credID) {
		return getCredentialDescription(new CredentialIdentifier(schemeManager, issuer, credID));
	}

	public void addCredentialDescription(CredentialDescription cd) throws InfoException {
		if (credentialDescriptions.containsKey(cd.getIdentifier())) {
			throw new InfoException("Cannot add credential " + cd.getIdentifier() + ", already exists");
		}
		credentialDescriptions.put(cd.getIdentifier(), cd);
	}

	public IssuerDescription getIssuerDescription(IssuerIdentifier identifier) {
		return issuerDescriptions.get(identifier);
	}

	@Deprecated
	public IssuerDescription getIssuerDescription(String identifier) {
		return issuerDescriptions.get(new IssuerIdentifier(identifier));
	}

	public void addIssuerDescription(IssuerDescription id) throws InfoException {
		if (issuerDescriptions.containsKey(id.getIdentifier())) {
			throw new InfoException("Cannot add issuer " + id.getName()
					+ ". An issuer with the id " + id.getID()
					+ " already exists.");
		}
		issuerDescriptions.put(id.getIdentifier(), id);
	}

	public void updateIssuerDescription(IssuerDescription id) {
		if (issuerDescriptions.containsKey(id.getIdentifier())) {
			issuerDescriptions.remove(id.getIdentifier());
		}
		issuerDescriptions.put(id.getIdentifier(), id);
	}

	public Collection<IssuerDescription> getIssuerDescriptions() {
		return issuerDescriptions.values();
	}

	public SchemeManager getSchemeManager(String name) {
		return schemeManagers.get(name);
	}

	public boolean containsSchemeManager(String url) {
		for (SchemeManager manager : schemeManagers.values())
			if (manager.getUrl().equals(url))
				return true;

		return false;
	}

	/**
	 * Add a new scheme manager.
	 * @throws InfoException if the manager already exists
	 */
	public void addSchemeManager(SchemeManager manager) throws InfoException {
		if (schemeManagers.containsKey(manager.getName()))
			throw new InfoException("Scheme manager with id " + manager.getName() + " already exists");

		schemeManagers.put(manager.getName(), manager);
	}

	public SchemeManager removeSchemeManager(String name) {
		return schemeManagers.remove(name);
	}

	/**
	 * Get all scheme managers, sorted alphabetically by their name.
	 */
	public ArrayList<SchemeManager> getSchemeManagers() {
		ArrayList<String> names = new ArrayList<>(schemeManagers.keySet());
		Collections.sort(names);

		ArrayList<SchemeManager> managers = new ArrayList<>(names.size());
		for (String name : names)
			managers.add(getSchemeManager(name));

		return managers;
	}

	/**
	 * Download a credential description, and if necessary the corresponding issuer description,
	 * from the scheme manager.
	 * @param identifier The credential type
	 * @return The credential description
	 * @throws IOException if the file could not be downloaded
	 * @throws InfoException if the scheme manager is unknown
	 */
	public CredentialDescription downloadCredentialDescription(CredentialIdentifier identifier)
	throws IOException, InfoException {
		IssuerIdentifier issuer = identifier.getIssuerIdentifier();

		if (getIssuerDescription(issuer) == null)
			downloadIssuerDescription(issuer);

		SchemeManager manager = schemeManagers.get(issuer.getSchemeManagerName());
		if (manager == null)
			throw new InfoException("Unknown scheme manager");
		String url = manager.getUrl() + "/" + issuer.getIssuerName() +
				"/Issues/" + identifier.getCredentialName() + "/description.xml";

		String cdXml = inputStreamToString(doHttpRequest(url));
		CredentialDescription cd = new CredentialDescription(cdXml);
		addCredentialDescription(cd);

		calculateReverseHash(identifier);

		if (serializer != null)
			serializer.saveCredentialDescription(cd, cdXml);

		return cd;
	}

	/**
	 * Download an issuer description from the scheme manager
	 * @param issuer The issuer
	 * @return The issuer deescription
	 * @throws IOException if the files could not be downloaed
	 * @throws InfoException if the scheme manager was unknown
	 */
	public IssuerDescription downloadIssuerDescription(IssuerIdentifier issuer) throws IOException, InfoException {
		SchemeManager manager = schemeManagers.get(issuer.getSchemeManagerName());
		if (manager == null)
			throw new InfoException("Unknown scheme manager");
		String url = manager.getUrl() + "/" + issuer.getIssuerName();

		String issuerXml = inputStreamToString(doHttpRequest(url + "/description.xml"));
		IssuerDescription id = new IssuerDescription(issuerXml);
		addIssuerDescription(id);

		InputStream logo = doHttpRequest(url + "/logo.png");
		if (serializer != null)
			serializer.saveIssuerDescription(id, issuerXml, logo);

		return id;
	}

	public SchemeManager downloadSchemeManager(String url) throws IOException, InfoException {
		// The base url for any manager is always the bare url, without trailing slash or description.xml or both
		if (url.endsWith("/"))
			url = url.substring(0, url.length() - 1);
		if (url.endsWith("/description.xml"))
			url = url.substring(0, url.length() - "/description.xml".length());

		SchemeManager manager = new SchemeManager(DescriptionStore.doHttpRequest(url + "/description.xml"));
		addSchemeManager(manager);

		// FIXME For easier debugger. Remove
		manager.setUrl(url);

		if (serializer != null)
			serializer.saveSchemeManager(manager);

		return manager;
	}

	/**
	 * Do a HTTP request to the server
	 * @param url The url to connect to
	 * @return An inputstream containing the server's response
	 * @throws IOException if the status is not 200
	 */
	public static InputStream doHttpRequest(String url) throws IOException {
		HttpGet get = new HttpGet(url);
		get.setConfig(RequestConfig.custom()
				.setSocketTimeout(httpTimeout)
				.setConnectTimeout(httpTimeout)
				.setConnectionRequestTimeout(httpTimeout)
				.build());

		HttpResponse response = httpClient.execute(get);
		int status = response.getStatusLine().getStatusCode();
		if (status == 404)
			throw new IOException("404: File not found");

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

	public static String inputStreamToString(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");

		br.close();
		is.close();
		return sb.toString();
	}

	public CredentialIdentifier hashToCredentialIdentifier(byte[] hash) {
		return reverseHashes.get(new String(Base64.encodeBase64(hash)));
	}
}
