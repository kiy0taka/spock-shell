/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kiy0taka.spock.shell.server

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.Filter.Chain
import com.sun.net.httpserver.HttpExchange

/**
 * Parse request parameters to arguments for groovy mock function.
 *
 * @author Kiyotaka Oku
 */
class ParamsFilter extends Filter {

    @Override
    void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        if (httpExchange.requestMethod.equalsIgnoreCase('POST')) {
            String query = httpExchange.getRequestBody().readLines()[0]
            httpExchange.setAttribute('args', parseQuery(query))
        }
        chain.doFilter(httpExchange)
    }

    List parseQuery(String query) {
        query.split('&').inject([]) { list, pair ->
            if (pair.indexOf('=') > 0) {
                def (key, val) = pair.split('=', 2)
                def m = key =~ /args(\d+)/
                if (m) {
                    list[m[0][1].toInteger()] = URLDecoder.decode(val, 'UTF-8')
                }
            }
            list
        }.collect { it ?: '' }
    }

    @Override
    String description() {
        return ParamsFilter.name
    }
}
