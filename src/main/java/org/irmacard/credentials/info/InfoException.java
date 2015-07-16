/**
 * InfoException.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
 */

package org.irmacard.credentials.info;

/**
 * Because I got tired of passing around exceptions all the time
 * Probably Pim has a nice fix for this already in place somewhere,
 * but in the mean time, this'll do.
 *
 * @author Wouter Lueks
 *
 */
public class InfoException extends Exception {
	/**
	 * Actual exception that was initially thrown.
	 * */
	Exception inner;

	/**
	 *
	 */
	private static final long serialVersionUID = 1812980213957824660L;

	@Deprecated
	public InfoException(Exception inner, String s) {
		super(s, inner);
		this.inner = inner;
	}

	public InfoException(String s, Exception inner) {
		super(s, inner);
		this.inner = inner;
	}

	public InfoException(String s) {
		super(s);
		this.inner = null;
	}

	@Deprecated
	public Exception getInner() {
		return inner;
	}
}
