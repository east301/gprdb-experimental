/**
 * gprdb - a simple and flexible framework for gene-pair relation database construction
 * Copyright 2015 gprdb developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jp.ac.tohoku.ecei.sb.gprdb.http.converter;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class RdfHttpMessageConverterImplTest {

    ///
    /// setup
    ///

    private RdfHttpMessageConverterImpl converter;

    @BeforeMethod
    public void setUp() {
        converter = new RdfHttpMessageConverterImpl();
    }

    ///
    /// canRead
    ///

    @Test
    public void canRead_returns_false()  {
        assertThat(converter.canRead(Object.class, MediaType.TEXT_HTML)).isFalse();
    }

    ///
    /// canWrite
    ///

    @DataProvider
    public Object[][] canWrite_returns_correct_result_data() {
        return new Object[][]{
            {Model.class, "application/rdf+xml", true},
            {Model.class, "text/n3", true},
            {null, "application/rdf+xml", false},
            {null, "text/n3", false},
            {null, "application/json", false},
            {Object.class, "application/rdf+xml", false},
            {Object.class, "application/json", false},
        };
    }

    @Test(dataProvider = "canWrite_returns_correct_result_data")
    public void canWrite_returns_correct_result(Class<?> clazz, String mediaType, boolean result) {
        assertThat(converter.canWrite(clazz, MediaType.parseMediaType(mediaType))).isEqualTo(result);
    }

    ///
    /// getSupportedMediaTypes
    ///

    @Test
    public void test_getSupportedMediaTypes() {
        assertThat(converter.getSupportedMediaTypes())
            .hasSize(2)
            .contains(MediaType.parseMediaType("application/rdf+xml"), MediaType.parseMediaType("text/n3"));
    }

    ///
    /// read
    ///

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_read_throws_an_exception(@Mocked HttpInputMessage message) {
        converter.read(Model.class, message);
    }

    ///
    /// write
    ///

    @Test
    public void test_write(
        @Mocked MediaType contentType,
        @Mocked RDFFormat format,
        @Mocked RDFWriter writer,
        @Mocked HttpOutputMessage message,
        @Mocked OutputStream stream,
        @Mocked Model model)
        throws IOException {

        AtomicReference<String> specifiedMimeType = new AtomicReference<>();
        AtomicReference<RDFFormat> specifiedRdfFormat = new AtomicReference<>();
        AtomicReference<OutputStream> specifiedOutputStream = new AtomicReference<>();

        new MockUp<Rio>() {
            @Mock
            RDFFormat getParserFormatForMIMEType(String mimeType) {
                specifiedMimeType.set(mimeType);
                return format;
            }

            @Mock
            RDFWriter createWriter(RDFFormat format, OutputStream stream) {
                specifiedRdfFormat.set(format);
                specifiedOutputStream.set(stream);
                return writer;
            }
        };

        new NonStrictExpectations() {{
            message.getBody(); result = stream;
        }};

        converter.write(model, contentType, message);

        assertThat(specifiedMimeType.get()).isNotNull().isEqualTo(contentType.toString());
        assertThat(specifiedRdfFormat.get()).isNotNull().isSameAs(format);
        assertThat(specifiedOutputStream.get()).isNotNull().isSameAs(stream);

        new Verifications() {{
            message.getBody(); times = 1;
            writer.startRDF(); times = 1;
            writer.endRDF(); times = 1;
        }};

    }

}
