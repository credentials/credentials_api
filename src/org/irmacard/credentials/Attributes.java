/**
 * Attributes.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Pim Vullers, Radboud University Nijmegen, May 2012,
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, August 2012.
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
	 * @param values the standard set of attributes
	 * @param expiry optional expiry date, if null, default is used.
	 */
	public Date getExpiryDate() {
		byte[] metadata = get(META_DATA_FIELD);
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
		byte[] metadata = attributes.get(META_DATA_FIELD);
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
	 * Test whether the credential containing these attributes is currently still valid.
	 * @return validity
	 */
	public boolean isValid() {
		return isValidOn(Calendar.getInstance().getTime());
	}
}
