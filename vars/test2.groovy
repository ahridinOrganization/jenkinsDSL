#!groovy

@NonCPS static def myJobFactory(def dslFactory, def jobName) {
        dslFactory.freeStyleJob(jobName) 
    }
    
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
   
    def myJob = myJobFactory(this, "${config.Name}")
    
        myJob.with(
                {    description('foo') }
    )
    
 }
return this;
