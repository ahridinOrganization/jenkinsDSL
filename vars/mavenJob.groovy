ef call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
	node {
		mavenJob("${config.NAME}") {
		  using('base-maven-build')
		  // ====================== WRAPPERS =============================
		  wrappers {
		    colorizeOutput()
				timestamps()
				buildUserVars()
				buildName('#\${BUILD_NUMBER}.\${NODE_NAME}')
				maskPasswords()
				preBuildCleanup {
				    includePattern('**/*')
				    deleteDirectories()
				    cleanupParameter('CLEANUP')
				}
				timeout {
			    absolute(${config.TIMEOUT})
			    abortBuild()
		    }
				credentialsBinding{usernamePassword("username", "password", "${config.credentialsId}")}    
		  }
		    // ====================== PARAMETERS =============================
			  parameters {
		      stringParam("JDK_VERSION", "${config.JDK_VERSION}","JDK Version")
		      stringParam("MVN_POM", "${config.MVN_POM}","Root POM name")
		      stringParam("MVN_GOALS", "${config.MVN_GOALS}","Maven goals to execute")
		      stringParam("TAG_URL", "${config.TAG_URL}","Full SVN URL to tags (without tag version)")
		      stringParam("ARTIFACTS", "${config.ARTIFACTS_REGEX}","Artifacts (regex)")
		      labelParam('SLAVE_LABEL') { defaultValue("${config.SLAVE_LABEL}") }
		      booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
		    } //end parameters
		    scm {
		    git {
		      remote {
			github('fabric8io/quickstarts','git')
			}
		      clean(true)
		      cloneTimeout(60)
		    }
		  }
		  preBuildSteps {
		    maven {
		      mavenInstallation('3.3.1')
		      runHeadless(true)
		      goals('build-helper:parse-version')
		      goals('versions:set')
		      goals('-DnewVersion=${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-${BUILD_NUMBER}')
		    }
		  }
		  goals('clean deploy')
		  goals('-DaltDeploymentRepository=local-nexus::default::${STAGING_REPO}')
		  goals('-Ddocker.registryPrefix=registry.cd.fabric8.io/')
		  goals('-settings /var/jenkins_home/.m2/settings.xml')
		  publishers {
		    downstreamParameterized {
		      trigger('fabric8-deploy', 'UNSTABLE_OR_WORSE', true) {
			currentBuild()
		      }
		    }
		  }
		}
	}
}
