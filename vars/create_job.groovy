def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()	
    node () {
        //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
           folder("${jobFolder}")
          

           pipelineJob(${jobFolder}/${config.NAME}") {
                definition {
                cps {
                    scriptPath('/vars/stage_0_pipeline.groovy')
                    sandbox()
                    }
                }
            }


           }
       }
}






