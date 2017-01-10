  
def call(body) {
    try {
        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        println(nodeNames().join(",").toString()) //getnodes

        timestamps {
            //timeout(time: 180, unit: 'MINUTES')
        node () {
            println("="*80) 
            println ("Checkout") 
            stage("Checkout") {
                //checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: config.repoUrl]], workspaceUpdater: [$class: config.checkoutMode]])        
            }
            stage('Build') {
                //def mvnHome = tool 'M2'
                (config.goals)split(",").each { goal -> println ("===>$goal") 
                                              maven { mavenInstallation('maven-3x')
                                              goals(goal) } 
                }
                //maven("test -Dproject.name=${project}/${branchName}")
                //sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore verify"
            }
            //stage('Promote') {
                 //println("="*80) 
                //println ("Promote") 
               // step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
                //step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml']) 
            //}         
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

