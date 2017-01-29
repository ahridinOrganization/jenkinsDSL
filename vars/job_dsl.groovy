def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def job    
    def params=""    
    for (item in config) {           
          params=params+"stringParam('${item.key.toString()}','${item.value.toString()}')\n"           
    }       
    println params
    node () {
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
            folder("${config.JOB}")
            pipelineJob("${config.JOB}") {
                definition {
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}}
                          parameters {
                             ${params}
                            booleanParam('myBool', false)                                                          
                            //choiceParam('choice', ['a', 'b', 'c'], 'FIXME')
                            //stringParam('myParameterName', ${test})                             
                          }                        
                          cps {
                              script(readFileFromWorkspace(${config.SCRIPT}))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}")                     
    } //end node
} //end call
