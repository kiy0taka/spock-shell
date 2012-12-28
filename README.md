## What?
[Spock](https://github.com/spockframework/spock) extension for shell script.

[![Build Status](https://buildhive.cloudbees.com/job/kiy0taka/job/spock-shell/badge/icon)](https://buildhive.cloudbees.com/job/kiy0taka/job/spock-shell/)

## Gradle settings

    apply plugin: 'groovy'

    repositories {
        mavenCentral()
        maven { url 'http://repo.kiy0taka.org/' }
    }

    dependencies {
        groovy 'org.codehaus.groovy:groovy-all:2.0.6'
        testCompile 'org.kiy0taka.spock:spock-shell:0.1'
    }

## Configuration
Add ShellSpec.groovy to src/test/groovy.

    script.dir = 'shell' // path to shell script directory.

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

### Mock

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

## GENT Template
https://github.com/kiy0taka/spock-shell.gent
