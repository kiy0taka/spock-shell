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

import groovy.ui.SystemOutputInterceptor

/**
 * Groovy mock function.
 *
 * @author Kiyotaka Oku
 */
class MockFunction {

    ShellContext ctx
    String name
    Closure func

    String toCommand() {
        """\
        |function $name {
        |    local command len
        |    command="curl -s http://localhost:${ctx.serverPort}/$name"
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

    String exec(List args) {

        def stdout = new Out()
        def stderr = ctx.redirectErrorStream ? stdout : new Out(error:true)

        def interceptors = [
            new SystemOutputInterceptor({ stdout.append(it); false }),
            new SystemOutputInterceptor({ stderr.append(it); false }, false)
        ]
        interceptors*.start()

        try {
            func(args)
        } finally {
            interceptors*.stop()
        }

        if (ctx.redirectErrorStream) {
            stdout.toString()
        } else {
            [stdout, stderr]*.toString().grep().join('\n')
        }
    }

    static class Out {

        String out = ''
        boolean error

        void append(String str) {
            out += str
        }

        @Override
        String toString() {
            out ? """|cat ${error ? '>&2 ' : ''}<< EOF
                     |${out - ~/\n$/}
                     |EOF""".stripMargin() : out
        }
    }
}
