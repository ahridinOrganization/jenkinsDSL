def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def job
    node {
        //def job1 = makeMeABasicJob(this) //Passing the groovy file class as the resolution context
        //job1.with({ steps {  shell('echo Hello') }  })
        BuildFramework.ant(job, 'my-ant-project', 'clean build')
    }
}


