package com.flying.mq.producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

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

@Service("MQProducerWithTrans")
public class MQProducerWithTransaction extends AbstractService {
	@LoadOnStartup
	public Properties initProducer() {
		var props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.module.getModuleConfig().getConfig("kafka.bootstrap_servers"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, MQProducerInterceptor.class.getName());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "mq_trans.producer");

		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		
		props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
		return props;
	}

	private ProducerRecord<String, String> buildRecord(String topic, Integer partition, Long timestamp, String key,
			String value, Map<String, String> headers) {
		assert (topic != null && value != null);
		ArrayList<Header> hs = new ArrayList<Header>();
		if (headers != null) {
			headers.entrySet().stream().forEach(x -> {
				if (x.getKey() != null && x.getValue() != null)
					hs.add(new RecordHeader(x.getKey(), x.getValue().getBytes()));
			});
		}
		return new ProducerRecord<String, String>(topic, partition, timestamp, key, value, hs);
	}

	private ProducerRecord<String, String> buildRecord(String topic, String value) {
		return this.buildRecord(topic, null, null, null, value, null);
	}

	public ActionResult sendInTransaction(
			@Param(value = "records", required = true, desc = "消息列表") 
			List<ProducerRecord<String, String>> records) {
		try(KafkaProducer<String, String> producer = new KafkaProducer<String, String>(initProducer())) {
			producer.initTransactions();
			producer.beginTransaction();
			try {
				records.stream().forEach(x -> producer.send(x));
				producer.commitTransaction();
				return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
			} catch (Exception e) {
				producer.abortTransaction();
				return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.FAIL);
			}
		}
	}

	public ActionResult send1(@Param(value = "topic", required = true, desc = "主题") String topic,
			@Param(value = "message", required = true, desc = "消息") String messages) {
		var records = Arrays.asList(messages.split(";")).stream().map(x -> buildRecord(topic, x))
				.collect(Collectors.toList());
		return this.sendInTransaction(records);
	}

	public ActionResult send2(@Param(value = "topic", required = true, desc = "主题") String topic,
			@Param(value = "message", required = true, desc = "消息") String[] messages) {
		var records = Arrays.asList(messages).stream().map(x -> buildRecord(topic, x)).collect(Collectors.toList());
		return this.sendInTransaction(records);
	}

	public ActionResult send3(@Param(value = "entries", required = true, desc = "元素") List<ProducerRecordEntry> entries) {
		var records = entries.stream().map(x -> x.asProducerRecord()).collect(Collectors.toList());
		return this.sendInTransaction(records);
	}

	public class ProducerRecordEntry {
		private String topic;
		private Integer partition;
		private Long timestamp;
		private String key;
		private String message;
		private Map<String, String> headers;

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public void setPartition(Integer partition) {
			this.partition = partition;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setHeaders(Map<String, String> headers) {
			this.headers = headers;
		}

		public ProducerRecord<String, String> asProducerRecord() {
			return buildRecord(topic, partition, timestamp, key, message, headers);
		}
	}

	class CallbackImpl implements Callback {

		@Override
		public void onCompletion(RecordMetadata rm, Exception ex) {
			if (ex == null) {
				String s = String.format("---onCompletion: Topic: %s, partition: %s, timestamp: %s, offset: %s",
						rm.topic(), rm.partition(), rm.timestamp(), rm.offset());
				System.out.println(s + ";  " + rm.toString());
			} else {
				ex.printStackTrace();
			}
		}

	}
}
