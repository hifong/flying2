package com.flying.mq.producer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MQProducerInterceptor implements ProducerInterceptor {
	private final static AtomicLong successCount = new AtomicLong(0);
	private final static AtomicLong failCount = new AtomicLong(0);

	@Override
	public void configure(Map<String, ?> configs) {
		System.out.println(configs);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProducerRecord onSend(ProducerRecord record) {
		record.headers().add("creator", "king".getBytes());
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata rm, Exception ex) {
		if(ex == null) {
			String s = String.format("---onAcknowledgement: %s \tTopic: %s, partition: %s, timestamp: %s, offset: %s",successCount.incrementAndGet(), rm.topic(), rm.partition(), rm.timestamp(), rm.offset());
			System.out.println(s + ";  "+ rm.toString());
		} else {
			System.out.println(String.format("---onAcknowledgement: %s \tInfo: %s ", failCount.incrementAndGet(), ex.toString()));
		}
	}

	@Override
	public void close() {
		System.out.println("-----Producer Closed!");
	}

}
