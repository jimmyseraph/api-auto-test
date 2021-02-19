package vip.testops.apitest.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.testops.apitest.config.EnvConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RabbitMqConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConsumer.class);
    public String recMsg = null;
    public String queueName;

    private final Channel channel;

    public RabbitMqConsumer(Channel channel){
        this.channel = channel;
        this.queueName = EnvConfig.ENV.get("mq.coffee.queueName");
    }

    @Override
    public void run() {
        try {
            channel.basicConsume(queueName, false, "tag_for_test",new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, StandardCharsets.UTF_8);
                    logger.info("Recieve mq message: {}", msg);
                    recMsg = msg;
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (IOException e) {
            logger.error("Cannot create mq channel.");
            e.printStackTrace();
        }
    }
}
