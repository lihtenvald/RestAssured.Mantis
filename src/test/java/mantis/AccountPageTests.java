package mantis;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class AccountPageTests {

    private String MANTIS_secure_session;
    private String PHPSESSID;
    private String MANTIS_STRING_COOKIE;
    private Map<String, String> cookies = new HashMap<>();

    @BeforeEach
    public void getCookies() {

        Response responseLogin = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .body("return=%2Fmantisbt%2Fmy_view_page.php&username=admin&password=admin20&secure_session=on")
                .post("https://academ-it.ru/mantisbt/login.php")
                .andReturn();

        MANTIS_secure_session = responseLogin.cookie("MANTIS_secure_session");
        PHPSESSID = responseLogin.cookie("PHPSESSID");
        MANTIS_STRING_COOKIE = responseLogin.cookie("MANTIS_STRING_COOKIE");

        System.out.println("MANTIS_secure_session: " + MANTIS_secure_session);
        System.out.println("PHPSESSID: " + PHPSESSID);
        System.out.println("MANTIS_STRING_COOKIE: " + MANTIS_STRING_COOKIE);

        cookies.put("MANTIS_secure_session", MANTIS_secure_session);
        cookies.put("PHPSESSID", PHPSESSID);
        cookies.put("MANTIS_STRING_COOKIE", MANTIS_STRING_COOKIE);
    }

    @Test
    public void getAccountPageTest() {
        Response response = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Respons: \n");
        response.prettyPrint();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().asString().contains("Real Name"));
    }

    @Test
    public void postChangeValueRealName() {
        String realName = "admin1";
        Response response = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies)
                .body("realname=" + realName)
                .post("https://academ-it.ru/mantisbt/account_update.php")
                .andReturn();

        System.out.println("Status Code response :" + response.statusCode());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().asString().contains("Real name successfully updated"));

        Response responsAccountPage = RestAssured
                .given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();

        System.out.println("Status Code responsAcountPage: " + responsAccountPage.statusCode());
        System.out.println("Response Account Page: \n" + responsAccountPage.prettyPrint());

        Assertions.assertEquals(200, responsAccountPage.statusCode());
        Assertions.assertTrue(responsAccountPage.body().asString().contains(realName));
    }


}
