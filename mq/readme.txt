----配置----
--module.xml--
kafka.bootstrap_servers

----生产端----
一般生产者：MQProducer
幂等生产者：MQProducerWithIdempotence
事务生产者：MQProducerWithTransaction

/mq/MQProducer/send1.do
/mq/MQProducer/send2.do
/mq/MQProducer/send3.do

----消费端----
订阅：/mq/MQConsumer/subscribe.do
取消订阅：/mq/MQConsumer/unsubscribe.do

----消费者示例----
DemoConsumer