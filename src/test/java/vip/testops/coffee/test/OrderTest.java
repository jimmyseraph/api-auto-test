package vip.testops.coffee.test;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.testops.apitest.config.EnvConfig;
import vip.testops.apitest.db.DBUtil;
import vip.testops.apitest.db.JdbcUtil;
import vip.testops.apitest.http.EasyRequest;
import vip.testops.apitest.http.EasyResponse;
import vip.testops.apitest.http.impl.OkHttpRequest;
import vip.testops.apitest.mq.MqUtil;
import vip.testops.apitest.mq.RabbitMqConsumer;
import vip.testops.coffee.test.entities.LoginRequestEntity;
import vip.testops.coffee.test.entities.OrderCreateRequestEntity;
import vip.testops.coffee.test.entities.OrderItemRequestEntity;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private static final Logger logger = LoggerFactory.getLogger(OrderTest.class);

    private static final String username = "liudao001";
    private static final String password = "12345678";
    private static String token;
    private static Gson gson;
    public static final String ORDER_NEW_PATH = "/order/new";
    public static String baseUrl;
    public static final Object lockObj = EnvConfig.LOCK_OBJECT;
    private String mqMessage;

    public void setMqMessage(String mqMessage) {
        this.mqMessage = mqMessage;
    }

    @BeforeAll
    public static void init(){
        gson = new Gson();
        // do login
        String url = EnvConfig.ENV.get("api.baseUrl");
        if(url == null){
            fail("There is no api base url available!");
        }
        baseUrl = url + EnvConfig.ENV.get("api.basePath");
        String login_url = baseUrl + AccountTest.LOGIN_PATH;
        String token_url = baseUrl + AccountTest.TOKEN_PATH;
        EasyRequest easyRequest = new OkHttpRequest();
        LoginRequestEntity loginRequestEntity = new LoginRequestEntity();
        loginRequestEntity.setUsername(username);
        loginRequestEntity.setPassword(password);
        String login_body = gson.toJson(loginRequestEntity);
        logger.info("Do login........");
        try {
            EasyResponse easyResponse = easyRequest.setUrl(login_url)
                    .setMethod("POST")
                    .setBody(EasyRequest.JSON, login_body)
                    .execute();
            // check login response
            assertEquals(200, easyResponse.getCode());
            assertEquals(1000, (Integer) JsonPath.read(easyResponse.getBody(), "$.code"));
            String code = JsonPath.read(easyResponse.getBody(), "$.data.code");

            // do exchange code for token
            easyRequest = new OkHttpRequest();
            easyResponse = easyRequest.setUrl(token_url)
                    .setMethod("GET")
                    .addQueryParam("code", code)
                    .execute();
            // check if get the token
            assertEquals(200, easyResponse.getCode());
            assertEquals(1000, (Integer) JsonPath.read(easyResponse.getBody(), "$.code"));
            token = JsonPath.read(easyResponse.getBody(), "$.data.token");
        } catch (IOException e) {
            fail("Error while sending request");
            e.printStackTrace();
        }
    }

    @DisplayName("Add an order success")
    @Test
    public void testAddOrder_success(){
        // get an Object locker

        String new_url = baseUrl + ORDER_NEW_PATH;
        synchronized (lockObj){
            // start mq consumer
            MqUtil mqUtil = new MqUtil();
            com.rabbitmq.client.Connection mqConnection = mqUtil.getConnection();
            Channel channel = mqUtil.queueBind(mqConnection);
            RabbitMqConsumer rabbitMqConsumer = new RabbitMqConsumer(channel);
            Thread t = new Thread(rabbitMqConsumer);
            t.start();

            // build test data
            OrderItemRequestEntity[] orderItemRequestEntities = new OrderItemRequestEntity[]{
                    new OrderItemRequestEntity(3, 4),
                    new OrderItemRequestEntity(4, 1)
            };
            String address = "Shanghai." + System.currentTimeMillis();
            OrderCreateRequestEntity orderCreateRequestEntity = new OrderCreateRequestEntity(address, orderItemRequestEntities);
            String body = gson.toJson(orderCreateRequestEntity);
            EasyRequest easyRequest = new OkHttpRequest();
            try {
                EasyResponse easyResponse = easyRequest.setUrl(new_url)
                        .setMethod("POST")
                        .addHeader("Access-Token", token)
                        .setBody(EasyRequest.JSON, body)
                        .execute();
                // check response
                assertEquals(200, easyResponse.getCode());
                assertEquals(1000, (Integer) JsonPath.read(easyResponse.getBody(), "$.code"));
                // check db
                Connection conn = JdbcUtil.getConnection(
                        EnvConfig.ENV.get("db.coffee.url"),
                        EnvConfig.ENV.get("db.coffee.username"),
                        EnvConfig.ENV.get("db.coffee.password")
                );
                Map<String, Object> resMap = DBUtil.getOne(
                        conn,
                        "select * from t_order where address = ?",
                        address
                );
                JdbcUtil.closeAll(null, null, conn);
                assertNotNull(resMap);
                assertFalse(resMap.isEmpty());
                // check mq
                for(int i = 0; i < 10; i++){
                    if(rabbitMqConsumer.recMsg == null){
                        Thread.sleep(1000);
                    } else {
                        break;
                    }
                }
                mqUtil.close(mqConnection, channel);
                assertEquals(body, rabbitMqConsumer.recMsg);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                fail("Error while sending request");
            }
        }

    }
}
