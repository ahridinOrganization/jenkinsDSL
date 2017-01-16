#!groovy
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    println "${config.Name}"
    node () {  
        listView(${config.Name}, config)  
        freeStyleJob("${jobFolder}/${config.Name}")
        git url: 'https://github.com/jfrogdev/project-examples.git'
        buildFlowJob(String name, Closure closure = null)
        
        }
    
 }
return this;
