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
}
