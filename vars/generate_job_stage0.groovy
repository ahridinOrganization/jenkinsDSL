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
                jdk('\${JDK_VERISON}')                
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
                } //end wrappers
                // ====================== PARAMETERS =============================
                parameters {
                    activeChoiceParam('JDK_VERISON') {
                        filterable()
                        choiceType('SINGLE_SELECT')
                        groovyScript {
                        script('return ["jdk7_64bit","jdk7_32bit","jdk8_64bit","jdk6_32bit","${config.JDK_VERSION}:selected"]')
                            fallbackScript('return ["jdk6_32bit", "jdk7_32bit","jdk7_64bit","jdk8_64bit"]')
                            }   
                    }                    
                    //booleanParam('RUN_TESTS', true, 'uncheck to disable tests')
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
                    //systemGroovyCommand { println("JDK_VERISON = '\${JDK_VERISON}'") binding('computerName', 'ubuntu-04') }                                                          
                    maven {
                        goals('\${MVN_GOALS} -B -X -V') 
                        mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
                        localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                        //properties(skipTests: true)
                        mavenInstallation("${config.MVN_VERSION}")
                        rootPOM('\${MVN_POM}')
                        //providedSettings('central-mirror')
                    } 
                } //end steps 
            } //end freeStyleJob        
        """
        build job: "${jobFolder}/${config.NAME}"
    }      
}
