def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()	
    node () {
        jobDsl scriptText:"""
           folder("${jobFolder}")
           freeStyleJob("${jobFolder}/${config.NAME}") {
	    description("Auto generated ${config.NAME} build job")
	    logRotator(21,-1,-1,-1) //(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep)
	    concurrentBuild()
	    quietPeriod(2)
	    label('\${SLAVE_LABEL}')    
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
		buildName('#\${BUILD_NUMBER}.\${NODE_NAME}')
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
		//choiceParam('JDK_VERISON', ["${config.JDK_VERSION}", "jdk7_64bit","jdk7_32bit","jdk8_64bit","jdk6_32bit"], 'JDK')
		stringParam("JDK_VERSION", "${config.JDK_VERSION}","JDK Version")
		stringParam("MVN_POM", "${config.MVN_POM}","Root POM name")
		stringParam("MVN_GOALS", "${config.MVN_GOALS}","Maven goals to execute")
		stringParam("TAG_URL", "${config.TAG_URL}","Full SVN URL to tags (without tag version)")
		stringParam("ARTIFACTS", "${config.ARTIFACTS_REGEX}","Artifacts (regex)")
		labelParam('SLAVE_LABEL') { defaultValue("${config.SLAVE_LABEL}") }
		booleanParam('CLEANUP', true, 'uncheck to disable workspace cleanup')
	    } //end parameters
	    // ====================== PROPERTIES =============================
		jdk('\${JDK_VERSION}')    
		properties {
		rebuild {autoRebuild(false)  }
		//properties {githubProjectUrl('https://github.com/jenkinsci/job-dsl-plugin')}
		zenTimestamp('yyyy-MM-dd-HH-mmm')
	    }
	    // ====================== PUBLISHERS =============================
	    publishers {
		//groovyPostBuild("manager.addShortText(manager.build.getEnvironment(manager.listener)[\'NEW_POM_VERSION\'])")
		archiveArtifacts {
		    pattern('target/*.*,pom.xml,POM_VERSION.txt')
		    onlyIfSuccessful()
		}
		buildDescription('', '\${POM_VERSION}')
		//analysisCollector { checkstyle() findbugs() pmd() warnings()}
		/*extendedEmail {
		    defaultSubject('Oops')
		    defaultContent('Something broken')
		    contentType('text/html')
		    triggers { failure { sendTo { developers() requester() culprits() }}}
		}*/
	    } //end publishers
	    // ====================== CONFIGURE =============================
	    /*configure { project ->  project/ 'buildWrappers' / 'org.jfrog.hudson.generic.ArtifactoryGenericConfigurator'{
		details {
		    artifactoryName('-1891791470@1452687536055')
		    artifactoryUrl 'http://engci-maven-master.cisco.com/artifactory'
		}
		deployerCredentialsConfig {
		    credentialsId '688d3adb-743f-4e05-90b8-2fa826dc860c'
		    overridingCredentials 'false'
		    ignoreCredentialPluginDisabled 'false'
		}
		useSpecs 'true'
		uploadSpec {spec {'''{"files":[{"pattern": "(.*).(jar|rpm)","regexp":"true"}]}'''}}
		deployBuildInfo 'true'
		includeEnvVars 'false'
	    }}*/
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
				for (i = 0; i <parameters.size(); ++i) 
					if (parameters[i].value != null) println ("\t" + parameters[i].name + "=\t" + parameters[i].value)			
				//parameters.each { if (\${it.value} != null) println "\t\${it.name}=\t\${it.value}" }
				out.println "=" * (50 + job.size())
				''')
		maven {
		    goals('\${MVN_GOALS} -B -X -V')
		    mavenOpts('-XX:MaxPermSize=128m -Xmx768m')
		    //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
		    localRepository(LocalRepositoryLocation.LOCAL_TO_EXECUTOR)            
		    mavenInstallation("${config.MVN_VERSION}")            
		    properties([skipTests: false])
		    injectBuildVariables(true)
		    rootPOM('\${WORKSPACE}/\${MVN_POM}')
		    //providedSettings('central-mirror')
		}
		//shell ('''echo POM_VERSION=$(printf 'VER\t${project.version}' | mvn help:evaluate | grep '^VER' | cut -f2) > POM_VERSION.txt''')
                //shell ('''echo POM_VERSION="\$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\\[') > POM_VERSION.txt''')
		//envInjectBuilder {propertiesFilePath('POM_VERSION.txt')}
		systemGroovyCommand('''
				import hudson.model.*
				//import hudson.util.*
				hudson = hudson.model.Hudson.instance
				def version = build.getEnvVars()['POM_VERSION'] 
				if (version)
					Thread.currentThread().executable.addAction(new ParametersAction([new StringParameterValue("NEW_POM_VERSION", (version.tokenize('-').first()) + "-" + (++version.tokenize('-').last().toInteger()))]))
				''')
		//maven {
		    //goals('build-helper:parse-version versions:set -DnewVersion=\$NEW_POM_VERSION scm:checkin -Dmessage="build version from jenkins job" -DpushChanges -B -X -V')
		  //  mavenInstallation("${config.MVN_VERSION}")
		//}
	    } //end steps
	} //end freeStyleJob     
	"""
        job = build job: "${jobFolder}/${config.NAME}"	  
    }   
	return job 
}
