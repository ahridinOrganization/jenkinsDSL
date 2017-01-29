def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def jobFolder="STAGE-0"
    def job
    def params
    for (item in config ) {
        println "${item.key}" + " " + "${item.value}"
        params=params+"""stringParam('${item.key}',${item.value}) """           
    }    
    node () {
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
            folder("${jobFolder}")
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}}
                          parameters {
                             ${params}
                             //choiceParam('choice', ['a', 'b', 'c'], 'FIXME')
                             //stringParam('myParameterName', ${test})
                             //predefinedProps(${config})
                             //predefinedProps([key2: 'value2', key3: 'value3'])
                          }                        
                          cps {
                              script(readFileFromWorkspace("${config.SCRIPT}"))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}",
                     MAVEN_GOALS: "${config.MAVEN_GOALS}",
                     MAVEN_VERISON: "${config.MAVEN_VERISON}",
                     MAVEN_POM: "${config.MAVEN_POM}",
                     MAVEN_SETTINGS: "${config.MAVEN_SETTINGS}",
                     JAVA_VERSION: "${config.JAVA_VERSION}",
                     TEST: "${config.TEST}",
                     CLEANUP: "${config.CLEANUP}")
    } //end node
} //end call
