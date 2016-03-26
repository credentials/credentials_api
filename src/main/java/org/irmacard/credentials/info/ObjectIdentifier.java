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
