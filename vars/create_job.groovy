def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job
    
    Map<String, String> predefinedProps = [:]
    predefinedProps.putAll(config)
    
    for (i = 0; i < predefinedProps.size(); ++i) {
        echo predefinedProps[i]
    }
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()	        
    node () {
        echo config.MAVEN_GOALS   
        
        
        //environmentVariables {propertiesFile('build.properties')}
        jobDsl scriptText:"""folder("${jobFolder}")"""
        //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}}
                          parameters {
                            predefinedProps(${predefinedProps})                          
                            predefinedProps(['ORI_EXPORT_DIR': 'ORI_EXPORT_DIR'])
                          }  
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
