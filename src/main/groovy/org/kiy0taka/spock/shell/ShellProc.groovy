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
/**
 * Execute shell command.
 *
 * @author Kiyotaka Oku
 */
class ShellProc {

    private ShellContext ctx

    int status
    String stdout
    String stderr

    @Lazy
    List<String> lines = { stdout.split('\n') }()

    void exec(String command) {

        def builder = new ProcessBuilder('sh')
            .directory(ctx.workspace)
            .redirectErrorStream(ctx.redirectErrorStream)
        builder.environment() << ctx.env

        def p = builder.start()
        p.out.withWriter { w ->
            w.println('set -a')
            ctx.writeContext(w)
            w.println(command)
            w.println('exit $?')
        }

        (status, stderr, stdout) = [p.waitFor(), p.err.text, p.text]

        println stdout
        System.err.println stderr
    }
}
