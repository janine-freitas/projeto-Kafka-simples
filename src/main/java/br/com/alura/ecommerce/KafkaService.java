package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class KafkaService implements Closeable {
    private final KafkaConsumer<String, String> consumer;
    private final ConsumerFunction parse;

    private KafkaService(ConsumerFunction parse, String groupId) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<String, String>(properties(groupId));
    }

    KafkaService(String groupId, String topic, ConsumerFunction parse) {
        this(parse, groupId);
        consumer.subscribe(Collections.singletonList(topic));
        //run();
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction parse) {
        this(parse, groupId);
        consumer.subscribe(topic);
        //run();
    }

    void run() {
        while(true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei "+records.count()+ " registros");
                for (var record : records) {
                    parse.consume(record);
                }
            }
        }
    }

    static Properties properties(String simpleName) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, simpleName);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        return properties;
    }

    @Override
    public void close(){
        consumer.close();
    }
}
