package jp.ac.tohoku.ecei.sb.gprdb.dataset;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class DatasetObjectNotFoundExceptionTest {

    ///
    /// constructor
    ///

    @Test
    public void test_constructor() {
        DatasetObjectNotFoundException ex = new DatasetObjectNotFoundException("foo", "bar");
        assertThat(ex.getMessage()).isEqualTo("foo not found: bar");
        assertThat(ex.getType()).isEqualTo("foo");
        assertThat(ex.getId()).isEqualTo("bar");
    }

    ///
    /// identifierNofFound
    ///

    @Test
    public void idnetifierNotFound_returns_correct_result() {
        DatasetObjectNotFoundException ex = DatasetObjectNotFoundException.identifierNotFound("baz");
        assertThat(ex.getMessage()).isEqualTo("identifier not found: baz");
        assertThat(ex.getType()).isEqualTo("identifier");
        assertThat(ex.getId()).isEqualTo("baz");
    }

    ///
    /// identifierDatasetNofFound
    ///

    @Test
    public void idnetifierDatasetNotFound_returns_correct_result() {
        DatasetObjectNotFoundException ex = DatasetObjectNotFoundException.identifierDatasetNotFound("baz");
        assertThat(ex.getMessage()).isEqualTo("identifier dataset not found: baz");
        assertThat(ex.getType()).isEqualTo("identifier dataset");
        assertThat(ex.getId()).isEqualTo("baz");
    }

    ///
    /// genePairRelationDatasetNofFound
    ///

    @Test
    public void genePairRelationDatasetNotFound_returns_correct_result() {
        DatasetObjectNotFoundException ex = DatasetObjectNotFoundException.genePairRelationDatasetNotFound("baz");
        assertThat(ex.getMessage()).isEqualTo("gene-pair relation dataset not found: baz");
        assertThat(ex.getType()).isEqualTo("gene-pair relation dataset");
        assertThat(ex.getId()).isEqualTo("baz");
    }

    ///
    /// genePairRelationTypeNofFound
    ///

    @Test
    public void genePairRelationTypeNotFound_returns_correct_result() {
        DatasetObjectNotFoundException ex = DatasetObjectNotFoundException.genePairRelationTypeNotFound("baz");
        assertThat(ex.getMessage()).isEqualTo("gene-pair relation type not found: baz");
        assertThat(ex.getType()).isEqualTo("gene-pair relation type");
        assertThat(ex.getId()).isEqualTo("baz");
    }

}
