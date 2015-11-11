/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*************************************************************************
 *                                                                       *
 *  CERT-CVC: EAC 1.11 Card Verifiable Certificate Library               * 
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.irmacard.credentials.cert;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.ejbca.cvc.AlgorithmUtil;
import org.ejbca.cvc.ByteField;
import org.ejbca.cvc.CVCTagEnum;
import org.ejbca.cvc.CVCertificate;
import org.ejbca.cvc.CVCertificateBody;
import org.ejbca.cvc.OIDField;
import org.ejbca.cvc.exception.ConstructionException;
import org.ejbca.cvc.util.BCECUtil;

/**
 * Represents a Card Verifiable Certificate according to the specification for EAC 1.11.
 * 
 * @author Keijo Kurkinen, Swedish National Police Board
 * @version $Id: CVCertificate.java 9077 2010-05-20 10:57:01Z anatom $
 */
public class IRMACertificate extends CVCertificate {

	private static final long serialVersionUID = 4092176253549374785L;

	private static CVCTagEnum[] allowedFields = new CVCTagEnum[] {
		CVCTagEnum.CERTIFICATE_BODY, 
		CVCTagEnum.SIGNATURE 
	};

	protected CVCTagEnum[] getAllowedFields() {
		return allowedFields;
	}

	/**
	 * Creates an instance from a CVCertificateBody
	 * @param body
	 * @throws IllegalArgumentException if the argument is null
	 */
	public IRMACertificate(IRMACertificateBody body) throws ConstructionException {
		super();

		if (body == null) {
			throw new IllegalArgumentException("body is null");
		}
		addSubfield(body);
	}

	/**
	 * Adds signature data
	 * @param signatureData
	 * @throws ConstructionException
	 */
	public void setSignature(byte[] signatureData) throws ConstructionException {
		addSubfield(new ByteField(CVCTagEnum.SIGNATURE, signatureData));
	}

	/**
	 * Returns the embedded CertificateBody
	 * @return
	 */
	public CVCertificateBody getCertificateBody() throws NoSuchFieldException {
		return (CVCertificateBody)getSubfield(CVCTagEnum.CERTIFICATE_BODY);
	}

	/**
	 * Returns the signature
	 * @return
	 */
	public byte[] getSignature() throws NoSuchFieldException {
		return ((ByteField)getSubfield(CVCTagEnum.SIGNATURE)).getData();
	}

	/**
	 * Returns the data To Be Signed
	 */
	public byte[] getTBS() throws ConstructionException {
		try {
			return getCertificateBody().getDEREncoded();
		} catch( IOException e ){
			throw new ConstructionException(e);
		} catch( NoSuchFieldException e ){
			throw new ConstructionException(e);
		}
	}

	/**
	 * Returns the certificate in text format
	 */
	public String toString() {
		return getAsText("");
	}

	/**
	 * Verifies the signature
	 */
	public void verify(PublicKey key, String provider) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		try {
			// Lookup the OID, the hash-algorithm can be found through it
			OIDField oid = getCertificateBody().getPublicKey().getObjectIdentifier();
			String algorithm = AlgorithmUtil.getAlgorithmName(oid);
			Signature sign = Signature.getInstance(algorithm, provider);

			// Verify the signature
			sign.initVerify(key);
			sign.update(getTBS());
			// Now convert the CVC signature to a X9.62 signature
			byte[] sig = BCECUtil.convertCVCSigToX962(algorithm, getSignature());
			if (!sign.verify(sig)) {
				throw new SignatureException("Signature verification failed!");
			}
		} catch (NoSuchFieldException e) {
			throw new CertificateException("CV-Certificate is corrupt", e);
		} catch (ConstructionException e) {
			throw new CertificateException("CV-Certificate is corrupt", e);
		}
	}
}
