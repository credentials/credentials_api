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

package org.irmacard.credentials;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import net.sf.scuba.smartcards.ProtocolCommand;
import net.sf.scuba.smartcards.ProtocolResponses;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.VerificationDescription;
import org.irmacard.credentials.keys.PrivateKey;
import org.irmacard.credentials.spec.IssueSpecification;
import org.irmacard.credentials.spec.VerifySpecification;

/**
 * A generic interface for interaction with a credentials system, abstracting
 * from the low-level credential technology specifics.
 */
public interface Credentials {

	/**
	 * Issue a credential to the user according to the provided specification
	 * containing the specified values.
	 * 
	 * @param specification
	 *            of the issuer and the credential to be issued.
	 * @param secretkey
	 * 			  of the issuer, to be used when issueing credential.
	 * @param values
	 *            to be stored in the credential.
	 * @throws CredentialsException
	 *             if the issuance process fails.
	 */
	public void issue(CredentialDescription specification, PrivateKey pkey, Attributes values, Date expires)
			throws CredentialsException;

	/**
	 * Get a blank IssueSpecification matching this Credentials provider.
	 * TODO: WL: would suggest to remove this.
	 * 
	 * @return a blank specification matching this provider.
	 */
	public IssueSpecification issueSpecification();

	/**
	 * Verify a number of attributes listed in the specification.
	 * 
	 * @param specification
	 *            of the credential and attributes to be verified.
	 * @return the attributes disclosed during the verification process or null
	 *         if verification failed
	 * @throws CredentialsException
	 */
	public Attributes verify(VerifySpecification specification)
			throws CredentialsException;

	/**
	 * Returns the ProtocolCommands necessary to request a proof from the card.
	 * This is a lower-level entry-point to the API, that allows the user to
	 * process the resulting APDU commands using a separate, possibly
	 * asynchronous interface.
	 *
	 * @param specification
	 *            specification of the credential and attributes to be verified.
	 * @param nonce The nonce used as part of the challenge
	 * @return
	 * @throws CredentialsException
	 */
	public List<ProtocolCommand> requestProofCommands(
			VerificationDescription specification, BigInteger nonce) throws CredentialsException;

	/**
	 * Compile the cards responses into a proof and check this proof for correctness.
	 * This is a lower-level entry-point to the API, that allows the user to
	 * process the resulting APDU commands using a separate, possibly
	 * asynchronous interface.
	 * 
	 * TODO: Wrap nonce again in Nonce object?
	 *
	 * @param specification
	 *            specification of the credential and attributes to be verified.
	 * @param nonce The nonce used when requesting the proof commands
	 * @param responses The responses to the proof requests
	 * @return the attributes disclosed during the verification process or null
	 *         if verification failed
	 * @throws CredentialsException
	 */
	public Attributes verifyProofResponses(VerificationDescription specification,
			BigInteger nonce, ProtocolResponses responses) throws CredentialsException;

	/**
	 * Generate a nonce for use in the asynchronous API. This nonce contains all the
	 * randomness required in the proof run, and hence acts as an explicit state.
	 *
	 * @return The nonce
	 * @throws CredentialsException
	 */
	public Nonce generateNonce(VerifySpecification specification)
			throws CredentialsException;
}
