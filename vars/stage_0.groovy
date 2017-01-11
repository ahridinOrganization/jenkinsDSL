  
def call(body) {
        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()
        timestamps {
        //println(nodeNames().join(",").toString()) //getnodes        
        
        //timeout(time: 180, unit: 'MINUTES')
        node ('windows') {
            try {
              //currentBuild.displayName = "${BUILD_USER_ID}."
              currentBuild.description = "${BUILD_ID}.${NODE_NAME}"
              //multiwrap([[$class: 'TimestamperBuildWrapper'],[$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'myfile', variable: 'FILE']]]]) 
              //wrappers{ credentialsBinding{  usernamePassword('userVar', 'passwordVar', '${cred}')  } }           
              jdk(config.jdkVersion)
              stage("Checkout") {
              //checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: config.repoUrl]], workspaceUpdater: [$class: config.checkoutMode]])
            }
            stage('Build') {
                 goals=config.mavenGoals.split(",")
                 for (int i=0;i<goals.length;++i) {
                 step {  /*maven {
                          mavenInstallation(config.mavenVersion)
                          goals(goals[i]) 
                          runHeadless(true)
                          //rootPOM("pom.xml")
                          localRepository(LocalToWorkspace)
                         }*/
                 withMaven() {
                      goals('clean') 
                  } 
                 }
                }
                //def mvnHome = tool 'M2'
                //maven("test -Dproject.name=${project}/${branchName}")
                //sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore verify"
            }
            stage('Promote') {
                //publishers {
                  //buildDescription('', '${BRANCH}')
                //publishers { mailer('', true, true) findbugs('**/findbugsXml.xml', true) pmd('**/*.pmd') cobertura('**/target/site/cobertura/coverage.xml')} 
                //println("="*80) 
                //println ("Promote") 
               // step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
                //step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml']) 
               // }
              }
            
            } catch (e) {  // if any exception occurs, mark the build as failed
              currentBuild.result = 'FAILURE'
              throw e
            } finally {
              step([$class: 'WsCleanup', cleanWhenFailure: false])
            }
        }
}
}
//build job: 'test_jobs', parameters: [[$class: 'StringParameterValue', name: 'param1', value:'test_param'], [$class: 'StringParameterValue', name:'dummy', value: "${index}"]]

// Collects a list of Node names from the current Jenkins instance
@NonCPS
def getNode(String name, String label) {
  jenkins.model.Jenkins.instance.nodes.collect { node -> 
      if (node.name==name && !node.getComputer().isOffline()) return node
      if (node.getLabelString()=~/(?i)${label}/ && !node.getComputer().isOffline()) return node
      return ""
  }
}
  
  

def multiwrap(wrappers, body) {_multiwrap(wrappers, 0, body)}

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

