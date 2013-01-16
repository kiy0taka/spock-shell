package org.kiy0taka.spock.shell.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import org.kiy0taka.spock.shell.MockFunction

/**
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
