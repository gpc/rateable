// Resource declarations for Resources plugin
modules = {
    'rateable' {
        dependsOn "yui-core"

        resource url: [plugin: "rateable", dir: "css", file: "ratings.css"]
        resource url: [plugin: "rateable", dir: "js", file: "ratings.js"], disposition: "head"
    }
}
