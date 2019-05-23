package test001;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.*;
import org.junit.Test;

import java.util.Properties;

public class MyTest {
    @Test
    public void test() throws Exception {
        /*
         * start fabbric ca server
         * $ cd ~
         * $ mkdir fabric-ca-server-path
         * $ cd fabric-ca-server-path
         * $ fabric-ca-server start -b fabric-ca-server-username:fabric-ca-server-pwd --cfg.affiliations.allowremove  --cfg.identities.allowremove
         * Listening on http://0.0.0.0:7054
         * */

        Properties properties = new Properties();
        HFCAClient hfcaClient = HFCAClient.createNewInstance("http://0.0.0.0:7054", properties);
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        //================================================================================================================
        Enrollment enrollment = hfcaClient.enroll("fabric-ca-server-username", "fabric-ca-server-pwd");
        CAUser rootUser = new CAUser();
        rootUser.setEnrollment(enrollment);
        HFCAAffiliation hfcaAffiliation = hfcaClient.getHFCAAffiliations(rootUser);
        printAffiliation(hfcaAffiliation);

        if (!checkHasAffiliation("com", hfcaAffiliation)) {
            hfcaClient.newHFCAAffiliation("com").create(rootUser);
            hfcaClient.newHFCAAffiliation("com.example").create(rootUser);
            hfcaClient.newHFCAAffiliation("com.example.org1").create(rootUser);
            hfcaClient.newHFCAAffiliation("com.example.org2").create(rootUser);
        }
        hfcaAffiliation = hfcaClient.getHFCAAffiliations(rootUser);
        printAffiliation(hfcaAffiliation);
        //================================================================================================================
        HFCACertificateRequest request = hfcaClient.newHFCACertificateRequest();
        HFCACertificateResponse response = hfcaClient.getHFCACertificates(rootUser, request);
        //这里会获取到多个CA公钥，每个公钥都可以验证CA，所以每个公钥都是有效的
        System.out.println("getHFCACertificates-->" + response.getCerts().size());
        //================================================================================================================
        RegistrationRequest registrationRequest = new RegistrationRequest("Admin@example.com", "com.example");
        registrationRequest.setType(HFCAClient.HFCA_TYPE_CLIENT);
        registrationRequest.getAttributes().add(new Attribute(HFCAClient.HFCA_ATTRIBUTE_HFREGISTRARROLES, HFCAClient.HFCA_TYPE_CLIENT));
        registrationRequest.getAttributes().add(new Attribute(HFCAClient.HFCA_ATTRIBUTE_HFREGISTRARDELEGATEROLES, HFCAClient.HFCA_TYPE_CLIENT));
        hfcaClient.register(registrationRequest, rootUser);
        //...
    }

    private void printAffiliation(HFCAAffiliation hfcaAffiliation) throws Exception {
        System.out.println("-->" + hfcaAffiliation.getName());
        for (HFCAAffiliation child : hfcaAffiliation.getChildren()) {
            printAffiliation(child);
        }
    }

    private boolean checkHasAffiliation(String name, HFCAAffiliation hfcaAffiliation) throws Exception {
        if (hfcaAffiliation.getName().equalsIgnoreCase(name)) {
            return true;
        } else {
            for (HFCAAffiliation child : hfcaAffiliation.getChildren()) {
                if (checkHasAffiliation(name, child)) {
                    return true;
                }
            }
        }
        return false;
    }
}
