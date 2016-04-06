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

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.CredentialIdentifier;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * A generic container class for attributes. Possibly this will just manage
 * attribute id and value pairs. The metadata field however, is handled
 * differently. Pre 0.8 version cards hold only the expiry date in this
 * slot, since 0.8 this has been changed to hold multiple fields.
 */
public class Attributes implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, byte[]> attributes;

	/**
	 * Precision factor for the expiry attribute, 1 means millisecond precision.
	 * Set to one week.
	 */
	public final static long EXPIRY_FACTOR = 1000 * 60 * 60 * 24 * 7;
	public final static String META_DATA_FIELD = "metadata";
	public final static int META_DATA_LENGTH; // Computed below
	public final static byte CURRENT_VERSION = (byte) 0x02;
	public final static int FIELD_COUNT = Field.values().length;

	/** The metadata fields. The order in which they appear here defines the order in which
	 *  they are stored in the metadata attribute. */
	private enum Field {
		VERSION(1),
		SIGNING_DATE(3),
		EXPIRY(2),
		KEY_COUNTER(2),
		CREDENTIAL_ID(16);

		private int length;
		private int offset;

		private Field(int length) {
			this.length = length;
		}
	}

	static {
		Field[] fields = Field.values();
		int offset = 0;

		fields[0].offset = 0;
		for (int i = 1; i < FIELD_COUNT; ++i) {;
			offset += fields[i-1].length;
			fields[i].offset = offset;
		}

		Field lastField = fields[FIELD_COUNT-1];
		META_DATA_LENGTH = lastField.offset + lastField.length;
	}

	public Attributes() {
		attributes = new HashMap<String, byte[]>();

		// Create meta-data field
		byte[] metadata = new byte[META_DATA_LENGTH];
		add(META_DATA_FIELD, metadata);

		// Set metadata fields to their default values
		setMetadataField(new byte[]{CURRENT_VERSION}, Field.VERSION);
		setSigningDate(null);
		setValidityDuration((short)(52 / 2));
		setKeyCounter(1);
	}

	public Attributes(HashMap<Integer, BigInteger> values) throws IllegalArgumentException {
		if (values.get(1) == null)
			throw new IllegalArgumentException("Metadata attribute was absent but is compulsory");

		attributes = new HashMap<String, byte[]>();
		attributes.put(META_DATA_FIELD, values.get(1).toByteArray());

		List<String> attributeNames = getCredentialDescription().getAttributeNames();
		for (int i = 0; i < attributeNames.size(); ++i) {
			BigInteger attribute = values.get(i+2);
			if (attribute != null)
				attributes.put(attributeNames.get(i), attribute.toByteArray());
		}
	}

	public Attributes(List<BigInteger> values) {
		attributes = new HashMap<String, byte[]>();
		attributes.put(META_DATA_FIELD, values.get(1).toByteArray());

		List<String> attributeNames = getCredentialDescription().getAttributeNames();
		for (int i = 0; i < attributeNames.size(); ++i)
			attributes.put(attributeNames.get(i), values.get(i+2).toByteArray());
	}

	public byte[] getMetadataField(Field field) {
		return Arrays.copyOfRange(attributes.get(META_DATA_FIELD), field.offset, field.offset + field.length);
	}

	public void setMetadataField(byte[] value, Field field) {
		byte[] metadata = attributes.get(META_DATA_FIELD);

		if (value.length > field.length)
			throw new RuntimeException("Metadata field value for " + field
					+ " too long: was " + value.length + ", maximum is " + field.length);

		// Populate the bytes, pushing them as far right as possible within the space allotted to this field
		int emptySpace = field.length - value.length;
		ByteBuffer buffer = ByteBuffer.wrap(metadata);
		buffer.position(field.offset);
		if (emptySpace > 0)
			buffer.put(new byte[emptySpace]);
		buffer.put(value);
		add(META_DATA_FIELD, buffer.array());

//		// Zero the fields first
//		for (int i = 0; i < length; ++i)
//			metadata[offset + i] = (byte) 0x0;
//
//		// Populate the bytes, pushing them as far right as possible within the space allotted to this field
//		int idx;
//		for (int i = 0; i < value.length; i++) {
//			idx = offset + (length - value.length) + i;
//			metadata[idx] = value[i];
//		}
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
			System.out.println(toString());
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

	public short getValidityDuration() {
		return ByteBuffer.wrap(getMetadataField(Field.EXPIRY)).getShort();
	}

	public void setValidityDuration(short numberOfWeeks) {
		setMetadataField(ByteBuffer.allocate(2).putShort(numberOfWeeks).array(), Field.EXPIRY);
	}

	public Date getExpiryDate() {
		Date signing = getSigningDate();
		long duration = getValidityDuration() * EXPIRY_FACTOR;

		return new Date(signing.getTime() + duration);
	}

	public void setExpiryDate(Date expiry) throws IllegalArgumentException {
		long expiryLong = expiry.getTime();
		if (expiryLong % EXPIRY_FACTOR != 0)
			throw new IllegalArgumentException("Expiry date does not match an epoch boundary");

		long signingLong = getSigningDate().getTime();
		short difference = (short)((expiryLong - signingLong) / EXPIRY_FACTOR);

		setMetadataField(ByteBuffer.allocate(2).putShort(difference).array(), Field.EXPIRY);
	}

	public Date getSigningDate() {
		byte[] signingDateBytes = getMetadataField(Field.SIGNING_DATE);
		long signingDateLong = new BigInteger(signingDateBytes).longValue();

		Calendar signingDate = Calendar.getInstance();
		signingDate.setTimeInMillis(signingDateLong * EXPIRY_FACTOR);
		return signingDate.getTime();
	}

	public void setSigningDate(Date date) {
		long value;
		if (date != null)
			value = date.getTime() / EXPIRY_FACTOR;
		else
			value = Calendar.getInstance().getTimeInMillis() / EXPIRY_FACTOR;

		setMetadataField(BigInteger.valueOf(value).toByteArray(), Field.SIGNING_DATE);

//		byte[] bytes = new byte[3];
//		ByteBuffer.allocate(8).putLong(value).get(bytes, 5, 3);
//		setMetadataField(bytes, Field.SIGNING_DATE);
	}

	public short getKeyCounter() {
		return ByteBuffer.wrap(getMetadataField(Field.KEY_COUNTER)).getShort();
	}

	public void setKeyCounter(int value) {
		setMetadataField(ByteBuffer.allocate(2).putShort((short)value).array(), Field.KEY_COUNTER);
	}

	/**
	 * Get the {@link CredentialIdentifier} from the metadata attribute of this instance.
	 * @return Either the identifier, or null if the {@link DescriptionStore} does not contain it.
	 */
	public CredentialIdentifier getCredentialIdentifier() {
		byte[] truncatedHash = getMetadataField(Field.CREDENTIAL_ID);
		try {
			return DescriptionStore.getInstance().hashToCredentialIdentifier(truncatedHash);
		} catch (InfoException e) {
			throw new RuntimeException(e);
		}
	}

	public void setCredentialIdentifier(CredentialIdentifier value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(value.toString().getBytes());
			byte[] truncatedHash = Arrays.copyOfRange(md.digest(), 0, 16);
			setMetadataField(truncatedHash, Field.CREDENTIAL_ID);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 hash algorithm absent");
		}
	}

	public CredentialDescription getCredentialDescription() {
		CredentialIdentifier identifier = getCredentialIdentifier();
		try {
			return DescriptionStore.getInstance().getCredentialDescription(identifier);
		} catch (InfoException e) {
			throw new RuntimeException(e);
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

	public ArrayList<BigInteger> toBigIntegers() throws InfoException {
		List<String> names = getCredentialDescription().getAttributeNames();
		ArrayList<BigInteger> bigints = new ArrayList<>(names.size() + 2);

		// Add metadata field manually as it is not included in .getAttributeNames()
		bigints.add(new BigInteger(get(META_DATA_FIELD)));

		// Add the other attributes
		for (int i = 0; i < names.size(); ++i) {
			byte[] value = get(names.get(i));
			if (value == null)
				throw new InfoException("Attribute " + names.get(i) + " missing");
			bigints.add(new BigInteger(value));
		}

		return bigints;
	}
}
