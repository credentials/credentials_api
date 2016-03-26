package org.irmacard.credentials.info;

/**
 * Identifies an issuer.
 */
@SuppressWarnings("unused")
public class IssuerIdentifier extends ObjectIdentifier {
	private static final long serialVersionUID = 3753046666859081598L;

	public IssuerIdentifier(String identifier) throws IllegalArgumentException {
		super(identifier);
	}

	@Override
	protected int maxSize() {
		return 1;
	}

	@Override
	protected int minSize() {
		return 1;
	}

	public String getIssuerName() {
		return identifier;
	}

	public String getSchemeManagerName() {
		return "default";
	}

	/**
	 * Get the (relative) path to storage where the description for this issuer should be stored.
	 * @param includeXml Whether or not to append description.xml
	 */
	public String getPath(boolean includeXml) {
		if (!includeXml)
			return getIssuerName();
		else
			return getIssuerName() + "/description.xml";
	}

	public IssuerDescription getIssuerDescription() {
		try {
			return DescriptionStore.getInstance().getIssuerDescription(this);
		} catch (InfoException e) {
			throw new RuntimeException(e);
		}
	}
}
