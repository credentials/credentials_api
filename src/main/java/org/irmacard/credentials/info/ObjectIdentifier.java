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

package org.irmacard.credentials.info;

import java.io.Serializable;

/**
 * Abstract class to represent identifiers for issuers, credential types or attributes.
 * It has a String representation that can be obtained using {@link #toString()}, and which
 * consists of parts contatenated with dots as separators.
 */
public abstract class ObjectIdentifier implements Serializable {
	private static final long serialVersionUID = 3508658191558056818L;

	protected String identifier;
	transient protected String[] parts;

	public ObjectIdentifier(String identifier) throws IllegalArgumentException {
		if (identifier == null || identifier.length() == 0)
			throw new IllegalArgumentException("Invalid value: can't be null or empty");

		parts = identifier.split("\\.");
		if (parts.length < minSize() || parts.length > maxSize())
			throw new IllegalArgumentException("Invalid value: inappropriate number of parts" +
					" (value: " + identifier + ")");

		this.identifier = identifier;
	}

	public String[] split() {
		if (parts == null)
			parts = identifier.split("\\.");

		return parts;
	}

	/** The minimal amount of parts. */
	protected abstract int minSize();
	/** The maximum amount of parts. */
	protected abstract int maxSize();

	@Override
	public String toString() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ObjectIdentifier))
			return false;

		ObjectIdentifier c = (ObjectIdentifier) o;
		return (this.identifier.equals(c.identifier));
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
}
