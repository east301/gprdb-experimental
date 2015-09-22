package jp.ac.tohoku.ecei.sb.gprdb.system.coding;

import jp.ac.tohoku.ecei.sb.gprdb.coding.Ref;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class RefTest {

    ///
    /// constructor, get, set
    ///

    @Test
    public void test_get_and_set() {
        //
        Ref<Integer> ref = new Ref<>();
        assertThat(ref.get()).isNull();

        ref.set(1);
        assertThat(ref.get()).isEqualTo(1);

        ref.set(2);
        assertThat(ref.get()).isEqualTo(2);

        //
        ref = new Ref<>(3);
        assertThat(ref.get()).isEqualTo(3);
    }

}
