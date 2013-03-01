/**
 * TreeWalkerI.java
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

import java.io.InputStream;
import java.net.URI;

public interface TreeWalkerI {

	/**
	 * This method will walk the configuration and store the results back
	 * into the DescriptionStore 
	 * @throws InfoException
	 */
	public void parseConfiguration(DescriptionStore ds) throws InfoException;

	/**
	 * Retrieve an InputStream for the given path in the configuration tree. Note that the
	 * path should be relative. The specific TreeWalker implementations should ensure that
	 * this path is properly referenced.
	 * @param path The relative path of the configuration file
	 * @return an InputStream of the configuration file.
	 * @throws InfoException
	 */
	public InputStream retrieveFile(URI path) throws InfoException;
}
