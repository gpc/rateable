grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    inherits "global"
    log "warn" 
    repositories {
        grailsPlugins()
        grailsCentral()
        mavenRepo "http://repo.grails.org/grails/core"
        mavenCentral()
    }
    plugins {
        build ':release:3.0.1', ':rest-client-builder:1.0.3', {
            export = false
        }

        runtime ":yui:2.8.2", {
            exclude "svn"
        }
    }
    dependencies {
    }
}
