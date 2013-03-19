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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A generic container class for attributes. Possibly this will just manage 
 * attribute id and value pairs.
 */
public class Attributes implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Precision factor for the expiry attribute, 1 means millisecond precision.
	 */
	public final static long EXPIRY_FACTOR = 1000 * 60 * 60 * 24;

	// TODO: provide an implementation for attribute storage.
	private Map<String, byte[]> attributes;
	
	public Attributes() {
		attributes = new HashMap<String, byte[]>();

		// Set expiry attribute to default value
		setExpiryAttribute(null);
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
		Calendar expires = Calendar.getInstance();
		expires.setTimeInMillis((new BigInteger(get("expiry"))).longValue()
				* EXPIRY_FACTOR);

		return expires.getTime();
	}

	/**
	 * Set the expiry attribute. This attribute is mandatory,
	 * if no Date is specified (i.e. expiry == null) then the default expiry time
	 * of six monts is used. Note that the granularity of the expiry time is set
	 * by EXPIRY_FACTOR.
	 * @param values the standard set of attributes
	 * @param expiry optional expiry date, if null, default is used.
	 */
	public void setExpiryAttribute(Date expiry) {
		Calendar expires = Calendar.getInstance();
		if (expiry != null) {
			expires.setTime(expiry);
		} else {
			expires.add(Calendar.MONTH, 6);
		}
        add("expiry", BigInteger.valueOf(
        		expires.getTimeInMillis() / EXPIRY_FACTOR).toByteArray());
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
