package org.irmacard.credentials.info;

import java.io.File;
import java.net.URI;

public class TreeWalker implements TreeWalkerI {
	URI CORE_LOCATION;
	DescriptionStore descriptionStore;
	
	public TreeWalker(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}

	public void parseConfiguration(DescriptionStore descriptionStore)
			throws InfoException {
		this.descriptionStore = descriptionStore;
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
				if((new File(credentialspec)).exists()) {
					CredentialDescription cd = new CredentialDescription(
							credentialspec);
					descriptionStore.addCredentialDescription(cd);
					System.out.println(cd);
				} else {
					System.out
							.println("Expected new form credential description");
				}
			}
		}
	}


}
