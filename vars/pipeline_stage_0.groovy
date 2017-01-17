def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {       
        def workspace = pwd() 
        echo "Hello from groovy" 
        load "${workspace}@libs/github.com/ahridinOrganization/jenkinsDSL/vars/pipelineScript.groovy"           
        pipelineScript(config)
    }

}
