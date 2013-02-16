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

	public InfoException(Exception inner, String s) {
		super(s);
		this.inner = inner;
	}
	
	public InfoException(String s) {
		super(s);
		this.inner = null;
	}
	
	public Exception getInner() {
		return inner;
	}
}
