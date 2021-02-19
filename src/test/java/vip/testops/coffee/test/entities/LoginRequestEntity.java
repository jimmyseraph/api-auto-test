package vip.testops.coffee.test.entities;

/**
 * Account login request entity
 * @version 1.0
 * @author liudao
 */
public class LoginRequestEntity {
    /**
     * username of the user
     */
    private String username;
    /**
     * password of the user
     */
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
