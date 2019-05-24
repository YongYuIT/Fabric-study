package org3_endorsement;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;

public class PeerUserEnrollment implements Enrollment, Serializable {

    private static final long serialVersionUID = 2771852454437963084L;

    private PrivateKey privateKey;
    private String certificate;

    public PeerUserEnrollment(File privateKeyFile, File certificateFile) throws Exception {
        this.certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)));
        this.privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
    }


    @Override
    public PrivateKey getKey() {
        return privateKey;
    }

    @Override
    public String getCert() {
        return certificate;
    }

    private static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        PEMParser pemParse = new PEMParser(pemReader);
        pemPair = (PrivateKeyInfo) pemParse.readObject();
        Security.addProvider(new BouncyCastleProvider());
        PrivateKey pk = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        pemParse.close();
        return pk;
    }

    public static File getPrivateKeyFileFromPath(String path_str) {
        return getFile(path_str, "_sk");
    }

    public static File getCertificateFileFromPath(String path_str) {
        return getFile(path_str, ".pem");
    }

    private static File getFile(String path_str, String endWith) {
        File path = new File(path_str);
        if (!path.isDirectory()) {
            return null;
        }
        File[] files = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if (s.endsWith(endWith)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (files == null || files.length < 1) {
            return null;
        }
        return files[0];
    }
}
