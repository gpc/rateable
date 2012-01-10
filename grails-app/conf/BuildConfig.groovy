grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log "warn" 
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "http://localhost:8081/artifactory/plugins-releases-local"
    }
    plugins {
        build ":release:1.0.1", {
            export = false
        }

        runtime ":yui:2.8.2"
    }
    dependencies {
    }
}
