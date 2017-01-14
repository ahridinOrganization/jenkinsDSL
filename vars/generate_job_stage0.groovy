def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node ("${config.slaveLabel}") {
        jobDsl scriptText:"""
            folder("${jobFolder}")
            freeStyleJob("${jobFolder}/${config.jobName}") {
                description("Auto generated ${config.jobName} stage-0 job")
                logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
                jdk("${JDK_VERISON}")
                concurrentBuild()
                quietPeriod(5) 
                label("${config.slaveLabel}")
                // ====================== SCM =============================
                scm {
                    svn {
                        checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
                        location("${config.repoUrl}"){
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
                    buildName('#${BUILD_NUMBER}')
                    maskPasswords()
                    /*credentialsBinding { 
                        file('KEYSTORE', 'keystore.jks')
                        usernamePassword('PASSWORD', 'keystore password')
                    }*/
                    preBuildCleanup {
                        includePattern('**/*')
                        deleteDirectories()
                        cleanupParameter('CLEANUP')
                    }
                    timeout {absolute(${config.timeout})}
                } //end wrappers
                // ====================== PARAMETERS =============================
                parameters {

                    /*listTagsParam('REPO_URL', "${config.repoUrl}") {
                        //tagFilterRegex(/^mytagsfilterregex/)
                        credentialsId('c2b9fdc3-7562-4bc4-b4f6-3de05444999e')
                        sortZtoA(true)
                        }*/
                        listTagsParam('TAG_URL',"${config.tagUrl}") {
                        credentialsId('c2b9fdc3-7562-4bc4-b4f6-3de05444999e')
                        //tagFilterRegex(/^mytagsfilterregex/)
                        //defaultValue()
                        sortNewestFirst()
                        }
                    credentialsParam('CREDENTIALS') {
                        type('com.cloudbees.plugins.credentials.common.StandardCredentials')
                        required()
                        defaultValue('29bae92d-6b9c-4f76-a54e-5b72f851a397')
                    }    
                    /*activeChoiceParam('CHECKOUT_STRATEGY') {
                        filterable()
                        choiceType('SINGLE_SELECT')
                        groovyScript {
                            script('["UPDATE","CHECKOUT","UPDATE_WITH_CLEAN","UPDATE_WITH_REVERT"]')
                            fallbackScript('["UPDATE","CHECKOUT","UPDATE_WITH_CLEAN","UPDATE_WITH_REVERT"]')
                            }   
                        }
                    */    
                    activeChoiceParam('JDK_VERISON') {
                        filterable()
                        choiceType('SINGLE_SELECT')
                        groovyScript {
                        script('return ["jdk7_64bit","jdk7_32bit","jdk8_64bit","jdk6_32bit","${config.jdkVersion}:selected"]')
                            fallbackScript('return ["jdk6_32bit", "jdk7_32bit","jdk7_64bit","jdk8_64bit"]')
                            }   
                    }
                    //textParam('ROOT_POM', 'pom.xml')
                    //booleanParam('RUN_TESTS', true, 'uncheck to disable tests')
                    booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
                   //labelParam('SLAVE_LABEL') { defaultValue('linux') }
                } //end parameters
                 // ====================== PROPERTIES =============================
                properties {
                    rebuild {autoRebuild(false)  }
                    //properties {githubProjectUrl('https://github.com/jenkinsci/job-dsl-plugin')   }
                    zenTimestamp('yyyy-MM-dd-HH-mmm')
                }
                // ====================== PUBLISHERS =============================
                publishers {
                    archiveArtifacts('build/test-output/**/*.html')
                    archiveJunit('**/target/surefire-reports/*.xml')
                    buildDescription('', '${BUILD_ID}.${NODE_NAME}')
                    //analysisCollector { checkstyle() findbugs() pmd() warnings()}                
                    extendedEmail {
                        disabled(true)
                        defaultSubject('Oops')
                        defaultContent('Something broken')
                        contentType('text/html')
                        //triggers {
                            //failure { sendTo {   developers() requester()  culprits()}}
                        //}
                    }
                } //end publishers
                steps {
                //systemGroovyCommand(readFileFromWorkspace('disconnect-slave.groovy')) {binding('computerName', 'ubuntu-04') }
                    systemGroovyCommand(println("JDK_VERISON = ${env.JDK_VERISON}" ))
                                                           
                    maven {
                        goals("-B -V -X -e ${config.mavenGoals}") 
                        mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
                        localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                        //properties(skipTests: true)
                        mavenInstallation("${config.mavenVersion}")
                        rootPOM("${config.mavenPom}")
                        //providedSettings('central-mirror')
                    } 
                } //end steps 
            } //end freeStyleJob        
        """
        build job: "${jobFolder}/${config.jobName}"
    }      
}
