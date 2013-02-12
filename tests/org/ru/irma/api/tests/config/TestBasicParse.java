package org.ru.irma.api.tests.config;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.junit.Assert;
import org.junit.Test;


public class TestBasicParse {
	URI core = new File(System.getProperty("user.dir")).toURI().resolve(
			"irma_configuration/");
	URI spec = core.resolve(
			"Surfnet/Issues/root/description.xml");
	
	@Test
	public void parseConfig () throws InfoException {
		CredentialDescription cd = new CredentialDescription(spec);
		System.out.println(cd);
	}
	
	@Test
	public void initializeDescriptionStore() throws InfoException {
		DescriptionStore.setCoreLocation(core);
		DescriptionStore.getInstance();
	}
	
	@Test
	public void retrieveCredentialInfo() throws InfoException {
		CredentialDescription cd = DescriptionStore.getInstance().getCredentialDescription((short) 10);
		if(cd == null) {
			fail("Credential description not found");
		}
		if(!cd.getIssuerName().equals("MijnOverheid")) {
			System.out.println(cd.getIssuerName());
			fail("Issuer name incorrect");
		}
	}

}
