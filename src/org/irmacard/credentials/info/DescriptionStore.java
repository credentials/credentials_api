package org.irmacard.credentials.info;

import java.net.URI;
import java.util.HashMap;

/**
 * TODO: Change print statements to proper Logging statements
 */
public class DescriptionStore {
	static URI CORE_LOCATION;
	static TreeWalkerI treeWalker;
	
	static DescriptionStore ds;
	
	HashMap<Integer,CredentialDescription> credentialDescriptions = new HashMap<Integer, CredentialDescription>();
	HashMap<String,IssuerDescription> issuerDescriptions = new HashMap<String, IssuerDescription>();

	/**
	 * Define the CoreLocation. This has to be set before using the 
	 * DescriptionStore or define a TreeWalker instead.
	 * @param coreLocation Location of configuration files.
	 */
	public static void setCoreLocation(URI coreLocation) {
		CORE_LOCATION = coreLocation;
	}
	
	/**
	 * Define the TreeWalker. This allows crawling more difficult storage systems,
	 * like Android's. This has to be set before using the DescriptionStore or define
	 * a coreLocation instead.
	 * @param treeWalker
	 */
	public static void setTreeWalker(TreeWalkerI treeWalker) {
		DescriptionStore.treeWalker = treeWalker;
	}

	/**
	 * Get DescriptionStore instance
	 * 
	 * @return The DescriptionStore instance
	 * @throws Exception if CoreLocation has not been set
	 */
	public static DescriptionStore getInstance() throws InfoException {		
		if(CORE_LOCATION == null && treeWalker == null) {
			// TODO: Improve exception type
			throw new InfoException(
					"Please set CoreLocation before using the DescriptionStore");
		}

		if(ds == null) {
			ds = new DescriptionStore();
		}

		return ds;
	}

	private DescriptionStore() throws InfoException {
		if(CORE_LOCATION != null) {
			treeWalker = new TreeWalker(CORE_LOCATION);
		}

		treeWalker.parseConfiguration(this);
	}
	
	public CredentialDescription getCredentialDescription(short id) {
		return credentialDescriptions.get(new Integer(id));
	}

	public void addCredentialDescription(CredentialDescription cd) {
		credentialDescriptions.put(new Integer(cd.getId()), cd);
	}
	
	public IssuerDescription getIssuerDescription(String name) {
		return issuerDescriptions.get(name);
	}
	
	public void addIssuerDescription(IssuerDescription id) {
		issuerDescriptions.put(id.getID(), id);
	}
}
