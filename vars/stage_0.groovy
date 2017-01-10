import hudson.model.*
    
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    println (nodeNames().join(",").toString())
    
//Debug
    //binding.variables.each {println "${it.key} = ${it.value}"}
    
    node (){
         println "="*80
    Executor.currentExecutor().currentExecutable.getAction(ParametersAction).parameters.each { ParameterValue v -> println v}
    
        stage("Checkout") {
            checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: config.repoUrl]], workspaceUpdater: [$class: config.checkoutMode]])        
         }
        stage('Main') {
           timeout(time: 180, unit: 'MINUTES') 
           docker.image(config.environment).inside { sh config.mainScript}
        }
        stage('Post') { 
            sh config.postScript
        }
   }
}
//build job: 'test_jobs', parameters: [[$class: 'StringParameterValue', name: 'param1', value:'test_param'], [$class: 'StringParameterValue', name:'dummy', value: "${index}"]]

// Collects a list of Node names from the current Jenkins instance
@NonCPS
def nodeNames() {
  return jenkins.model.Jenkins.instance.nodes.collect { node -> (node.name=="linux")? node:null}
}
/*
publishers {
        extendedEmail {
            recipientList('me@halfempty.org')
            defaultSubject('Oops')
            defaultContent('Something broken')
            contentType('text/html')
            triggers {
                beforeBuild()
                stillUnstable {
                    subject('Subject')
                    content('Body')
                    sendTo {
                        developers()
                        requester()
                        culprits()
                    }
                }
            }
        }
    }*/

