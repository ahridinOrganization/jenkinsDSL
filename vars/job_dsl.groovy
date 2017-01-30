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
        echo pwd()
        //additionalClasspath: pwd()/,
        checkout([$class: 'GitSCM', branches: [[name: '*/master']],doGenerateSubmoduleConfigurations: false, extensions: [],submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e', 
                                url: "https://github.com/ahridinOrganization/jenkinsDSL"]]])
        jobDsl ignoreMissingFiles: false,  lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
            folder("${jobFolder}")
            pipelineJob("${jobFolder}/${config.NAME}") {
                definition {
                          parameters {
                             ${params}
                            booleanParam('myBool', false)                                                          
                            //choiceParam('choice', ['a', 'b', 'c'], 'FIXME')
                            //stringParam('myParameterName', ${test})                             
                          }    
                          cpsScm { scm {git('https://github.com/jenkinsci/job-dsl-plugin.git')}
                          cps {
                              //script(readFileFromWorkspace(${config.SCRIPT}))
                              sandbox()
                          } //end cps
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}")                     
    } //end node
} //end call
