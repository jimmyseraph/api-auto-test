package vip.testops.coffee.test;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vip.testops.apitest.config.EnvConfig;
import vip.testops.apitest.db.DBUtil;
import vip.testops.apitest.db.JdbcUtil;
import vip.testops.apitest.http.EasyRequest;
import vip.testops.apitest.http.EasyResponse;
import vip.testops.apitest.http.impl.OkHttpRequest;
import vip.testops.coffee.test.entities.RegisterRequestEntity;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private static String username;
    private static String password;
    private static Gson gson;

    public static final String LOGIN_PATH = "/account/login";
    public static final String REGISTER_PATH = "/account/register";
    public static final String TOKEN_PATH = "/account/token";

    @BeforeAll
    public static void init(){
        String time = System.currentTimeMillis() % 1000 + "";
        String rnd = (int)(Math.random() * 900 + 100) + "";
        username = "test" + rnd + time;
        password = "test12345678";
        gson = new Gson();
    }

    @Test
    @DisplayName("Register success")
    public void testRegister_success(){
        String url = EnvConfig.ENV.get("api.baseUrl");
        if(url == null){
            fail("There is no api base url available!");
        }
        url = url + EnvConfig.ENV.get("api.basePath") + REGISTER_PATH;
        EasyRequest easyRequest = new OkHttpRequest();

        RegisterRequestEntity registerRequestEntity = new RegisterRequestEntity();
        registerRequestEntity.setUsername(username);
        registerRequestEntity.setPassword(password);
        registerRequestEntity.setPassword2(password);
        registerRequestEntity.setCellphone("13312312345");
        registerRequestEntity.setGender("M");
        String body = gson.toJson(registerRequestEntity);
        try {
            EasyResponse easyResponse = easyRequest.setUrl(url)
                    .setMethod("POST")
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
                    "select * from t_account where accountName = ?",
                    username
            );
            JdbcUtil.closeAll(null, null, conn);
            assertNotNull(resMap);
            assertFalse(resMap.isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error while sending request");
        }
    }

    @DisplayName("Register failed")
    @ParameterizedTest(name = "[{index}] expected: {1}")
    @MethodSource("registerRequestEntityProvider")
    public void testRegister_Fail(RegisterRequestEntity registerRequestEntity, Map<String, Object> expectedMap){
        String url = EnvConfig.ENV.get("api.baseUrl");
        if(url == null){
            fail("There is no api base url available!");
        }
        url = url + EnvConfig.ENV.get("api.basePath") + REGISTER_PATH;
        EasyRequest easyRequest = new OkHttpRequest();
        String body = gson.toJson(registerRequestEntity);
        try {
            EasyResponse easyResponse = easyRequest.setUrl(url)
                    .setMethod("POST")
                    .setBody(EasyRequest.JSON, body)
                    .execute();
            for(Map.Entry<String, Object> entry : expectedMap.entrySet()){
                assertEquals(entry.getValue(), JsonPath.read(easyResponse.getBody(), entry.getKey()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error while sending request");
        }
    }

    public static Stream<Arguments> registerRequestEntityProvider(){
        Map<String, Object> map1 = new HashMap<>();
        map1.put("$.code", 2002);
        map1.put("message", "username must form with a-zA-Z_0-9, and the length must between 6 and 12");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("$.code", 2002);
        map2.put("message", "username must form with a-zA-Z_0-9, and the length must between 6 and 12");
        Map<String, Object> map3 = new HashMap<>();
        map3.put("$.code", 2001);
        map3.put("message", "Confirm password is different from password.");
        return Stream.of(
                Arguments.arguments(new RegisterRequestEntity("a", "a12345678", "a12345678", "M", "13511111111"), map1), // username is too short
                Arguments.arguments(new RegisterRequestEntity("a123456789123456789", "a12345678", "a12345678", "M", "13511111111"), map2), // username is too long
                Arguments.arguments(new RegisterRequestEntity("a12345678", "a12345678", "a123456789", "M", "13511111111"), map3) // password not same
        );
    }
}
