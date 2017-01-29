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
          params=params+"stringParam('${item.key.toString()}','${item.value.toString()}')\n"           
    }       
    println params
    
    node () {
        def exist = fileExists "job_dsl.groovy"
    echo exist.toString()
    exist = fileExists config.SCRIPT
    echo  exist.toString()
        
        step([
            $class: 'ExecuteDslScripts',
            scriptLocation: [targets: [**/config.SCRIPT].join('\n')],
            removedJobAction: 'DISABLE',
            removedViewAction: 'DELETE',
            lookupStrategy: 'JENKINS_ROOT',
            additionalClasspath: 'vars'
        ])
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'JENKINS_ROOT', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
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
                              script(readFileFromWorkspace(${config.SCRIPT}))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}")                     
    } //end node
} //end call
