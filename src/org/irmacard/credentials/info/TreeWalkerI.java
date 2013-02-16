package org.irmacard.credentials.info;

import java.io.InputStream;
import java.net.URI;

public interface TreeWalkerI {

	/**
	 * This method will walk the configuration and store the results back
	 * into the DescriptionStore 
	 * @throws InfoException
	 */
	public void parseConfiguration(DescriptionStore ds) throws InfoException;

	public InputStream retrieveFile(URI path) throws InfoException;
}
