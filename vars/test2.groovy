#!groovy
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
   
    dslFactory.job("ANNA") 
            
            
           //def myJob = myJobFactory(this, "${config.Name}")
    //        myJob.with(
    //            {    description('foo') }
    //)
        def mJob=freeStyleJobBuild("${config.Name}")
        mJob.with ( {description('foo') } )
    
 }
return this;
