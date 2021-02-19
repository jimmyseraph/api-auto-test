package vip.testops.apitest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    public static Map<String, String> ENV = new HashMap<>();
    public static Object LOCK_OBJECT;

    static {
        LOCK_OBJECT = new Object();
        String prof = System.getProperty("api.env", "default");
        String configFileName = "env-" + prof + ".properties";
        InputStream is = EnvConfig.class.getClassLoader().getResourceAsStream(configFileName);
        Properties prop = new Properties();
        try {
            if (is != null) {
                prop.load(is);
                for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                    ENV.put(entry.getKey().toString(), entry.getValue().toString());
                }
                is.close();
            } else {
                logger.error("Cannot find {} properties file", configFileName);
            }
        } catch (IOException e) {
            logger.error("Cannot load {} properties file", configFileName);
            e.printStackTrace();
        }
    }

}
