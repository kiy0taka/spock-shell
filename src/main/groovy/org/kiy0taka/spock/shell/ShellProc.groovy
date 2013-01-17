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
 * Execute shell command.
 *
 * @author Kiyotaka Oku
 */
class ShellProc {

    File dir
    Map<String, String> env
    List<String> mockScripts
    Map<String, Closure> mockFunctions
    String command
    int status
    String stdout
    String stderr
    HttpServer server

    @Lazy
    List<String> lines = { stdout.split('\n') }()

    boolean redirectErrorStream

    void exec() {

        try {

            startMockServer()

            def builder = new ProcessBuilder('sh')
                .directory(dir)
                .redirectErrorStream(redirectErrorStream)
            builder.environment() << env

            def p = builder.start()
            p.out.withWriter { w ->
                w.println('set -a')

                setupMockScripts(w)
                setupMockFunctions(w)

                w.println(command)
                w.println('exit $?')
            }

            (status, stderr, stdout) = [p.waitFor(), p.err.text, p.text]

            println stdout
            System.err.println stderr

        } finally {
            server?.stop(0)
        }

    }

    void setupMockScripts(Writer w) {
        mockScripts.each {
            w.println("source $it")
        }
    }

    void setupMockFunctions(Writer w) {
        mockFunctions?.keySet()?.each { name ->
            w.println """\
                    |function $name {
                    |    local command len
                    |    command="curl -s http://localhost:${server.address.port}/$name"
                    |    len=\$#
                    |    for ((i=0; i<\${len}; i++))
                    |    do
                    |        command="\$command --data-urlencode args\${i}=\$1"
                    |        shift
                    |    done
                    |    exec \$command | sh
                    |}
                    |""".stripMargin()
        }
    }

    void startMockServer() {
        server = HttpServer.create(new InetSocketAddress(0), 1)
        def handler = new MockFunctionHandler(mockFunctions:mockFunctions.collectEntries { name, mock ->
            [(name):new MockFunction(func:mock, redirectErrorStream:redirectErrorStream)]
        })
        def ctx = server.createContext('/', handler)
        ctx.filters << new ParamsFilter()
        server.executor = Executors.newSingleThreadExecutor()
        server.start()
    }
}
