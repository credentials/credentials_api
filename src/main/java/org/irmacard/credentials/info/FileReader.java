package org.irmacard.credentials.info;

import java.io.InputStream;

public interface FileReader {
	InputStream retrieveFile(String path) throws InfoException;
	String[] list(String path);
	boolean isEmpty(String path);
	boolean containsFile(String path, String filename);
}
