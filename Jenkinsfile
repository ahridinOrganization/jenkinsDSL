import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL
 

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
