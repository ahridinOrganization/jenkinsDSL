import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL
 
 
try {
	node {
		stage ('\u2776Stage1: PrepareEnv'){
			echo "\u2600BUILD_URL\t=${env.BUILD_URL}\nBUILD_CAUSE\t=${env.BUILD_CAUSE}\nBUILD_ID\t=${env.BUILD_ID}"
			def workspace = pwd()
			echo "\u2600 workspace=${workspace}"		
			}
		stage ('\u2777Stage2: Build') { /* .. snip .. */ }	
		stage ('\u2778Stage3: Test') { 
			parallel (
			windows: { node {echo "NODE_NAME=${env.NODE_NAME}"; sh "echo building on windows now" }},
			linux: { node {echo "NODE_NAME=${env.NODE_NAME}"	; sh "echo building on mac now"}}
			)	
			}
	}// node
} // try end
catch (exc) {
	/*
	err = caughtError
	currentBuild.result = "FAILURE"
	String recipient = 'ahridin@cisco.com'
	mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
	body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
	to: recipient,
	replyTo: recipient,
	from: 'noreply@ci.jenkins'
	*/
} finally {
	(currentBuild.result != "ABORTED") && node("master") {
    // Send e-mail notifications for failed or unstable builds.
    // currentBuild.result must be non-null for this step to work.
    def email_to ="ahridin@cisco.com"
	step([$class: 'Mailer',
        notifyEveryUnstableBuild: true,
        recipients: "${email_to}",
        sendToIndividuals: true])
}
// Must re-throw exception to propagate error:
if (err) {throw err }
}
