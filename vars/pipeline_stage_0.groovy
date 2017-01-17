def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {       
        mavenJob()
        //freeStyleJob("${jobFolder}/${config.NAME}") {
        def workspace = pwd() 
        echo "Hello from pipeline_stage_0.groovy" 
        load "${workspace}@libs/github.com/ahridinOrganization/jenkinsDSL/vars/pipelineScript.groovy"           
        pipelineScript(config)
    }

}
