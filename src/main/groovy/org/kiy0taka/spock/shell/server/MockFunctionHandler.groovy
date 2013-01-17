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

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import org.kiy0taka.spock.shell.MockFunction

/**
 * HttpHandler implementation for groovy mock function.
 *
 * @author Kiyotaka Oku
 */
class MockFunctionHandler implements HttpHandler {

    Map<String, MockFunction> mockFunctions

    @Override
    void handle(HttpExchange exchange) throws IOException {

        String commandName = exchange.requestURI.path[1..-1]
        List args = exchange.getAttribute('args')
        def result = mockFunctions[commandName].exec(args)

        exchange.sendResponseHeaders(200, 0)
        exchange.responseBody.withWriter {
            it.println(result)
        }
    }
}
