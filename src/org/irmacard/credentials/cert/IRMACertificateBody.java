package org.irmacard.credentials.cert;

import java.util.Date;

import org.ejbca.cvc.CAReferenceField;
import org.ejbca.cvc.CVCPublicKey;
import org.ejbca.cvc.CVCTagEnum;
import org.ejbca.cvc.CVCertificateBody;
import org.ejbca.cvc.DateField;
import org.ejbca.cvc.HolderReferenceField;
import org.ejbca.cvc.IntegerField;
import org.ejbca.cvc.exception.ConstructionException;

/**
 * Represents an IRMA CertificateBody
 */
public class IRMACertificateBody extends CVCertificateBody {

	private static final long serialVersionUID = 3239349191144274047L;

	private static CVCTagEnum[] allowedFields = new CVCTagEnum[] {
		CVCTagEnum.PROFILE_IDENTIFIER, 
		CVCTagEnum.CA_REFERENCE,
		CVCTagEnum.PUBLIC_KEY,
		CVCTagEnum.HOLDER_REFERENCE,
		CVCTagEnum.EFFECTIVE_DATE,
		CVCTagEnum.EXPIRATION_DATE
	};

	@Override
	protected CVCTagEnum[] getAllowedFields() {
		return allowedFields;
	}

	/**
	 * Creates an empty instance
	 */
	IRMACertificateBody() {
		super();
	}

	/**
	 * Creates an instance suitable for CertificateRequest
	 * @param authorityReference
	 * @param publicKey
	 * @param holderReference
	 * @throws ConstructionException
	 */
	public IRMACertificateBody(
			CAReferenceField      authorityReference, 
			CVCPublicKey          publicKey, 
			HolderReferenceField  holderReference ) throws ConstructionException {
		this();

		// All arguments must be set, except authorityReference which is
		// optional in requests
		if (publicKey == null) {
			throw new IllegalArgumentException("publicKey is null");
		}
		if (holderReference == null) {
			throw new IllegalArgumentException("holderReference is null");
		}

		// Add subfields
		addSubfield(new IntegerField(CVCTagEnum.PROFILE_IDENTIFIER, CVC_VERSION ));
		addSubfield(authorityReference);

		addSubfield(publicKey);
		addSubfield(holderReference);
	}

	/**
	 * Creates an instance suitable for a CVCertificate
	 * @param authorityReference
	 * @param publicKey
	 * @param holderReference
	 * @param authRole
	 * @param accessRight
	 * @param validFrom
	 * @param validTo
	 */
	public IRMACertificateBody(
			CAReferenceField      authorityReference, 
			CVCPublicKey          publicKey, 
			HolderReferenceField  holderReference, 
			Date                  validFrom,
			Date                  validTo) throws ConstructionException
			{
		this(authorityReference, publicKey, holderReference);

		if (validFrom == null) {
			throw new IllegalArgumentException("validFrom is null");
		}
		if (validTo == null) {
			throw new IllegalArgumentException("validTo is null");
		}

		// Add subfields
		addSubfield(new DateField(CVCTagEnum.EFFECTIVE_DATE,  validFrom));
		addSubfield(new DateField(CVCTagEnum.EXPIRATION_DATE, validTo));
	}

	/**
	 * Returns 'Effective Date' 
	 * @return
	 */
	public Date getValidFrom() throws NoSuchFieldException {
		return ((DateField)getSubfield(CVCTagEnum.EFFECTIVE_DATE)).getDate();
	}

	/**
	 * Returns 'Expiration Date' 
	 * @return
	 */
	public Date getValidTo() throws NoSuchFieldException {
		return ((DateField)getSubfield(CVCTagEnum.EXPIRATION_DATE)).getDate();
	}

	/**
	 * Returns 'Certificate Authority Reference'
	 * Since this field is optional in a CVCRequest this method may return null
	 * @return
	 */
	public CAReferenceField getAuthorityReference() throws NoSuchFieldException {
		return (CAReferenceField)getOptionalSubfield(CVCTagEnum.CA_REFERENCE);
	}

	/**
	 * Returns the public key
	 * @return
	 */
	public CVCPublicKey getPublicKey() throws NoSuchFieldException {
		return (CVCPublicKey)getSubfield(CVCTagEnum.PUBLIC_KEY);
	}

	/**
	 * Returns 'Certificate Holder Reference'
	 * @return
	 */
	public HolderReferenceField getHolderReference() throws NoSuchFieldException {
		return (HolderReferenceField)getSubfield(CVCTagEnum.HOLDER_REFERENCE);
	}
}