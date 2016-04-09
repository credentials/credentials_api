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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

public class BasicFileReader implements FileReader {
	URI coreLocation;

	public BasicFileReader(URI location) {
		this.coreLocation = location;
	}

	@Override
	public InputStream retrieveFile(String path) throws InfoException {
		try {
			return coreLocation.resolve(path).toURL().openStream();
		} catch (IOException e) {
			throw new InfoException("Tried to read file " + path, e);
		}
	}

	@Override
	public String[] list(String path) {
		return new File(coreLocation.resolve(path)).list();
	}

	@Override
	public boolean isEmpty(String path) {
		String[] files = list(path);
		return files == null || files.length == 0;
	}

	@Override
	public boolean containsFile(String path, String filename) {
		String[] files = list(path);
		return files != null && Arrays.asList(files).contains(filename);
	}

	@Override
	public boolean containsFile(String path) {
		String[] parts = path.split("/");
		String filename = parts[parts.length-1];
		path = path.substring(0, path.length() - filename.length() - 1);
		return containsFile(path, filename);
	}
}
