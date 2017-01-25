def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()	
    node () {
        echo config.NAME
        echo config.MAIL
        jobDsl scriptText:"""folder("${jobFolder}")"""
        //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
            pipelineJob(${jobFolder}/${config.NAME})
                {
                    definition {
                        cps {
                           scriptPath('/vars/stage_0_pipeline.groovy')
                            sandbox()
                        } //end cps
                    } //end definition
               } //end pipelinejob
        """        
        //job = build job: "${jobFolder}/${config.NAME}"	
    } //end node
} //end call
