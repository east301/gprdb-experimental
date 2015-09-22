package jp.ac.tohoku.ecei.sb.gprdb;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.NotEmpty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Shu Tadaka
 */
@SpringApplicationConfiguration(classes = ApplicationSource.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractLiveServerTest extends AbstractTestNGSpringContextTests {

    private static final String BROWSER = getProperty("selenium.browser");
    private static final String PHANTOM_JS = findPhantomJs();

    @SneakyThrows
    private static String getProperty(@NotEmpty String key) {
        Properties properties = new Properties();
        try (InputStream stream = Resources.getResource("selenium.properties").openStream()) {
            properties.load(stream);
            return properties.getProperty(key);
        }
    }

    @SneakyThrows(IOException.class)
    private static String findPhantomJs() {
        Set<String> targets = ImmutableSet.of("phantomjs", "phantomjs.exe");
        return Files.walk(Paths.get(getProperty("selenium.projectDirectory")))
            .filter(Files::isRegularFile)
            .filter(p ->  targets.contains(p.getFileName().toString()))
            .map(p -> p.normalize().toString())
            .findFirst()
            .orElse(null);
    }

    @Value("${local.server.port}")
    protected int port;

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        switch (BROWSER) {
            case "firefox":
                this.driver = new FirefoxDriver();
                break;

            case "phantomjs":
            default:
                this.driver = new PhantomJSDriver(new DesiredCapabilities(ImmutableMap.of(
                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    PHANTOM_JS
                )));
                break;
        }
    }

    @AfterMethod
    public void tearDown() {
        this.driver.quit();
    }

    @NotEmpty
    protected String getUrl(@NotEmpty String suffix) {
        return String.format("http://127.0.0.1:%d%s", this.port, suffix);
    }

    protected void goTo(@NotEmpty String urlSuffix) {
        this.driver.get(getUrl(urlSuffix));
    }

    protected void goToAndWaitLoading(@NotEmpty String urlSuffix) {
        this.driver.get(getUrl(urlSuffix));
        this.driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
    }

    @NotNull
    protected WebElement waitElement(@NotNull By by) {
        return new WebDriverWait(this.driver, 30).until((WebDriver d) -> d.findElement(by));
    }

}
