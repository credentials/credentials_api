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

	public boolean containsCredentialDescription(CredentialIdentifier identifier) {
		return reader.containsFile(identifier.getPath(false), "description.xml");
	}

	public CredentialDescription loadCredentialDescription(CredentialIdentifier identifier) throws InfoException {
		return new CredentialDescription(reader.retrieveFile(identifier.getPath(true)));
	}

	public boolean containsIssuerDescription(IssuerIdentifier identifier) {
		return reader.containsFile(identifier.getPath(false), "description.xml");
	}

	public IssuerDescription loadIssuerDescription(IssuerIdentifier identifier) throws InfoException {
		return new IssuerDescription(reader.retrieveFile(identifier.getPath(true)));
	}

	public boolean containsVerificationDescription(String issuer, String name) {
		return reader.containsFile(issuer + "/Verifies/" + name, "description.xml");
	}

	public FileReader getFileReader() {
		return reader;
	}
}
