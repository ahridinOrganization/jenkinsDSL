@Library('github.com/ahridinOrganization/jenkinsDSL') _
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
    freeStyleJob("${config.jobName}_stage0.groovy") {
           description("Auto generated ${config.jobName} stage-0 job")
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
                    booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
                 } //end parameters
                 // ====================== PROPERTIES =============================
                properties {
                    rebuild {autoRebuild(false)  }
                    zenTimestamp('yyyy-MM-dd-HH-mmm')
                }
                // ====================== PUBLISHERS =============================
                steps {
                    maven {
                        goals("-X -e ${config.mavenGoals}") 
                        mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
                        localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                        //properties(skipTests: true)
                        mavenInstallation("${config.mavenVersion}")
                        rootPOM("${config.mavenPom}")
                        //providedSettings('central-mirror')
                    } 
                } //end steps 
            } //end freeStyleJob 
    }
return this;

