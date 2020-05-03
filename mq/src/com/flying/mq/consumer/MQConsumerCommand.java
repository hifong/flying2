package com.flying.mq.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface MQConsumerCommand {
	void execute(KafkaConsumer<?, ?> consumer);
}
