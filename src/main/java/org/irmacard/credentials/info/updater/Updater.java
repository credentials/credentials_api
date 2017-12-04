package org.irmacard.credentials.info.updater;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.security.Security;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyFactory;
import java.security.PublicKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Updater {
    public static byte[] download(String url) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        return IOUtils.toByteArray(request.execute().getContent());
    }

    public static boolean Update(String url, String path, String pk)
                throws IOException, VerificationFailedException,
                      IndexParsingException, NoSuchAlgorithmException,
                      InvalidKeySpecException, NoSuchProviderException,
                       SignatureException, InvalidKeyException {
        boolean changes = false;

        // Download index and signature
        byte[] rawIndex = download(url + "/index");
        byte[] rawIndexSig = download(url + "/index.sig");
        
        // Check signature
        PemObject pemObj = (new PEMParser(new StringReader(pk))).readPemObject();
        PublicKey ppk = KeyFactory.getInstance("EC","BC").generatePublic(
                new X509EncodedKeySpec(pemObj.getContent()));
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaVerify.initVerify(ppk);
        ecdsaVerify.update(rawIndex);
        if (!ecdsaVerify.verify(rawIndexSig)) {
            throw new VerificationFailedException("Invalid signature on index");
        }

        // Parse index
        Map<String,String> index = parseIndex(new String(rawIndex));

        // Loop over files
        for (String file : index.keySet()) {
            Path filePath = Paths.get(path, file);

            if (Files.exists(filePath)) {
                byte[] contents = Files.readAllBytes(filePath);
                if (DigestUtils.sha256Hex(contents).equals(index.get(file))) {
                    continue; // file is up-to-date
                }
            } else {
                if (!Files.exists(filePath.getParent())) {
                    Files.createDirectories(filePath.getParent());
                }
            }

            // The file doesn't exist or is out-of-date; create it!
            byte[] contents = download(url + "/" + file);
            if (!DigestUtils.sha256Hex(contents).equals(index.get(file))) {
                throw new VerificationFailedException(
                        String.format("Hash mismatch for %s %s != %s", file,
                        DigestUtils.sha256Hex(contents), index.get(file)));
            }
            Files.write(filePath, contents);
            changes = true;
        }
        return changes;
    }

    // Parses the index file into a map filename -> hash.
    static Map<String,String> parseIndex(String index)
                throws IndexParsingException {
        HashMap<String,String> ret = new HashMap<String,String>();
        for (String line : StringUtils.split(index, "\n")) {
            String[] bits = StringUtils.split(line, " ", 2);
            if (bits.length != 2) {
                throw new IndexParsingException("Malformed index line");
            }
            String hash = bits[0];
            String path = bits[1];
            String[] pathBits = StringUtils.split(line, "/", 2);
            if (pathBits.length != 2) {
                throw new IndexParsingException("Malformed index path");
            }
            ret.put(pathBits[1], hash);
        }
        return ret;
    }
}
