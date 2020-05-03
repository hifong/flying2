package com.flying.mq.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.flying.common.util.KeyValuePair;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.data.Data;

public class MQConsumerThread extends Thread{
	final ConcurrentLinkedQueue<MQConsumerCommand> commands = new ConcurrentLinkedQueue<MQConsumerCommand>();
	final AtomicBoolean isRunning = new AtomicBoolean(true);
	final KafkaConsumer<String, String> consumer;
	final String handlerModuleId;
	final String handlerService;
	final String topic;
	final String groupId;
	final boolean isAutoCommit;
	
	MQConsumerThread(String topic, String groupId, boolean isAutoCommit, 
			String handlerModuleId, String handlerService, int threadIndex) {
		this.setName("ConsumerThread:" + topic +":"+ groupId + ":" +threadIndex);
		this.topic = topic;
		this.groupId = groupId;
		this.handlerModuleId = handlerModuleId;
		this.handlerService = handlerService;
		this.isAutoCommit = isAutoCommit;
		var props = new Properties();
		props.putAll(MQConsumer.props);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, isAutoCommit);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, topic +":"+ groupId + ":" +threadIndex);
		System.out.println("---->>>>"+this+".started!");
		consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topic));
	}
	
	public boolean eq(String topic, String groupId) {
		return this.topic.equals(topic) && 
				(this.groupId == null? groupId == null: this.groupId.equals(groupId));
	}
	
	public synchronized void unsubscribe() {
		this.commands.add(new MQConsumerUnsubscribeCommand());
		isRunning.set(false);
	}
	
	public void run() {
		while(isRunning.get()) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			records.forEach(record -> {
				Data row = new Data();
				row.put(MQConsumer.CONSUMER_FIELD_TOPIC, record.topic());
				row.put(MQConsumer.CONSUMER_FIELD_KEY, record.key());
				row.put(MQConsumer.CONSUMER_FIELD_MESSAGE, record.value());
				row.put(MQConsumer.CONSUMER_FIELD_OFFSET, record.offset());
				row.put(MQConsumer.CONSUMER_FIELD_PARTITION, record.partition());
				row.put(MQConsumer.CONSUMER_FIELD_TIMESTAMP, record.timestamp());
				if(record.headers() != null) {
					var hds = new ArrayList<KeyValuePair<String, byte[]>>();
					record.headers().forEach(x -> {
						hds.add(new KeyValuePair<String, byte[]>(x.key(), x.value()));
					});
					row.put(MQConsumer.CONSUMER_FIELD_HEADERS, hds);
				}
				ServiceHelper.invoke(handlerModuleId, handlerService, row);
				//
				if(!isAutoCommit)
					consumer.commitAsync();
			});
			//
			if(!commands.isEmpty()) {
				for(Iterator<MQConsumerCommand> it = commands.iterator(); it.hasNext(); ) {
					MQConsumerCommand cmd = it.next();
					cmd.execute(consumer);
					it.remove();
				}
			}
		}
	}
}