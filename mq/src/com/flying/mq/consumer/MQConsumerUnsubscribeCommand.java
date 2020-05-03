package com.flying.mq.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;

public class MQConsumerUnsubscribeCommand implements MQConsumerCommand{

	@Override
	public void execute(KafkaConsumer<?, ?> consumer) {
		consumer.unsubscribe();
		consumer.close();
	}
}
