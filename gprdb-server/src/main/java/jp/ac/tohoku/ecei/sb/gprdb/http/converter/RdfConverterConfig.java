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

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Shu Tadaka
 */
@Configuration
@ConditionalOnWebApplication
public class RdfConverterConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //
        configurer.favorPathExtension(true);

        //
        for (MediaType mimeType : RdfHttpMessageConverterImpl.MEDIA_TYPES) {
            RDFFormat format = Rio.getParserFormatForMIMEType(mimeType.toString());
            for (String extension : format.getFileExtensions()) {
                configurer.mediaType(extension, mimeType);
            }
        }
    }

}
