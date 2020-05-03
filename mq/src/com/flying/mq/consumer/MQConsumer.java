package com.flying.mq.consumer;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.LoadOnStartup;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.service.AbstractService;

@Service("MQConsumer")
public class MQConsumer extends AbstractService {
	
	public final static List<MQConsumerThread> threads = Utils.newArrayList();
	public final static Properties props = new Properties();
	
	public final static String CONSUMER_FIELD_TOPIC = "topic";
	public final static String CONSUMER_FIELD_KEY = "key";
	public final static String CONSUMER_FIELD_MESSAGE = "message";
	public final static String CONSUMER_FIELD_OFFSET = "offset";
	public final static String CONSUMER_FIELD_PARTITION = "partition";
	public final static String CONSUMER_FIELD_TIMESTAMP = "timestamp";
	public final static String CONSUMER_FIELD_HEADERS = "headers";
	
	@LoadOnStartup
	public void initConsumer() {
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.module.getModuleConfig().getConfig("kafka.bootstrap_servers"));
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "mq.consumer");
//		props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, MQProducerInterceptor.class.getName());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "mq.consumer");
	}

	public Data partitionsFor(@Param("topic")String topic) {
		try(KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props)){
			return new Data(Codes.CODE, Codes.SUCCESS, "partitionInfos", consumer.partitionsFor(topic));
		}
	}
	
	//
	public synchronized ActionResult subscribe(
			@Param(value = "topic", required=true, desc="主题")String topic, 
			@Param(value = "groupId", required=true, desc="客户组")String groupId, 
			@Param(value = "autoCommit", required=false, desc="自动提交")boolean autoCommit,
			@Param(value = "threadCount", required=false, min="1", desc="线程数")int threadCount,
			@Param(value = "handlerModuleId", required=true, desc="消费模块")String handlerModuleId,
			@Param(value = "handlerService", required=true, desc="消费服务")String handlerService) {
		for(int i=0; i< threadCount; i++) {
			int c = threads.stream().filter(x -> x.eq(topic, groupId)).collect(Collectors.counting()).intValue();
			MQConsumerThread t = new MQConsumerThread(topic, groupId, autoCommit, handlerModuleId, handlerService, c);
			threads.add(t);
			t.start();
		}
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}

	public synchronized ActionResult unsubscribe(
			@Param(value = "topic", required=true, desc="主题")String topic, 
			@Param(value = "groupId", required=true, desc="客户组")String groupId) {
		threads.stream().filter(x -> x.eq(topic, groupId)).forEach(x -> x.unsubscribe());
		threads.removeIf(x -> x.eq(topic, groupId));
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}
	
}
