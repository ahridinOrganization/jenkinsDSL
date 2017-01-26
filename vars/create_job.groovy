def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def test= "Anna " + config.MAVEN_GOALS
    def jobFolder="STAGE-0"
    def job
    node () {
        echo test
        echo config.MAVEN_GOALS 
        
       //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
            folder("${jobFolder}")
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}}
                          parameters {
                             choiceParam('choice', ['a', 'b', 'c'], 'FIXME')
                             stringParam('myParameterName', test)
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
    } //end node
} //end call

 @NonCPS
// @NonCPS
def printList(params) {
    
     List<String> props = params.collect { "${it.key}=${it.value}" }
    echo props.toString()
}
