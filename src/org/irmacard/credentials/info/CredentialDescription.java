package org.irmacard.credentials.info;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CredentialDescription extends ConfigurationParser {
	String description;
	String name;
	String issuerName;
	short id;

	URI path;

	List<AttributeDescription> attributes;
	
	/**
	 * FIXME: Constructor only for testing purposes
	 */
	@Deprecated
	public CredentialDescription(short id) throws InfoException{
		description = "A Description";
		name = "CoolName";
		issuerName = "CoolIssuer";
		this.id = id;
		
		attributes = new LinkedList<AttributeDescription>();
		attributes.add(new AttributeDescription());
	}

	public CredentialDescription(URI file) throws InfoException {
		super();
		Document d = parse(file);
		
		description = getFirstTagText(d, "Description");
		name = getFirstTagText(d, "Name");
		issuerName = getFirstTagText(d, "IssuerName");
		id = (short) Integer.parseInt(getFirstTagText(d, "Id"));
		
		NodeList attrList = ((Element) d.getElementsByTagName("Attributes")
				.item(0)).getElementsByTagName("Attribute");
		attributes = new LinkedList<AttributeDescription>();
		for (int i = 0; i < attrList.getLength(); i++) {
			attributes
					.add(new AttributeDescription((Element) attrList.item(i)));
		}
		
		path = file.resolve("./");

	}

	/**
	 * Nicer string representation would be nice
	 */
	public String toString() {
		return name + "(" + id + "): " + attributes;
	}

	/**
	 * Get the name of the credential. For example this can be
	 * "Studentcard". Note that it is not qualified.
	 * 
	 * @return the name of the credential
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the name of the issuer. For example this can be "Surfnet"
	 * or "Radboud University". We may need to tweak something in
	 * irma_configuration to fully support full names.
	 * 
	 * @return the name of the issuer
	 */
	public String getIssuerName() {
		return issuerName;
	}

	/**
	 * Get a more eleborate description of the credential.
	 * 
	 * @return credential description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the internal identifier used to denote this credential
	 */
	public short getId() {
		return id; 
	}
	
	/**
	 * Get path for configuration file
	 */
	public URI getPath() {
		return path;
	}

	/**
	 * Get the list of attribute names.
	 * 
	 * @return list of attributes names
	 */
	public List<String> getAttributeNames() {
		List<String> ret = new LinkedList<String>();
		for(AttributeDescription a : attributes) {
			ret.add(a.getName());
		}
		return ret;
	}
	
	/**
	 * Get the list of attribute descriptions.
	 * 
	 * @return list of attributes names
	 */
	public List<String> getAttributeDescriptions() {
		List<String> ret = new LinkedList<String>();
		for(AttributeDescription a : attributes) {
			ret.add(a.getDescription());
		}
		return ret;
	}
	
	/**
	 * Get the attribute descriptions themselves
	 * 
	 * @return list of attributes
	 */
	public List<AttributeDescription> getAttributes() {
		return attributes;
	}
}
