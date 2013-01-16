package org.kiy0taka.spock.shell.server

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.Filter.Chain
import com.sun.net.httpserver.HttpExchange

/**
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
