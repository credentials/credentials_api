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

package org.irmacard.credentials;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A generic container class for attributes. Possibly this will just manage
 * attribute id and value pairs. The metadata field however, is handled
 * differently. Pre 0.8 version cards hold only the expiry date in this
 * slot, since 0.8 this has been changed to hold multiple fields.
 */
public class Attributes implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Precision factor for the expiry attribute, 1 means millisecond precision.
	 */
	public final static long EXPIRY_FACTOR = 1000 * 60 * 60 * 24;

	public final static String META_DATA_FIELD = "metadata";
	public final static byte VERSION = (byte) 0x01;
	public final static int VERSION_OFFSET = 0;
	public final static int VERSION_LENGTH = 1;
	public final static int EXPIRY_OFFSET = 1;
	public final static int EXPIRY_LENGTH = 3;
	public final static int CRED_ID_OFFSET = 4;
	public final static int CRED_ID_LENGTH = 2;
	public final static int META_DATA_LENGTH = VERSION_LENGTH + EXPIRY_LENGTH + CRED_ID_LENGTH;
	public final static int PREVIOUS_META_LENGTH = 2;

	// TODO: provide an implementation for attribute storage.
	private Map<String, byte[]> attributes;
	
	public Attributes() {
		attributes = new HashMap<String, byte[]>();

		// Create meta-data field
		byte[] metadata = new byte[META_DATA_LENGTH];
		metadata[VERSION_OFFSET] = VERSION;
		add(META_DATA_FIELD, metadata);

		// Set expire date meta-field to default value
		setExpireDate(null);
	}
	
	public void add(String id, byte[] value) {
		attributes.put(id, value);
	}
	
	public byte[] get(String id) {
		return attributes.get(id);
	}

	public Set<String> getIdentifiers() {
		return attributes.keySet();
	}

	public void print() {
		for(String k : attributes.keySet() ) {
			System.out.println(k + ": " + new String(get(k)));
		}
	}
	
	public String toString() {
		String res = "[";
		for(String k : attributes.keySet() ) {
			res += k + ": " + new String(get(k)) + ", ";
		}
		res += "]";
		return res;
	}

	/**
	 * Get the expiry date of the credential containing these attributes
	 */
	public Date getExpiryDate() {
		return extractExpiryDate(get(META_DATA_FIELD));
	}

	/**
	 * Interpret the specified BigInteger as a metadata attribute and extract the validity date.
	 */
	public static Date extractExpiryDate(BigInteger metadata) {
		return extractExpiryDate(metadata.toByteArray());
	}

	/**
	 * Interpret the specified byte array as a metadata attribute and extract the validity date.
	 */
	public static Date extractExpiryDate(byte[] metadata) {
		long expiry = 0;

		if (metadata.length == PREVIOUS_META_LENGTH) {
			expiry = new BigInteger(metadata).longValue();
		} else {
			byte[] expiry_field = Arrays.copyOfRange(metadata,
					EXPIRY_OFFSET, EXPIRY_OFFSET + EXPIRY_LENGTH);
			expiry = new BigInteger(expiry_field).longValue();
		}

		Calendar expires = Calendar.getInstance();
		expires.setTimeInMillis(expiry * EXPIRY_FACTOR);
		return expires.getTime();
	}

	/**
	 * Set the expiry meta-field. This field is mandatory,
	 * if no Date is specified (i.e. expiry == null) then the default expiry time
	 * of six monts is used. Note that the granularity of the expiry time is set
	 * by EXPIRY_FACTOR.
	 * @param expiry optional expiry date, if null, default is used.
	 */
	public void setExpireDate(Date expiry) {
		Calendar expires = Calendar.getInstance();
		if (expiry != null) {
			expires.setTime(expiry);
		} else {
			expires.add(Calendar.MONTH, 6);
		}

		byte[] expiry_field = BigInteger.valueOf(
				expires.getTimeInMillis() / EXPIRY_FACTOR).toByteArray();
		byte[] metadata = attributes.get(META_DATA_FIELD);

		// Zero the fields first
		for (int i = 0; i < EXPIRY_LENGTH; i++) {
			metadata[EXPIRY_OFFSET + i] = (byte) 0x0;
		}

		// Offset the data, so recovery works
		for (int i = 0; i < expiry_field.length; i++) {
			int idx = EXPIRY_OFFSET + (EXPIRY_LENGTH - expiry_field.length) + i;
			metadata[idx] = expiry_field[i];
		}

		add("metadata", metadata);
	}

	/**
	 * Sets the credential id meta-field. This field is mandatory.
	 * @param id
	 */
	public void setCredentialID(short id) {
		byte[] metadata = attributes.get(META_DATA_FIELD);
		metadata[CRED_ID_OFFSET] = (byte) (id >> 8);
		metadata[CRED_ID_OFFSET + 1] = (byte) (id & 0xff);
		add("metadata", metadata);
	}

	/**
	 * Gets the credential id meta-field.
	 */
	public short getCredentialID() {
		return extractCredentialId(attributes.get(META_DATA_FIELD));
	}

	/**
	 * Interpret the specified BigInteger as a metadata attribute and extract the credential ID.
	 */
	public static short extractCredentialId(BigInteger metadata) {
		return extractCredentialId(metadata.toByteArray());
	}

	/**
	 * Interpret the specified byte array as a metadata attribute and extract the credential ID.
	 */
	public static short extractCredentialId(byte[] metadata) {
		if (metadata.length > PREVIOUS_META_LENGTH) {
			return (short) (((metadata[CRED_ID_OFFSET] & 0xff) << 8) |
					(metadata[CRED_ID_OFFSET + 1] & 0xff));
		} else {
			// Not available in old credentials
			return 0;
		}
	}

	/**
	 * Test whether the the credential containing these attributes is still valid
	 * on the given date.
	 * @return validity
	 */
	public boolean isValidOn(Date date) {
		return date.before(getExpiryDate());
	}

	/**
	 * Test whether a credential containing the specified metadata attribute would be valid on the given date.
	 * @param date Date to test
	 * @param metadata Metadata attribute
	 * @return validity
	 */
	public static boolean isValidOn(Date date, BigInteger metadata) {
		return date.before(extractExpiryDate(metadata));
	}

	/**
	 * Test whether a credential containing the specified metadata attribute would be valid on the given date.
	 * @param date Date to test
	 * @param metadata Metadata attribute
	 * @return validity
	 */
	public static boolean isValidOn(Date date, byte[] metadata) {
		return date.before(extractExpiryDate(metadata));
	}

	/**
	 * Test whether the credential containing these attributes is currently still valid.
	 * @return validity
	 */
	public boolean isValid() {
		return isValidOn(Calendar.getInstance().getTime());
	}

	/**
	 * Test whether a credential containing the specified metadata attribute would currently be valid.
	 * @param metadata Metadata attribute
	 * @return validity
	 */
	public static boolean isValid(BigInteger metadata) {
		return isValidOn(Calendar.getInstance().getTime(), metadata);
	}

	/**
	 * Test whether a credential containing the specified metadata attribute would currently be valid.
	 * @param metadata Metadata attribute
	 * @return validity
	 */
	public static boolean isValid(byte[] metadata) {
		return isValidOn(Calendar.getInstance().getTime(), metadata);
	}
}
