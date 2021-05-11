package com.delivery.producer;

import com.example.payment.dto.PaymentJsonDTO;
import com.example.payment.enums.PaymentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class ProducerApplication {
    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(ProducerApplication.class, args);

        //Creating Properties
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        properties.put("enable.idempotence", "true");
        properties.put("transactional.id", "prod-1");

        Producer<String, String> producer = new KafkaProducer<>(properties);
        ObjectMapper mapper = new ObjectMapper();
        PaymentJsonDTO dto = new PaymentJsonDTO();
        dto.setAmount("2");
        dto.setCreditCard("dd");
        dto.setAccountId("44");
        dto.setPaymentType(PaymentType.OFFLINE);
        String recordValue = mapper.writeValueAsString(dto);

        producer.initTransactions();
        producer.beginTransaction();

        ProducerRecord<String, String> record = new ProducerRecord<>("offline", null, recordValue);
        producer.send(record);


        // ONLINE
        dto.setAmount("2");
        dto.setCreditCard("dd");
        dto.setAccountId("44");
        dto.setPaymentType(PaymentType.ONLINE);
        String onlineData = mapper.writeValueAsString(dto);

        ProducerRecord<String, String> online = new ProducerRecord<>("online", null, onlineData);
        producer.send(online);
        producer.flush();
        producer.commitTransaction();
        producer.close();
    }
}
