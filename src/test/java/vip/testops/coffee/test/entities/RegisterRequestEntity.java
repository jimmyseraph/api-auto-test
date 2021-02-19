package vip.testops.coffee.test.entities;

/**
 * Account register request entity
 * @version 1.0
 * @author liudao
 */
public class RegisterRequestEntity {
    /**
     * username of the user
     */
    private String username;
    /**
     * password of the user
     */
    private String password;
    /**
     * confirm password
     */
    private String password2;
    /**
     * the gender of the user
     */
    private String gender;
    /**
     * the cellphone number of the user
     */
    private String cellphone;

    public RegisterRequestEntity() {
    }

    public RegisterRequestEntity(String username, String password, String password2, String gender, String cellphone) {
        this.username = username;
        this.password = password;
        this.password2 = password2;
        this.gender = gender;
        this.cellphone = cellphone;
    }

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

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
}
