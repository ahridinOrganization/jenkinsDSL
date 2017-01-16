#!groovy
def myJobFactory(def dslFactory, def jobName) {
        dslFactory.freeStyleJob("${config.Name}") 
    }
@NonCPS
static def makeMeABasicJob(def context) { context.freeStyleJob() }
    
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
   def job1 = makeMeABasicJob(this) //Passing the groovy file class as the resolution context
    job1.with({
    //custom stuff
    scm {
        svn {
            //etc....
                }
        }
     })

    def myJob = myJobFactory(this, "${config.Name}")
    
    //def myJob = freeStyleJob("${config.Name}")
    
    println "${config.Name}"
    node () {  
        listView("${config.Name}", config)  
        freeStyleJob("${jobFolder}/${config.Name}")
        //git url: 'https://github.com/jfrogdev/project-examples.git'
        buildFlowJob(String name, Closure closure = null)
        
        }
    
 }
return this;
