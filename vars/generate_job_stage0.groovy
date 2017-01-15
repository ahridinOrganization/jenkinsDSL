def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node () {
        def myjob=freeStyleJob("${config.NAME}")        
        jobDsl scriptText:"""
            folder("${jobFolder}")
            freeStyleJob("${jobFolder}/${config.NAME}") {
                description("Auto generated ${config.NAME} stage-0 job")
                logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
                concurrentBuild()
                quietPeriod(5) 
                label("${config.SLAVE_LABEL}")
                // ====================== SCM =============================
                scm {
                    svn {
                        checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
                        location("${config.REPO_URL}"){
                            credentials('c2b9fdc3-7562-4bc4-b4f6-3de05444999e')
                            ignoreExternals(true)
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
		    configure { project ->
        		project / 'buildWrappers' / 'org.jfrog.hudson.generic.ArtifactoryGenericConfigurator' {
				details {
					artifactoryName '-1891791470@1452687536055'
					artifactoryUrl 'http://engci-maven-master.cisco.com/artifactory'					
				}
				deployerCredentialsConfig {
					credentialsId '688d3adb-743f-4e05-90b8-2fa826dc860c'
        				overridingCredentials 'false'
				}	
				useSpecs 'true'
				uploadSpec {
					spec { }
				}
        	    	}
    		    }
                } //end wrappers
                // ====================== PARAMETERS =============================
                parameters {
                    /*activeChoiceParam('JDK_VERISON') {
                        filterable()
                        choiceType('SINGLE_SELECT')
                        groovyScript {
                        script('return ["${config.JDK_VERSION}","jdk7_64bit","jdk7_32bit","jdk8_64bit","jdk6_32bit"]')
                            fallbackScript('return ["jdk6_32bit", "jdk7_32bit","jdk7_64bit","jdk8_64bit"]')
                            }   
                    } */
                    //choiceParam('JDK_VERISON', ["${config.JDK_VERSION}", "jdk7_64bit","jdk7_32bit","jdk8_64bit","jdk6_32bit"], 'JDK')
                    //booleanParam('RUN_TESTS', true, 'uncheck to disable tests')
		    stringParam("JDK_VERSION", "${config.JDK_VERSION}","JDK Version")
		    stringParam("MVN_POM", "${config.MVN_POM}","Root POM name")
                    stringParam("MVN_GOALS", "${config.MVN_GOALS}","Maven goals to execute")
                    stringParam("TAG_URL", "${config.TAG_URL}","Full SVN URL to tags (without tag version)")                        
                    /*listTagsParam('TAG_URL',"${config.TAG_URL}") {
                        credentialsId('c2b9fdc3-7562-4bc4-b4f6-3de05444999e')
                        //tagFilterRegex(/^mytagsfilterregex/)
                        defaultValue("${config.TAG_URL}")
                        sortNewestFirst()
                    }*/            
                    labelParam('SLAVE_LABEL') { defaultValue("${config.SLAVE_LABEL}") }
                    booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
                } //end parameters
                 // ====================== PROPERTIES =============================
                properties {
                    rebuild {autoRebuild(false)  }
                    //properties {githubProjectUrl('https://github.com/jenkinsci/job-dsl-plugin')}
                    zenTimestamp('yyyy-MM-dd-HH-mmm')
                }
                // ====================== PUBLISHERS =============================
		jdk('\${JDK_VERSION}')  
                publishers {
                    //archiveArtifacts('build/test-output/**/*.html')
                    //archiveJunit('**/target/surefire-reports/*.xml')
                    buildDescription('', '${BUILD_ID}.${NODE_NAME}')
                    //analysisCollector { checkstyle() findbugs() pmd() warnings()}                
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
                    //systemGroovyCommand(readFileFromWorkspace('disconnect-slave.groovy')) {binding('computerName', 'ubuntu-04') }
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
                    maven {
                        goals('\${MVN_GOALS} -B -X -V') 
                        mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
                        //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                        localRepository(LocalRepositoryLocation.LOCAL_TO_EXECUTOR)
                        properties(skipTests: true)                                              
                        mavenInstallation("${config.MVN_VERSION}")
                        injectBuildVariables(true)
                        rootPOM('\${WORKSPACE}/\${MVN_POM}')
                        //providedSettings('central-mirror')
                        } 
		    systemGroovyCommand('''
			import hudson.model.*
			import hudson.util.*
			hudson = hudson.model.Hudson.instance
			try {
        			def version = build.getEnvVars()['POM_VERSION'] 
                                if (version!=null) 
					Thread.currentThread().executable.addAction(new ParametersAction([new StringParameterValue("NEW_POM_VERSION", (version.tokenize('-').first()) + "-" + (++version.tokenize('-').last().toInteger()))]))
			} catch(Error e) { out.println e.toString()} ''') 					    	
		    maven {
			 goals('build-helper:parse-version -B -X -V')
			 goals('versions:set -B -X -V')
			 goals('-DnewVersion=\$NEW_POM_VERSION scm:checkin -Dmessage="build version from jenkins job" -DpushChanges -B -X -V')
			 mavenInstallation("${config.MVN_VERSION}")                       
                        }	
                                 
                } //end steps 
            } //end freeStyleJob        
        """
        build job: "${jobFolder}/${config.NAME}"
	    
    }      
}
