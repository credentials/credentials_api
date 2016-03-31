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

package org.irmacard.credentials.info.config;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;

import org.irmacard.credentials.info.*;
import org.junit.Test;


public class TestBasicParse {
	static public final String schemeManager = "irma-demo";

	URI core = new File(System.getProperty("user.dir")).toURI().resolve(
			"irma_configuration/");
	URI spec = core.resolve(
			"irma-demo/Surfnet/Issues/root/description.xml");
	
	@Test
	public void parseConfig () throws InfoException {
		CredentialDescription cd = new CredentialDescription(spec);
		System.out.println(cd);
	}

	@Test
	public void initializeDescriptionStore() throws InfoException {
		DescriptionStore.initialize(new DescriptionStoreDeserializer(core));
	}
	
	@Test
	public void retrieveCredentialInfo() throws InfoException {
		CredentialDescription cd = DescriptionStore.getInstance()
				.getCredentialDescriptionByName(schemeManager, "MijnOverheid", "ageLower");
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
		CredentialDescription cd = DescriptionStore.getInstance()
				.getCredentialDescriptionByName(schemeManager, "MijnOverheid", "ageLower");
		System.out.println(DescriptionStore.getInstance().getIssuerDescription(new IssuerIdentifier("irma-demo", "RU")));
		System.out.println(cd.getIssuerDescription());
	}

}
