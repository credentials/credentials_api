package org.irmacard.credentials.info;

import java.io.File;
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
			ds = new DescriptionStore();;
		}
		
		if(CORE_LOCATION == null) {
			// TODO: Improve exception type
			throw new InfoException(
					"Please set CoreLocation before using the DescriptionStore");
		}
		
		return ds;
	}
	
	private void initialPass() throws InfoException {
		File[] files = new File(CORE_LOCATION).listFiles();
		for(File f : files) {
			if(f.isDirectory()) {
				tryProcessIssuer(f);
			}
		}
	}
	
	private void tryProcessIssuer(File f) throws InfoException {
		// Determine whether we should process this directory.
		File config = new File(f.toURI().resolve("description.xml"));
		if(config.exists()) {
			System.out.println("Config found, now processing");
			System.out.println(config);
			
			// TODO: Somehow parse issuer description
			
			// Process credentials issued by this issuer
			tryProcessCredentials(f);
			
			// TODO: Process proof specifications
		}
	}
	
	private void tryProcessCredentials(File f) throws InfoException {
		File credentials = new File(f.toURI().resolve("Issues"));
		if(credentials.exists()) {
			System.out.println("Credentials exists");
			for (File c : credentials.listFiles()) {
				System.out.println("Processing credential: " + c);
				URI credentialspec = c.toURI().resolve("description.xml");
				if(c.exists()) {
					CredentialDescription cd = new CredentialDescription(
							credentialspec);
					credentialDescriptions.put(new Integer(cd.getId()), cd);
					System.out.println(cd);
				} else {
					System.out
							.println("Expected new form credential description");
				}
			}
		}
	}

	private DescriptionStore() throws InfoException {
		initialPass();
	}
	
	CredentialDescription getCredentialDescription(short id) {
		// FIXME for now just return something random
		try {
			return new CredentialDescription(id);
		} catch (InfoException e) {
			return null;
		}
		//return credentialDescriptions.get(id);
	}
}
