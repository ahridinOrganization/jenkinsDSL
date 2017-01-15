def call(body) {
    def config = [:]
    def jobFolder="DOCKER"
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node () {
        jobDsl scriptText:"""
            folder("${jobFolder}")
            freeStyleJob("${jobFolder}/${config.NAME}") {
                description("Auto generated ${config.NAME} docker build job")
                logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
                concurrentBuild()
                quietPeriod(5) 
                label("${config.SLAVE_LABEL}")
                // ====================== SCM =============================
		scm {
		     git {
            		remote { 
				github('git@github3.cisco.com:vgehe-devops/docker.git')
                		credentials('c2b9fdc3-7562-4bc4-b4f6-3de05444999e')
            			}
                     }					    	
                }
                // ====================== WRAPPERS =============================
                wrappers {
                    colorizeOutput()
                    timestamps()
                    buildUserVars()
                    //buildName('#${BUILD_NUMBER}')
                    maskPasswords()
                    preBuildCleanup {
                        includePattern('**/*')
                        deleteDirectories()
                        cleanupParameter('CLEANUP')
                    }
                    timeout {absolute(${config.TIMEOUT})}
                    credentialsBinding{usernamePassword('username', 'password', 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e')}		    
                } //end wrappers
                // ====================== PARAMETERS =============================
                parameters {
                    stringParam("COMPONENT", "${config.COMPONENT}","")		    
		    stringParam("VERSION", "${config.VERSION}","")		    
		    stringParam("PROJECT", "${config.PROJECT}","")                    
                    labelParam("SLAVE_LABEL") { defaultValue("${config.SLAVE_LABEL}") }
                    booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
                } //end parameters
                 // ====================== PROPERTIES =============================
                properties {
                    rebuild {autoRebuild(false)  }
                    //properties {githubProjectUrl('https://github.com/jenkinsci/job-dsl-plugin')}
                    zenTimestamp('yyyy-MM-dd-HH-mmm')
                }
                // ====================== PUBLISHERS =============================
		publishers {
                    archiveArtifacts('build/test-output/**/*.html')                
                    buildDescription('', '${BUILD_ID}.${config.COMPONENT_NAME}.${config.COMPONENT_VERSION}.${config.PROJECT}')
                    /*extendedEmail {
                        disabled(true)
                        defaultSubject('Oops')
                        defaultContent('Something broken')
                        contentType('text/html')
                        //triggers {
                            //failure { sendTo {   developers() requester()  culprits()}}
                        //}
                    }*/
                } //end publishers		
		// ====================== STEPS =============================
                steps {
                    systemGroovyCommand ('''
			import hudson.model.*
			import hudson.util.*
			import hudson.node_monitors.*
			hudson = hudson.model.Hudson.instance			
			def parameters = Thread.currentThread().executable?.actions.find{ it instanceof ParametersAction }?.parameters
			def job = Thread.currentThread().executable.getEnvVars()['JOB_NAME'] 
			out.println "=" * 25 + job + "=" * 25
   			parameters.each { println "\t\${it.name}=\t\${it.value}" }
			out.println "=" * (50 + job.size())
			''')
		shell ('''
			if [[ \${VERSION} == *"SNAPSHOT"* ]]; then
				echo "SNAPSHOT version - skipping Docker Image build!"
			else
				cd \${COMPONENT}
    				if [[ -f "build-params.properties" ]]; then
					sed -i "s/project::name=general/project::name=\${PROJECT}/g" build-params.properties
    				else
    					echo "using project_name=\${PROJECT}"
    				fi
    				./build.sh push
			fi

			'''
                                 
                } //end steps 
            } //end freeStyleJob        
        """
        build job: "${jobFolder}/${config.NAME}"
	    
    }      
}
