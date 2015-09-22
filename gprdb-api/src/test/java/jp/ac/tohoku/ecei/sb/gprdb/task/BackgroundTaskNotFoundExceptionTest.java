package jp.ac.tohoku.ecei.sb.gprdb.task;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class BackgroundTaskNotFoundExceptionTest {

    ///
    /// constructor
    ///

    @Test
    public void constructor_sets_correct_error_message() {
        assertThat(new BackgroundTaskNotFoundException("foo"))
            .hasMessage("background task not found: foo");
    }

}
