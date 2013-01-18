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
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestName
import org.kiy0taka.spock.shell.server.MockFunctionHandler
import org.kiy0taka.spock.shell.server.ParamsFilter
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Executors

/**
 * Spock extension for shell script.
 *
 * @author Kiyotaka Oku
 * @author Tsuyoshi Yamamoto
 */
class ShellSpec extends Specification {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder()

    @Rule
    public TestName testName = new TestName()

    @Shared
    private static ConfigObject config = {
        def r = Thread.currentThread().contextClassLoader.loadClass('ShellSpecConfig')
        new ConfigSlurper().parse(r)
    }()

    private ShellContext ctx
    private ShellProc currentProc

    void setup() {
        ctx = new ShellContext(workspace:tempFolder.root, config:config)
    }

    void cleanup() {
        def reportDir = new File(config.report.dir ?: 'build/reports/spock-shell')
        def dir = new File(new File(reportDir, getClass().canonicalName.replaceAll(/\./, '/')), testName.methodName)
        new AntBuilder().copy(todir:dir.absolutePath) {
            fileset(dir:workspace.absolutePath)
        }
        ctx.close()
    }

    File getWorkspace() {
        ctx.workspace
    }

    void export(String name, String value) {
        ctx.export(name, value)
    }

    void mockScript(String script) {
        ctx.addMockScript(script)
    }

    void mockFunction(String name, Closure mock) {
        ctx.addMockFunction(name, mock)
    }

    void exec(String command) {
        currentProc = new ShellProc(ctx:ctx)
        currentProc.exec(command)
    }

    void run(String scriptName, String... args) {
        exec([new File(config.script.dir, scriptName).absolutePath, *args].join(' '))
    }

    void resources(String path){
        def resourcesPath = getClass().getResource("/${path}")
        if (!resourcesPath) throw new FileNotFoundException("resources directory [${path}] not found.")

        def dir = new File(resourcesPath.toURI())
        if (dir.isDirectory()) {
            new AntBuilder().copy(todir:workspace.absolutePath) {
                fileset(dir:dir.absolutePath)
            }
        }
    }

    int getStatus() {
        currentProc.status
    }

    String getStdout() {
        currentProc.stdout
    }

    String getStderr() {
        currentProc.stderr
    }

    List<String> getLines() {
        currentProc.lines
    }

    void redirectErrorStream(boolean redirectErrorStream) {
        ctx.redirectErrorStream = redirectErrorStream
    }

}
