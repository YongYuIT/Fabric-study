package event;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

public class MyTest {

    private static final String FABRIC_CONFIG_PATH = "/mnt/hgfs/fabric-env/fabric-samples/first-network/crypto-config/";
    private static final String FABRIC_USER_MSP_PATH = "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";

    @Test
    public void test() throws Exception {

        /*
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
         * disable tls on orderer and peers, need to modify docker-compose-cli.yaml and peer-base.yaml files
         * $ ./byfn.sh down
         * $ ./byfn.sh up
         * */
        //========================================================
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        File privateKeyFile = PeerUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + FABRIC_USER_MSP_PATH + "keystore/");
        File certificateFile = PeerUserEnrollment.getCertificateFileFromPath(FABRIC_CONFIG_PATH + FABRIC_USER_MSP_PATH + "signcerts/");
        PeerUserEnrollment peerUserEnrollment = new PeerUserEnrollment(privateKeyFile, certificateFile);
        PeerUser user = new PeerUser();
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        user.setEnrollment(peerUserEnrollment);
        hfclient.setUserContext(user);
        //========================================================
        Channel channel = hfclient.newChannel("mychannel");
        Peer peer = hfclient.newPeer("peer0", "grpc://localhost:7051");
        channel.addPeer(peer);
        Orderer orderer = hfclient.newOrderer("orderer", "grpc://localhost:7050");
        channel.addOrderer(orderer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        System.out.println("channel.initialize success");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");
        //========================================================
        String blockHandlerId = channel.registerBlockListener(new BlockListener() {
            @Override
            public void received(BlockEvent blockEvent) {
                System.out.println("registerBlockListener");
                for (BlockInfo.EnvelopeInfo envelopeInfo : blockEvent.getEnvelopeInfos()) {
                    System.out.println("registerBlockListener-->" + envelopeInfo.getChannelId());
                }
            }
        });
        System.out.println("blockHandlerId-->" + blockHandlerId);
        String chaincodeHandlerId = channel.registerChaincodeEventListener(Pattern.compile("chaincode_event1001"), Pattern.compile("test_event_name"), new ChaincodeEventListener() {
            @Override
            public void received(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
                System.out.println("registerChaincodeEventListener-->" + chaincodeEvent.getEventName() + "-->" + new String(chaincodeEvent.getPayload()));
            }
        });
        System.out.println("chaincodeHandlerId-->" + chaincodeHandlerId);
        /*
         * BlockListener works, ChaincodeEventListener doesn't work
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
         * $ docker exec -it cli bash
         * # peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
         * # peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --peerAddresses peer0.org2.example.com:9051 -c '{"Args":["invoke","a","b","10"]}'
         * */

        /*
         * ChaincodeEventListener works
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/chaincode
         * $ mkdir -p event_test/go
         * $ cd event_test/go
         * $ cp /mnt/hgfs/proj/Go-Stu/smart_contract/chaincode_event1001.go .
         * $ docker exec -it cli bash
         * # peer chaincode install -n chaincode_event1001 -v v0 -p github.com/chaincode/event_test/go
         * # peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n chaincode_event1001 -v v0 -c '{"Args":[]}' -P "AND('Org1MSP.member')"
         * this way doesn't work
         * # peer chaincode query -C mychannel -n chaincode_event1001 -c '{"Args":["test_env","test_event_key"]}'
         * this way works
         * # peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n chaincode_event1001 --peerAddresses peer0.org1.example.com:7051 -c '{"Args":["test_env","test_event_key"]}'
         * */
        System.in.read();
        channel.unregisterBlockListener(blockHandlerId);
        channel.unregisterChaincodeEventListener(chaincodeHandlerId);
    }
}
