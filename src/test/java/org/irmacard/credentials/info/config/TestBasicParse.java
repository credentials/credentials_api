package org.irmacard.credentials.info.config;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.VerificationDescription;
import org.junit.Assert;
import org.junit.Test;


public class TestBasicParse {
	URI core = new File(System.getProperty("user.dir")).toURI().resolve(
			"irma_configuration/");
	URI spec = core.resolve(
			"Surfnet/Issues/root/description.xml");
	URI vspec = core.resolve(
			"RU/Verifies/rootID/description.xml");
	
	@Test
	public void parseConfig () throws InfoException {
		CredentialDescription cd = new CredentialDescription(spec);
		System.out.println(cd);
	}

	@Test
	public void parseVerifierConfig() throws InfoException {
		VerificationDescription vd = new VerificationDescription(vspec);
		System.out.println(vd);
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
		if(!cd.getIssuerID().equals("MijnOverheid")) {
			System.out.println(cd.getIssuerID());
			fail("Issuer name incorrect");
		}
	}
	
	@Test
	public void retrieveCredentialDescription() throws InfoException {
		System.out.println("Retrieve credential test");
		CredentialDescription cd = DescriptionStore.getInstance().getCredentialDescription((short) 10);
		System.out.println(DescriptionStore.getInstance().getIssuerDescription("RU"));
		System.out.println(cd.getIssuerDescription());
	}

}
