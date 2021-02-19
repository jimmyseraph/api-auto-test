package vip.testops.apitest.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.testops.apitest.config.EnvConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqUtil {

    private static final Logger logger = LoggerFactory.getLogger(MqUtil.class);

    private String hostname;
    private int port;
    private String username;
    private String password;
    private String queueName;
    private String exchangeName;

    public MqUtil(){
        this.hostname = EnvConfig.ENV.get("mq.coffee.hostname");
        this.port = Integer.parseInt(EnvConfig.ENV.get("mq.coffee.port"));
        this.username = EnvConfig.ENV.get("mq.coffee.username");
        this.password = EnvConfig.ENV.get("mq.coffee.password");
        this.queueName = EnvConfig.ENV.get("mq.coffee.queueName");
        this.exchangeName = EnvConfig.ENV.get("mq.coffee.exchangeName");
    }

    public Connection getConnection(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        Connection conn = null;
        try {
            conn = factory.newConnection();
        } catch (IOException e) {
            logger.error("Cannot create connection to the specified mq server at {}:{}", hostname, port);
            e.printStackTrace();
        } catch (TimeoutException e) {
            logger.error("Connect failed: timeout!");
            e.printStackTrace();
        }
        return conn;
    }

    public Channel queueBind(Connection conn){
        Channel channel = null;
        try {
            channel = conn.createChannel();
            channel.queueBind(queueName, exchangeName, "#");
        } catch (IOException e) {
            logger.error("Cannot create mq channel.");
            e.printStackTrace();
        }
        return channel;
    }

    public void close(Connection conn, Channel channel){
        if(channel != null){
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
