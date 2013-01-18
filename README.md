## What?
[Spock](https://github.com/spockframework/spock) extension for shell script.

[![Build Status](https://buildhive.cloudbees.com/job/kiy0taka/job/spock-shell/badge/icon)](https://buildhive.cloudbees.com/job/kiy0taka/job/spock-shell/)

## License

Apache License, Version 2.0

## Gradle settings

    apply plugin: 'groovy'

    repositories {
        mavenCentral()
        maven { url 'http://repo.kiy0taka.org/' }
    }

    dependencies {
        groovy 'org.codehaus.groovy:groovy-all:2.0.6'
        testCompile 'org.kiy0taka.spock:spock-shell:0.4'
    }

## Configuration
src/test/groovy/ShellSpec.groovy

key|required|default|description
---|--------|-------|-----------
script.dir|yes| - |path to shell script directory.
report.dir|no|build/reports/spock-shell|path to report directory.

See https://github.com/kiy0taka/spock-shell/blob/master/src/test/groovy/ShellSpecConfig.groovy

## Sample

See [ShellSpecSpec](https://github.com/kiy0taka/spock-shell/blob/master/src/test/groovy/org/kiy0taka/spock/shell/ShellSpecSpec.groovy)

## Feature

### Run shell scrpit

    run 'myscript.sh', 'arg0', 'arg1'

### Execute commands

    exec 'echo "Hello!"'

### Export environment variable

    export 'VAR', 'VALUE'

### Variables

name|type|description
----|----|-----------
status|int|Exit status
stdout|String|Standard Output
stderr|String|Standard Error
lines|List&lt;String&gt;|Standard Output lines
workspace|File|Shell working directory

### Mock (Shell)

* mock.sh

        function curl {
            echo "called curl $*"
        }

* run.sh

        curl http://www.google.com

* RunSpec.groovy

        setup:
        mockScript 'mock.sh'

        when:
        run 'run.sh'

        then:
        lines[0] == 'called curl http://www.google.com'

### Mock (Groovy)

    given:
    mockFunction('curl') { args ->
        new File(workspace, 'index.html').text = 'Hello World'
    }

    when:
    exec 'curl -O index.html http://www.example.org'

    then:
    def downloaded = new File(workspace, 'index.html')
    downloaded.exists()
    downloaded.text == 'Hello World'

### Test resources

Copy src/resources/test direcotry to workspace.

    resources 'test'

## GENT Template
https://github.com/kiy0taka/spock-shell.gent
