package org.kiy0taka.spock.shell

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class ShellSpec extends Specification {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder()

    @Shared
    private static ConfigObject config = {
        def r = Thread.currentThread().contextClassLoader.loadClass('ShellSpecConfig')
        new ConfigSlurper().parse(r)
    }()

    private Map<String, String> env = [:]
    private List<String> mockScripts = []
    private ShellProc currentProc
    private boolean redirectErrorStream

    File getWorkspace() {
        tempFolder.root
    }

    void export(String name, String value) {
        env[name] = value
    }

    void mockScript(String script) {
        mockScripts << new File(config.script.dir, script).absolutePath
    }

    void exec(String command) {
        currentProc = new ShellProc(
            dir:tempFolder.root,
            env:env,
            mockScripts:mockScripts,
            command:command,
            redirectErrorStream:redirectErrorStream)
        currentProc.exec()
    }

    void run(String scriptName, String... args) {
        exec([new File(config.script.dir, scriptName).absolutePath, *args].join(' '))
    }

    void resources(String path){
        def resourcesPath = getClass().getResource("/${path}")
        if(!resourcesPath) throw new FileNotFoundException("resources directory [${path}] not found.")

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
        this.redirectErrorStream = redirectErrorStream
    }
}
