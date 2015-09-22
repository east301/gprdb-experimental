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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Shu Tadaka
 */
@Component
@ConditionalOnWebApplication
@Primary
class CachedGenePairRelationAccessorFactoryImpl implements GenePairRelationAccessorFactory {

    private final GenePairRelationAccessorFactory parent;
    private final LoadingCache<String, GenePairRelationReaderCacheWrapper> readerCache;

    @Autowired
    public CachedGenePairRelationAccessorFactoryImpl(
        @NotNull GenePairRelationAccessorFactory parent,
        @Min(0) @Value("${gprdb.server.genePairRelationReader.cacheSeconds:300}") int cacheSeconds) {

        this.parent = parent;
        this.readerCache = createReaderCache(cacheSeconds);
    }

    private LoadingCache<String, GenePairRelationReaderCacheWrapper> createReaderCache(int cacheSeconds) {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(cacheSeconds, TimeUnit.SECONDS)
            .removalListener(rem -> ((GenePairRelationReaderCacheWrapper)rem.getValue()).dispose())
            .build(new CacheLoader<String, GenePairRelationReaderCacheWrapper>() {
                @Override
                public GenePairRelationReaderCacheWrapper load(String key) {
                    String id = key.split("/", 2)[1];
                    return new GenePairRelationReaderCacheWrapper(parent.newReader(id));
                }
            });
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void cleanReaderCache() {
        this.readerCache.cleanUp();
    }

    @Override
    public GenePairRelationReader newReader(@NotNull String id) {
        String key = Thread.currentThread().getName().replace("/", "_") + "/" + id;
        return this.readerCache.getUnchecked(key);
    }

    @Override
    public GenePairRelationWriter newWriter(
            @NotNull String id, @NotNull List<GenePairRelationType> types, @NotNull List<Identifier> genes) {

        return this.parent.newWriter(id, types, genes);
    }

}
