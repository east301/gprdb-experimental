package jp.ac.tohoku.ecei.sb.gprdb.controller;

import jp.ac.tohoku.ecei.sb.gprdb.AbstractLiveServerTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GenericControllerTest extends AbstractLiveServerTest {

    @Test
    public void test() throws Exception {
        //
        goToAndWaitLoading("/");

        //
        List<WebElement> elements = this.driver.findElements(By.tagName("h1"));
        assertThat(elements).hasSize(2);
        assertThat(elements.get(0).getText()).isEqualTo("gprdb");
        assertThat(elements.get(1).getText()).isEqualTo("Welcome to gprdb framework!");
    }

}
