
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def runjob(Object myconfig) {
def myJob = freeStyleJob("${config.jobName}")
myJob.with {
    description 'A Simple Job'
    tools {
                maven "Maven 3.0.4"
                jdk "Oracle JDK 8u40"
            }
    }
}



def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {       
        //freeStyleJob("${jobFolder}/${config.NAME}") {
        def workspace = pwd() 
        echo "Hello from pipeline_stage_0.groovy" 
        //load "${workspace}@libs/github.com/ahridinOrganization/jenkinsDSL/vars/pipeline0.groovy"           
        //pipelineScript(config)
        runjob(config)
        //       workflowJob("testWworkflowJob")
  //      buildFlowJob("${config.jobName}") {            
            
        }
    }

