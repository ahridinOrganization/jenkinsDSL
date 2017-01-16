/*def basicConfiguration() {
    return {
        description('foo')
        scm {
            
        }
    }
}
*/
/*def myJob = freeStyleJob('example') {
    publishers {
        // more config
    }
}*/
//myJob.with basicConfiguration()
@NonCPS
def myJobFactory(def dslFactory, def jobName) {
    dslFactory.freeStyleJob(jobName) {
        description('foo')
    }
}

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
       
    node {
        def myJob = myJobFactory(delegate,"${config.Name}")
        //def stage0_job = freeStyleJob(config.componentName) { }
          
        
        
        
}
}
