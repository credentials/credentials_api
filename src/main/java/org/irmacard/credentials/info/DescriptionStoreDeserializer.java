package org.irmacard.credentials.info;

import java.net.URI;

@SuppressWarnings("unused")
public class DescriptionStoreDeserializer {
	private FileReader reader;

	public DescriptionStoreDeserializer(URI coreLocation) {
		this.reader = new BasicFileReader(coreLocation);
	}

	public DescriptionStoreDeserializer(FileReader reader) {
		this.reader = reader;
	}

	public boolean containsCredentialDescription(String identifier) {
		// TODO move to a credential identifier class
		String[] parts = identifier.split("\\.");
		String issuer = parts[0];
		String credential = parts[1];

		return reader.containsFile(issuer + "/Issues/" + credential, "description.xml");
	}

	public CredentialDescription loadCredentialDescription(String identifier) throws InfoException {
		String[] parts = identifier.split("\\.");
		String issuer = parts[0];
		String credential = parts[1];
		String path = issuer + "/Issues/" + credential + "/description.xml";

		return new CredentialDescription(reader.retrieveFile(path));
	}

	public boolean containsIssuerDescription(String name) {
		return reader.containsFile(name, "/description.xml");
	}

	public IssuerDescription loadIssuerDescription(String name) throws InfoException {
		String path = name + "/description.xml";
		return new IssuerDescription(reader.retrieveFile(path));
	}

	public boolean containsVerificationDescription(String issuer, String name) {
		return reader.containsFile(issuer + "/Verifies/" + name, "description.xml");
	}

	public FileReader getFileReader() {
		return reader;
	}
}
