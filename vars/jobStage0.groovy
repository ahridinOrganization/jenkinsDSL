def basicConfiguration() {
    return {
        description('foo')
        scm {
            
        }
    }
}

def myJob = freeStyleJob('example') {
    publishers {
        // more config
    }
}
myJob.with basicConfiguration()

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
       
    node {
        def stage0_job = freeStyleJob(config.componentName) { }
        /*
         
        } //end freeStyleJob
        
        
}
}
