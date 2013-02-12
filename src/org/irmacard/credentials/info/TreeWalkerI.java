package org.irmacard.credentials.info;

public interface TreeWalkerI {

	/**
	 * This method will walk the configuration and store the results back
	 * into the DescriptionStore 
	 * @throws InfoException
	 */
	public void parseConfiguration(DescriptionStore ds) throws InfoException;

}
