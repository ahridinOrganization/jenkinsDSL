    
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    //getnodes
    //out.println(nodeNames().join(",").toString())
    try {
    node (){
        out.println("="*80)    
        stage("Checkout") {
            //checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: config.repoUrl]], workspaceUpdater: [$class: config.checkoutMode]])        
         }
        stage('Build') {
           timeout(time: 180, unit: 'MINUTES') 
           //docker.image(config.environment).inside { sh config.mainScript}            
            def mvnHome = tool 'M2'
            //sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore verify"
        }
        stage('Promote') { 
             step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
             step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml']) 
        }
   }
   } catch (e) {  // if any exception occurs, mark the build as failed
         currentBuild.result = 'FAILURE'
            throw e
        } finally {
          step([$class: 'WsCleanup', cleanWhenFailure: false])
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

