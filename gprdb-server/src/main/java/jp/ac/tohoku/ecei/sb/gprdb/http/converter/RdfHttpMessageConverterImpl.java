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

import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of {@link HttpMessageConverter} which converts {@link Model} to http response.
 *
 * @author Shu Tadaka
 */
@Component
@ConditionalOnWebApplication
@SuppressWarnings("rawtypes")
class RdfHttpMessageConverterImpl implements HttpMessageConverter<Model> {

    private static final String[] MEDIA_TYPE_STRINGS = new String[]{"application/rdf+xml", "text/n3"};

    public static final List<MediaType> MEDIA_TYPES = Arrays
        .stream(MEDIA_TYPE_STRINGS)
        .map(MediaType::parseMediaType)
        .collect(Collectors.toList());

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return MEDIA_TYPES.contains(mediaType)
            && clazz != null
            && Model.class.isAssignableFrom(clazz);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return MEDIA_TYPES;
    }

    @Override
    public Model read(Class<? extends Model> clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(Model model, MediaType contentType, HttpOutputMessage outputMessage)
        throws IOException {

        //
        RDFFormat format = Rio.getParserFormatForMIMEType(contentType.toString());

        //
        try (OutputStream stream = outputMessage.getBody()) {
            RDFWriter writer = Rio.createWriter(format, stream);
            writer.startRDF();
            model.forEach(writer::handleStatement);
            writer.endRDF();
        }
    }

}
