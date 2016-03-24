package org.irmacard.credentials.info;

import java.io.InputStream;

public interface DescriptionStoreSerializer {
	void saveCredentialDescription(CredentialDescription credential, String xml);
	void saveIssuerDescription(IssuerDescription issuer, String xml, InputStream logo);
}
