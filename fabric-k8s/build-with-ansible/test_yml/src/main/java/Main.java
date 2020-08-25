import static msp.BuildTools.buildOrdererOrgs;
import static msp.BuildTools.buildPeerOrgs;
import static sys_channel.BuildTools.buildSysChannel;

public class Main {
    public static void main(String[] args) throws Exception {
        buildPeerOrgs();
        System.out.println("-------------------------------------");
        buildOrdererOrgs();
        System.out.println("-------------------------------------");
        buildSysChannel();
    }

}




