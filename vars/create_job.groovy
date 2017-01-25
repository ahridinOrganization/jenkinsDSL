def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    printList(config)
    Map<String, String> predefinedProps = config    
    body()	        
    node () {
        echo config.MAVEN_GOALS 
        echo predefinedProps.MAVEN_GOALS
        jobDsl scriptText:"""folder("${jobFolder}")"""
        //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}}
                          parameters {predefinedProps([${config})]}  
                          publishers {aggregateDownstreamTestResults()}
                          cps {
                              script(readFileFromWorkspace("${config.SCRIPT}"))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """        
        //job = build job: "${jobFolder}/${config.NAME}"	
       /* def jobRebase = freeStyleJob("Test")
        jobRebase.with {
            publishers {
              downstreamParameterized {
                trigger("JobB", 'SUCCESS') {
                  parameters { // since 1.23
                    predefinedProp("BRANCH","\${GIT_BRANCH}")
                  }
                }
              }
            }
        }*/
        
    } //end node
} //end call

 @NonCPS
// @NonCPS
def printList(params) {
    
     List<String> props = params.collect { "${it.key}=${it.value}" }
    echo props.toString()
}
