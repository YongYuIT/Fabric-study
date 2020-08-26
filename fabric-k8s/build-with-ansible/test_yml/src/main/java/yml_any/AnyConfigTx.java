package yml_any;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnyConfigTx {
    public static void AnyTx() throws Exception {
        YamlReader reader = new YamlReader(new FileReader("configtx.sample.yaml"));
        Object object = reader.read();
        Map map = (Map) object;
        Object TwoOrgsOrdererGenesis = ((Map) map.get("Profiles")).get("TwoOrgsOrdererGenesis");
        Map Obj1 = new LinkedHashMap();
        Obj1.put("TwoOrgsOrdererGenesis", TwoOrgsOrdererGenesis);
        Map Obj2 = new LinkedHashMap();
        Obj2.put("Profiles", Obj1);
        String yaml = getYml(Obj2);
        yaml = yaml.replace("!", "#");
        yaml = yaml.replace("OR('", "\"OR('");
        yaml = yaml.replace("')", "')\"");
        System.out.println(yaml);
        saveFile(yaml, "configtx-genesis.sample.yaml");

        Object TwoOrgsChannel = ((Map) map.get("Profiles")).get("TwoOrgsChannel");
        Obj1 = new LinkedHashMap();
        Obj1.put("TwoOrgsChannel", TwoOrgsChannel);
        Obj2 = new LinkedHashMap();
        Obj2.put("Profiles", Obj1);
        yaml = yaml.replace("!", "#");
        yaml = yaml.replace("OR('", "\"OR('");
        yaml = yaml.replace("')", "')\"");
        yaml = getYml(Obj2);
        System.out.println(yaml);
        saveFile(yaml, "configtx-channel.sample.yaml");
    }

    private static String getYml(Object object) {
        StringWriter stringWriter = new StringWriter();
        YamlWriter writer = new YamlWriter(stringWriter);
        try {
            writer.write(object);
            writer.close();
        } catch (YamlException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static void saveFile(String content, String fileName) throws Exception {
        FileWriter fwriter = new FileWriter(fileName);
        fwriter.write(content);
        fwriter.flush();
        fwriter.close();
    }
}
