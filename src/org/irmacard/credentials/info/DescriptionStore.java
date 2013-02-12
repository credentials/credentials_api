package org.irmacard.credentials.info;

import java.net.URI;
import java.util.HashMap;

/**
 * TODO: Change print statements to proper Logging statements
 */
public class DescriptionStore {
	static URI CORE_LOCATION;
	static DescriptionStore ds;
	
	HashMap<Integer,CredentialDescription> credentialDescriptions = new HashMap<Integer, CredentialDescription>();

	/**
	 * Define the CoreLocation. This has to be set before using the 
	 * DescriptionStore.
	 * @param coreLocation Location of configuration files.
	 */
	public static void setCoreLocation(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}
	
	/**
	 * Get DescriptionStore instance
	 * 
	 * @return The DescriptionStore instance
	 * @throws Exception if CoreLocation has not been set
	 */
	public static DescriptionStore getInstance() throws InfoException {
		if(ds == null) {
			ds = new DescriptionStore();
		}
		
		if(CORE_LOCATION == null) {
			// TODO: Improve exception type
			throw new InfoException(
					"Please set CoreLocation before using the DescriptionStore");
		}
		
		return ds;
	}

	private DescriptionStore() throws InfoException {
		TreeWalker tw = new TreeWalker(CORE_LOCATION, this);
		tw.parseConfiguration();
	}
	
	public CredentialDescription getCredentialDescription(short id) {
		return credentialDescriptions.get(new Integer(id));
	}

	protected void addCredentialDescription(CredentialDescription cd) {
		credentialDescriptions.put(new Integer(cd.getId()), cd);
	}
}
