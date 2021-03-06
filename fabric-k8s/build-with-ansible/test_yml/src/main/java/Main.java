import static docker.BuildTools.buildOrderer;
import static docker.BuildTools.buildPeer;
import static gen_and_channel.BuildTools.buildChannel;
import static gen_and_channel.BuildTools.buildOrdererGenesis;
import static msp.BuildTools.buildOrdererOrgs;
import static msp.BuildTools.buildPeerOrgs;
import static yml_any.AnyConfigTx.AnyTx;

public class Main {
    public static void main(String[] args) throws Exception {
        AnyTx();
        System.out.println("-------------------------------------");
        buildPeerOrgs();
        System.out.println("-------------------------------------");
        buildOrdererOrgs();
        System.out.println("-------------------------------------");
        buildOrdererGenesis();
        System.out.println("-------------------------------------");
        buildChannel();
        System.out.println("-------------------------------------");
        buildOrderer("1.4.6", "orderer.example.com");
        System.out.println("-------------------------------------");
        buildPeer("1.4.6", "Org1MSP", "org1.example.com", "peer0", 7051, "peer1.org1.example.com:8051");
        buildPeer("1.4.6", "Org2MSP", "org2.example.com", "peer0", 9051, "peer1.org2.example.com:10051");
        buildPeer("1.4.6", "Org3MSP", "org3.example.com", "peer0", 11051, "peer1.org3.example.com:12051");
    }

}




