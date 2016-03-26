package org.irmacard.credentials.info;

/**
 * Identifies a credential type.
 */
@SuppressWarnings("unused")
public class CredentialIdentifier extends ObjectIdentifier {
	private static final long serialVersionUID = -7562306005187222195L;

	private transient IssuerIdentifier issuer;

	public CredentialIdentifier(String identifier) throws IllegalArgumentException {
		super(identifier);
	}

	public CredentialIdentifier(IssuerIdentifier issuer, String credential) {
		this(issuer + "." + credential);
	}

	@Override
	protected int minSize() {
		return 2;
	}

	@Override
	protected int maxSize() {
		return 2;
	}

	public IssuerIdentifier getIssuerIdentifier() {
		if (issuer == null)
			issuer = new IssuerIdentifier(getIssuerName());

		return issuer;
	}

	public String getIssuerName() {
		return split()[0];
	}

	public String getCredentialName() {
		return split()[1];
	}

	/**
	 * Get the (relative) path to storage where the description for this credential type should be stored.
	 * @param includeXml Whether or not to append description.xml
	 */
	public String getPath(boolean includeXml) {
		String path = getIssuerIdentifier().getPath(false) + "/Issues/" + getCredentialName();
		if (includeXml)
			return path + "/description.xml";
		else
			return path;
	}

	public CredentialDescription getCredentialDescription() {
		try {
			return DescriptionStore.getInstance().getCredentialDescription(this);
		} catch (InfoException e) {
			throw new RuntimeException(e);
		}
	}
}
