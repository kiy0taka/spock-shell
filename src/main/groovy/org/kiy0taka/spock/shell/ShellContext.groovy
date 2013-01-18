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
package org.kiy0taka.spock.shell

import com.sun.net.httpserver.HttpServer
import org.kiy0taka.spock.shell.server.MockFunctionHandler
import org.kiy0taka.spock.shell.server.ParamsFilter

import java.util.concurrent.Executors

/**
 * Context for ShellSpec.
 *
 * @author Kiyotaka Oku
 */
class ShellContext {

    Map<String, String> env = [:]
    List<File> mockScripts = []
    Map<String, MockFunction> mockFunctions = [:]
    boolean redirectErrorStream
    File workspace
    int serverPort
    ConfigObject config

    private HttpServer server

    void export(String name, String value) {
        env[name] = value
    }

    void addMockScript(String fileName) {
        mockScripts << new File(new File(config.script.dir), fileName)
    }

    void addMockFunction(String name, Closure func) {
        if (mockFunctions.isEmpty()) {
            startMockServer()
        }
        mockFunctions[name] = new MockFunction(name:name, func:func, ctx:this)
    }

    private void startMockServer() {
        server = HttpServer.create(new InetSocketAddress(0), 1)
        def handler = new MockFunctionHandler(mockFunctions:mockFunctions)
        def ctx = server.createContext('/', handler)
        ctx.filters << new ParamsFilter()
        server.executor = Executors.newSingleThreadExecutor()
        server.start()
        serverPort = server.address.port
    }

    void writeContext(Writer w) {
        mockScripts.each {
            w.println("source ${it.absolutePath}")
        }
        mockFunctions.values().each {
            w.println(it.toCommand())
        }
    }

    void close() throws IOException {
        server?.stop(0)
    }
}
