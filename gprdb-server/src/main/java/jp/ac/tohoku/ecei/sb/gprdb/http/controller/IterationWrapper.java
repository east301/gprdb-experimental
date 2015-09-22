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

package jp.ac.tohoku.ecei.sb.gprdb.http.controller;

import lombok.RequiredArgsConstructor;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResult;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class IterationWrapper implements Iterable<BindingSet>, Iterator<BindingSet> {

    private final QueryResult<BindingSet> result;

    @Override
    public Iterator<BindingSet> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.result.hasNext();
    }

    @Override
    public BindingSet next() {
        if (!this.result.hasNext()) {
            throw new NoSuchElementException();
        }

        return this.result.next();
    }

}
