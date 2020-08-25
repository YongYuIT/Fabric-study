import static msp.BuildTools.buildOrdererOrgs;
import static msp.BuildTools.buildPeerOrgs;
import static sys_channel.BuildTools.buildChannel;
import static sys_channel.BuildTools.buildOrdererGenesis;

public class Main {
    public static void main(String[] args) throws Exception {
        buildPeerOrgs();
        System.out.println("-------------------------------------");
        buildOrdererOrgs();
        System.out.println("-------------------------------------");
        buildOrdererGenesis();
        System.out.println("-------------------------------------");
        buildChannel();
    }

}




