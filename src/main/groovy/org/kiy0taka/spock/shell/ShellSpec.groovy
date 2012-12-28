package org.kiy0taka.spock.shell

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class ShellSpec extends Specification {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder()

    @Shared
    static ConfigObject config = {
        def r = Thread.currentThread().contextClassLoader.loadClass('ShellSpecConfig')
        new ConfigSlurper().parse(r)
    }()

    int status
    String stdout
    String stderr
    List<String> env = []
    List<String> mockScripts = []
    ShellProc currentProc

    File getWorkspace() {
        tempFolder.root
    }

    void export(String name, String value) {
        env << "${name}=${value}"
    }

    void mockScript(String script) {
        mockScripts << new File(config.script.dir, script).absolutePath
    }

    void exec(String command) {
        currentProc = new ShellProc(dir:tempFolder.root, env:env, mockScripts:mockScripts, command:command)
        currentProc.exec()
    }

    void run(String scriptName, String... args) {
        exec([new File(config.script.dir, scriptName).absolutePath, *args].join(' '))
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
}
