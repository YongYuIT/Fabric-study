import kafka.admin.AdminUtils;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.zookeeper.common.Time;

import java.util.Properties;


public class test {

    private static final String ZK_CONNECT = "0.0.0.0:2181,0.0.0.0:2182,0.0.0.0:2183";
    private static final int SESSION_TIMEOUT = 30000;
    private static final int CONNECT_TIMEOUT = 30000;

    private static void createTopic(String topic, int partition, int replica) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(ZK_CONNECT, SESSION_TIMEOUT, CONNECT_TIMEOUT, JaasUtils.isZkSecurityEnabled());
            if (!AdminUtils.topicExists(zkUtils, topic)) {
                AdminUtils.createTopic(zkUtils, topic, partition, replica, new Properties(), AdminUtils.createTopic$default$6());
                System.out.println("success");
            } else {
                System.out.println("topicExists");
            }
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        } finally {
            zkUtils.close();
        }
    }

    private static Callback sendCallBack = new Callback() {
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            System.out.println("send error");
            e.printStackTrace();
        }
    };

    private static void singleThreadProducer() {
        Properties configs = initProducerProperties();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);
        for (int i = 0; i < 100; i++) {
            long time_now = Time.currentWallTime();
            ProducerRecord record = new ProducerRecord<String, String>(TOPIC, null, time_now, "fuck-->" + (i % 2), "value-->" + i);
            //异步发送
            producer.send(record, sendCallBack);
            System.out.println("success send " + i);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                //org.apache.kafka.common.errors.TimeoutException: Expiring 16 record(s) for test-fuck-2: 30038 ms has passed since batch creation plus linger time
                //server.properties-->advertised.listeners

                //org.apache.kafka.common.errors.TimeoutException: Failed to update metadata after 60000 ms.

            }

        }
        producer.close();
    }

    private static final String BROKER_LIST = "0.0.0.0:9092,0.0.0.0:9093,0.0.0.0:9094";

    private static Properties initProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    private static String TOPIC = "test-fuck";

    public static void main(String[] args) {
        int partition = 3;
        int replica = 2;

        createTopic(TOPIC, partition, replica);
        singleThreadProducer();
    }

    /*cmd

    docker stop kafka_1.thinking.com kafka_2.thinking.com kafka_3.thinking.com test_1.thinking.com test_2.thinking.com test_3.thinking.com
    docker rm kafka_1.thinking.com kafka_2.thinking.com kafka_3.thinking.com test_1.thinking.com test_2.thinking.com test_3.thinking.com
    docker network rm kafka_thinking
    sudo rm -rf ./kafka-grp-cmd/log-*

    docker-compose -f docker-compose-zookeeper.yml up -d &
    docker-compose -f docker-compose-kafka-grp.yml up -d &

    docker exec -it kafka_1.thinking.com /bin/bash
    cd /usr/local/kafka/bin
    ./kafka-run-class.sh kafka.admin.TopicCommand --zookeeper test_1.thinking.com:2181 --list
    ./kafka-topics.sh --zookeeper test_1.thinking.com:2181 --describe --topic test-fuck
    ./kafka-console-consumer.sh --bootstrap-server kafka_1.thinking.com:9092 --topic test-fuck --from-beginning
    ./kafka-run-class.sh kafka.tools.DumpLogSegments --files /tmp/kafka-logs/test-fuck-0/00000000000000000000.log --print-data-log
    */
}
