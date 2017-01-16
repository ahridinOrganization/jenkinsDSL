#!groovy
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
   
    folder(String name, Closure closure = null) // since 1.30
    freeStyleJob('project-a/compile')
    def myJob = freeStyleJob('SimpleJob')
    myJob.with {
      description 'A Simple Job'
    }
            
            
           //def myJob = myJobFactory(this, "${config.Name}")
    //        myJob.with(
    //            {    description('foo') }
    //)
       
 }
return this;
