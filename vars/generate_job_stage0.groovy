def call(body) {
    def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
node {
    jobDsl scriptText:"""
    freeStyleJob("${config.jobName}") {
        description("auto generated ${config.jobName} stage-0 job")
        //disabled()
        logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
        jdk("${config.jdkVersion}")
        concurrentBuild()
        quietPeriod(5)
        label("${config.slaveLabel}")
        // ====================== SCM =============================
        scm {
            svn {
                checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
                location("${config.repoUrl}"){
                    credentials('29bae92d-6b9c-4f76-a54e-5b72f851a397')
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
                credentialsId('29bae92d-6b9c-4f76-a54e-5b72f851a397')
                sortZtoA(true)
                }
                listTagsParam('TAG_URL',"${config.tagUrl}") {
                credentialsId('29bae92d-6b9c-4f76-a54e-5b72f851a397')
                //tagFilterRegex(/^mytagsfilterregex/)
                //defaultValue()
                sortNewestFirst()
                }*/
            /*credentialsParam('CREDENTIALS') {
                type('com.cloudbees.plugins.credentials.common.StandardCredentials')
                required()
                defaultValue('29bae92d-6b9c-4f76-a54e-5b72f851a397')
            }*/    
            /*activeChoiceParam('CHECKOUT_STRATEGY') {
                filterable()
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script('["UPDATE","CHECKOUT","UPDATE_WITH_CLEAN","UPDATE_WITH_REVERT"]')
                    fallbackScript('["UPDATE","CHECKOUT","UPDATE_WITH_CLEAN","UPDATE_WITH_REVERT"]')
                    }   
                }
            */    
            /*activeChoiceParam('JDK_VERISON') {
                filterable()
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script('["jdk6_32bit", "jdk7_32bit","jdk8_32bit","jdk8_64bit"]')
                    fallbackScript('["jdk6_32bit", "jdk7_32bit","jdk8_32bit","jdk8_64bit"]')
                    }   
                }*/
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
            //consoleParsing {projectRules('xxx.parser')}
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
            maven {
                goals("-X -e ${config.mavenGoals}") //clean install pmd:pmd findbugs:findbugs clover2:instrument clover2:clover
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
}
}