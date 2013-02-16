package org.irmacard.credentials.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class TreeWalker implements TreeWalkerI {
	URI CORE_LOCATION;
	DescriptionStore descriptionStore;
	IssuerDescription currentIssuer;
	
	public TreeWalker(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}
	
	public InputStream retrieveFile(URI path) throws InfoException {
		try {
			return CORE_LOCATION.resolve(path).toURL().openStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new InfoException(e, "Tried to read file " + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new InfoException(e, "Tried to read file " + path);
		}
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
			
			currentIssuer = new IssuerDescription(config.toURI());
			System.out.println("Got Issuer: " + currentIssuer);
			descriptionStore.addIssuerDescription(currentIssuer);
			
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
