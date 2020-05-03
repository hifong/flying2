package com.flying.mq.producer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;

import com.flying.common.util.Codes;
import com.flying.framework.annotation.LoadOnStartup;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.service.AbstractService;

@Service("MQProducer")
public class MQProducer extends AbstractService {
	
	public static KafkaProducer<String, String> producer;
	@LoadOnStartup
	public void initProducer() {
		var props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.module.getModuleConfig().getConfig("kafka.bootstrap_servers"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, MQProducerInterceptor.class.getName());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "mq.test");
		producer = new KafkaProducer<String, String>(props);
		System.out.println("----MQProducer initialized!!");
	}
	
	public void closeProducer() {
		producer.close();
		System.out.println("----MQProducer!!");
	}
	
	private ProducerRecord<String, String> buildRecord(String topic, Integer partition, Long timestamp, 
			String key, String value, Map<String, String> headers) {
		assert (topic != null && value != null);
		ArrayList<Header> hs = new ArrayList<Header>();
		if(headers != null) {
			headers.entrySet().stream().forEach(x -> {
				if(x.getKey() != null && x.getValue() != null) 
					hs.add(new RecordHeader(x.getKey(), x.getValue().getBytes()));
			});
		}
		return new ProducerRecord<String, String>(topic, partition, timestamp, key, value, hs);
	}
	
	private ProducerRecord<String, String> buildRecord(String topic, String key, String value) {
		return this.buildRecord(topic, null, null, key, value, null);
	}
	
	private ProducerRecord<String, String> buildRecord(String topic, String value) {
		return this.buildRecord(topic, null, null, null, value, null);
	}

	public ActionResult send1(
			@Param(value = "topic", required=true, desc="主题") String topic, 
			@Param(value = "message", required=true, desc="消息") String message) {
		var record = this.buildRecord(topic, message);
		producer.send(record, new CallbackImpl());
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}

	public ActionResult send2(
			@Param(value = "topic", required=true, desc="主题") String topic, 
			@Param(value = "key", required=true, desc="KEY") String key, 
			@Param(value = "message", required=true, desc="消息") String message) {
		var record = this.buildRecord(topic, key, message);
		producer.send(record, new CallbackImpl());
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}

	public ActionResult send3(
			@Param(value = "topic", required=true, desc="主题") String topic, 
			@Param(value = "partition", required=true, desc="分区") Integer partition, 
			@Param(value = "key", required=true, desc="KEY") String key, 
			@Param(value = "message", required=true, desc="消息") String message) {
		var record = this.buildRecord(topic, partition, null, key, message, null);
		producer.send(record, new CallbackImpl());
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}
	
	class CallbackImpl implements Callback {

		@Override
		public void onCompletion(RecordMetadata rm, Exception ex) {
			if(ex == null) {
				String s = String.format("---onCompletion: Topic: %s, partition: %s, timestamp: %s, offset: %s", rm.topic(), rm.partition(), rm.timestamp(), rm.offset());
				System.out.println(s + ";  "+ rm.toString());
			} else {
				ex.printStackTrace();
			}
		}
		
	}
}
