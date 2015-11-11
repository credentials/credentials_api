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

package org.irmacard.credentials.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;
import net.sf.scuba.smartcards.ResponseAPDU;

public class CardAuthenticationService extends CardService {

	private static final long serialVersionUID = -7992986822145276116L;

	private static final byte CLA_ISO7816 = 0x00;
	private static final byte INS_ISO7816_INTERNAL_AUTHENTICATE = (byte) 0x88;
	private CardService service;

	public CardAuthenticationService(CardService service) {
		this.service = service;
	}

	public void open() throws CardServiceException {
		service.open();
	}

	public boolean isOpen() {
		return service.isOpen();
	}

	public ResponseAPDU transmit(CommandAPDU capdu)
	throws CardServiceException {
		return service.transmit(capdu);
	}

	public byte[] transmitControlCommand(int controlCode, byte[] command)
	throws CardServiceException {
		return service.transmitControlCommand(controlCode, command);
	}

	public void close() {
		service.close();
	}

	public String getName() {
		return "Authentication: " + service.getName();
	}

	public byte[] getATR() 
	throws CardServiceException {
		return service.getATR();
	}

	public SecureMessagingWrapper authenticateCard(BigInteger modulus, BigInteger exponent) 
    throws CardServiceException {
    	SecretKey encKey, macKey; long ssc = 0;
    	BigInteger terminalSeedInt = new BigInteger(modulus.bitLength(), new Random());
    	terminalSeedInt.mod(modulus);
    	byte[] terminalSeedBytes = terminalSeedInt.toByteArray();
    	byte[] terminalSeed = new byte[128];
    	System.arraycopy(terminalSeedBytes, 0, terminalSeed, terminalSeed.length - terminalSeedBytes.length, terminalSeedBytes.length);
    	CommandAPDU command = new CommandAPDU(CLA_ISO7816, INS_ISO7816_INTERNAL_AUTHENTICATE, 0x00, 0x00, terminalSeed);
    	ResponseAPDU response = service.transmit(command);
    	if (response.getSW() != 0x9000) {
    		throw new CardServiceException("Card authentication failed.");
    	}
    	byte[] cardSeed = response.getData();
    	
    	try{
			MessageDigest shaDigest = MessageDigest.getInstance("SHA1");
			SecretKeyFactory desKeyFactory = SecretKeyFactory.getInstance("DESede");
			byte[] sscBytes = new byte[8], hash = new byte[20], key = new byte[24];
		
			shaDigest.update(cardSeed);
			shaDigest.update(terminalSeed);			
			shaDigest.update(new byte[]{ 0x00, 0x00, 0x00, 0x01 }); // ENC_MODE
			hash = shaDigest.digest();
			System.arraycopy(hash, 0, key, 0, 8);
			System.arraycopy(hash, 8, key, 8, 8);
			System.arraycopy(hash, 0, key, 16, 8);
			encKey = desKeyFactory.generateSecret(new DESedeKeySpec(key));			
			System.arraycopy(hash, 16, sscBytes, 0, 4);
		
			shaDigest.update(cardSeed);
			shaDigest.update(terminalSeed);			
			shaDigest.update(new byte[]{ 0x00, 0x00, 0x00, 0x02 }); // ENC_MODE
			hash = shaDigest.digest();
			System.arraycopy(hash, 0, key, 0, 8);
			System.arraycopy(hash, 8, key, 8, 8);
			System.arraycopy(hash, 0, key, 16, 8);
			macKey = desKeyFactory.generateSecret(new DESedeKeySpec(key));			
			System.arraycopy(hash, 16, sscBytes, 4, 4);
			
			for (int i = 0; i < sscBytes.length; i++) {
				ssc = (ssc << 8) | (sscBytes[i] & 0x000000ff);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CardServiceException("Key derivation failed: " + e.getMessage());
		}
    	
    	try {
			return new SecureMessagingWrapper(encKey, macKey, ssc);
		} catch (Exception e) {
			e.printStackTrace();
    		throw new CardServiceException("Secure messaging setup failed: " + e.getMessage());
		}
    }
}
