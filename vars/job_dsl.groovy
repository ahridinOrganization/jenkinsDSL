def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def jobFolder="STAGE-0"
    def job
    def params=""
    for (item in config) {
        if (item != null)            
          params=params+"stringParam('${item.key.toString()}',${item.value.toString()})\n"           
    }       
    println params
    node () {
       def workspace = pwd()
       echo workspace
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
            folder("${jobFolder}")
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          cpsScm { scm {git('https://github.com/ahridinOrganization/jenkinsDSL')}}
                          parameters {
                             ${params}
                            booleanParam('myBool', false)                                                          
                            //choiceParam('choice', ['a', 'b', 'c'], 'FIXME')
                            //stringParam('myParameterName', ${test})                             
                          }                        
                          cps {
                              script(readFileFromWorkspace('\${SCRIPT}'))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}")                     
    } //end node
} //end call
