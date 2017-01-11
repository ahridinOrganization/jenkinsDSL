@com.cloudbees.groovy.cps.NonCPS
def param = { name , val -> new hudson.model.StringParameterValue(name, val) }

@com.cloudbees.groovy.cps.NonCPS
def paramBool = { name , val -> new hudson.model.BooleanParameterValue(name, val) }


def call(body) {
    def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    //uild job: "CA/VGS3/check_for_repodata", parameters: [param('version', HE_version), param('repository_location', product_artifactory + "vgs3-product/"), paramBool("wait_for_publish",false), paramBool("wait_for_repodata",false)]
    //Expected named arguments but got [deviceman, org.jenkinsci.plugins.workflow.cps.CpsClosure2@23dd9007]
   // build job: 'My Test Job', parameters: [new hudson.model.StringParameterValue('version', 'master')]
    //parameters: [param('version', HE_version), param('repository_location', product_artifactory + "vgs3-product/"), paramBool("wait_for_publish",false), paramBool("wait_for_repodata",false)]
    node {
        freeStyleJob('${config.componentName}') {
            description('My first job')
            disabled()
            logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
            jdk('${JDK_VERISON}')
            concurrentBuild()
            quietPeriod(5)
            // ====================== SCM =============================
            scm {
                svn {
                    checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
                        location(config.repoUrl){
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
                buildName('#${BUILD_NUMBER}.${ENV,var="BRANCH"}.${BUILD_USER}')
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
                timeout {absolute('${JOB_TIMEOUT}')}
            } //end wrappers
            // ====================== PARAMETERS =============================
            parameters {
                listTagsParam('REPO_URL', 'https://wwwin-svn-jrsm.cisco.com/nds/ch_repo/trunk/vgs3') {
                    //tagFilterRegex(/^mytagsfilterregex/)
                    credentialsId('29bae92d-6b9c-4f76-a54e-5b72f851a397')
                    sortZtoA(true)
                    }
                listTagsParam('TAG_URL', config.tagUrl) {
                    credentialsId('29bae92d-6b9c-4f76-a54e-5b72f851a397')
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
                        script('["jdk6_32bit", "jdk7_32bit","jdk1.7.0_05 64bit","jdk1.8.0_05 64bit"]')
                        fallbackScript('["jdk6_32bit", "jdk7_32bit","jdk1.7.0_05 64bit","jdk1.8.0_05 64bit"]')
                        }   
                    }
                textParam('ROOT_POM', 'pom.xml')
                booleanParam('RUN_TESTS', true, 'uncheck to disable tests')
                booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
                labelParam('SLAVE_LABEL') { defaultValue('linux') }
            } //end parameters
             // ====================== PROPERTIES =============================
            properties {
                rebuild {autoRebuild(false)  }
                properties {githubProjectUrl('https://github.com/jenkinsci/job-dsl-plugin')   }
                zenTimestamp('yyyy-MM-dd-HH-mmm')
            }
            // ====================== PUBLISHERS =============================
            publishers {
                archiveArtifacts('build/test-output/**/*.html')
                archiveJunit('**/target/surefire-reports/*.xml')
                buildDescription('', '${BUILD_ID}.${BUILD_TIMESTAMP}.${NODE_NAME}.${BUILD_USER_ID}')
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
                    goals('clean verify') //clean install pmd:pmd findbugs:findbugs clover2:instrument clover2:clover
                    mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
                    localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                    properties(skipTests: false)
                    mavenInstallation('Maven 3.0.4')
                    rootPOM(rootPOM)
                    //providedSettings('central-mirror')
                } 
            } //end steps 
        } //end freeStyleJob
}
}
