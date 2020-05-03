package com.flying.mq.demo;

import java.util.List;

import com.flying.common.util.Codes;
import com.flying.common.util.KeyValuePair;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.mq.consumer.MQConsumer;

@Service("MQConsumer.demo")
public class DemoConsumer {
	
	public ActionResult consume1(
			@Param(value=MQConsumer.CONSUMER_FIELD_TOPIC, desc="����") String topic,
			@Param(value=MQConsumer.CONSUMER_FIELD_KEY, desc="Key") String key,
			@Param(value=MQConsumer.CONSUMER_FIELD_MESSAGE, desc="��Ϣ") String message,
			@Param(value=MQConsumer.CONSUMER_FIELD_OFFSET, desc="λ��") long offset,
			@Param(value=MQConsumer.CONSUMER_FIELD_PARTITION, desc="����") int partition,
			@Param(value=MQConsumer.CONSUMER_FIELD_TIMESTAMP, desc="ʱ���") long timestamp,
			@Param(value=MQConsumer.CONSUMER_FIELD_HEADERS, desc="ͷ����") List<KeyValuePair<String, byte[]>> headers) {

		System.out.println(String.format("DemoConsumer.consume1  --->>>  Topic: %s, Partition: %s, Key: %s, Offset: %s, Message: %s", 
				topic, partition, key, offset, message));
		
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}
	
	public ActionResult consume2(
			@Param(value=MQConsumer.CONSUMER_FIELD_KEY, desc="Key") String key,
			@Param(value=MQConsumer.CONSUMER_FIELD_MESSAGE, desc="��Ϣ") String message,
			@Param(value=MQConsumer.CONSUMER_FIELD_HEADERS, desc="ͷ����") List<KeyValuePair<String, byte[]>> headers) {

		System.out.println(String.format("DemoConsumer.consume2  --->>>  Key: %s, Message: %s", 
				key, message));
		
		return ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	}
}
