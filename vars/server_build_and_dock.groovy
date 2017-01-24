def pom=null
def newVersion=null


properties([
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '-1']],
    [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
    parameters([
        choice(choices: 'CISCO-Artifactory\nSDS-Artifactory', description: '', name: 'ARTIFACTORY'),// choices are a string of newline separated values
        string(name: 'MAVEN_GOALS', defaultValue: 'clean validate test package verify pmd:pmd findbugs:findbugs -B -X -V ', description: ''),
        string(name: 'MAVEN_VERISON', defaultValue: 'Maven 3.0.4', description: ''),
        string(name: 'MAVEN_POM', defaultValue: 'pom.xml', description: ''),
        string(name: 'MAVEN_SETTINGS', defaultValue: 'setting.xml', description: ''),
        string(name: 'JAVA_VERSION', defaultValue: 'jdk7_64bit', description: ''),
        booleanParam(name: 'TEST', defaultValue: false, description: 'Run tests'),
        booleanParam(name: 'CLEANUP', defaultValue: false, description: 'Clean checkout'),
        booleanParam(name: 'DEPLOY', defaultValue: false, description: 'Upload to artifactory/docker registry'),
        string(name: 'MAIL', defaultValue: 'ahridin@cisco.com', description: ''),
        string(name: 'REPO_URL', defaultValue: 'https://github3.cisco.com/TestVGE/secure-gateway', description: ''),
        string(name: 'NODE_LABEL', defaultValue: 'vgs', description: ''),
    ])
   ])
timestamps {
try {
    node (params.NODE_LABEL) {
        try {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e',usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            wrap([$class: 'BuildUser']) {
                def server = Artifactory.server(params.ARTIFACTORY)
                def descriptor = Artifactory.mavenDescriptor()
                def rtMaven = Artifactory.newMavenBuild()
                def buildInfo = Artifactory.newBuildInfo()
                rtMaven.deployer releaseRepo:'cisco-spvss-staging', snapshotRepo:'cisco-spvss-staging', server: server
                //rtMaven.resolver releaseRepo:'repo', snapshotRepo:'repo', server: server
                rtMaven.deployer.deployArtifacts = (params.DEPLOY) ? true : false
                rtMaven.opts = "-Xmx2048m -XX:MaxPermSize=1024m" 
                rtMaven.tool = params.MAVEN_VERISON
                //descriptor.failOnSnapshot = true
                def skipTests=(params.TEST) ? "" : " -Dmaven.test.skip=true -DskipTests=true "
            
                stage('Setup') {
                    try {
                        echo "="*80 + "\nEnviroment Setup...".toString() + "\n" + "="*80
                        tool name: params.MAVEN_VERISON, type: 'maven'
                        //tool name: params.JAVA_VERISON, type: 'jdk'
                        if (params.CLEANUP)
                            step([$class: 'WsCleanup', cleanWhenFailure: false])
                        if (params.REPO_URL =~ /wwwin-svn-jrsm/)
                            checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', 
                                remote: params.REPO_URL]], workspaceUpdater: [$class: 'UpdateWithCleanUpdater']])       
                        else
                            checkout([$class: 'GitSCM', branches: [[name: '*/master']],doGenerateSubmoduleConfigurations: false, extensions: [],submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e', 
                                url: params.REPO_URL]]])
                        pom = readMavenPom file:"${params.MAVEN_POM}".toString()
                        echo "===> Incrementing version"
                        //newVersion=pom.version.split('-')[0]+"-"+(pom.version.split('-')[1].toInteger()+1)
                        newVersion=pom.version.split('-')[0]+"-"+(pom.version.split('-')[1].toInteger())
                        currentBuild.displayName = "#${env.BUILD_NUMBER}.${pom.name} " + ("${newVersion}" != null ? "${newVersion}":"").toString()
                        currentBuild.description = "[node:${env.NODE_NAME}]\t[user:${env.BUILD_USER_ID}]".toString()
                        echo "=== >current version= " + pom.version + "\tnew version= " + newVersion
                        descriptor.version = newVersion
                        descriptor.pomFile = params.MAVEN_POM
                        descriptor.transform()
                        pom=readMavenPom file:"${params.MAVEN_POM}".toString()
                        //rtMaven.deployer.addProperty("SVN_REVISION", "${env.SVN_REVISION}").addProperty("compatibility", "1", "2", "3")
                    } catch (error) { throw error }    
                }   
                stage('Build'){
                    try {
                        echo "="*80 + "\nBuilding ${newVersion}...".toString() + "\n" + "="*80
                        buildInfo.env.capture = true
                        buildInfo.env.collect()
                        concurrency: 1
                        rtMaven.run pom: "${params.MAVEN_POM}".toString(), goals:"-Dmaven.repo.local=${WORKSPACE} ${params.MAVEN_GOALS} -DskipSCMCheck ${skipTests} -Dmaven.artifact.threads=1".toString(), buildInfo: buildInfo
                        if (params.TEST) {
                           zip archive: true, dir: "../".toString(), glob: '**/log/*', zipFile: 'logs.zip'
                           step([$class: 'JUnitResultArchiver', testResults: '**/TEST-*.xml'])
                        }
                        server.publishBuildInfo buildInfo
                        //rtMaven.run pom: "${params.MAVEN_POM}".toString(), goals:"build-helper:parse-version versions:set -Dmaven.repo.local=${WORKSPACE} -DnewVersion=${newVersion} versions:commit scm:checkin -Dusername=${env.USERNAME} -Dpassword=${env.PASSWORD} -Dmessage='Build: Version update by jenkins (${env.BUILD_ID})' -DpushChanges".toString()
                        archiveArtifacts allowEmptyArchive: true, artifacts: "**/*${pom.name}-${newVersion}*,**/logs.zip".toString(), caseSensitive: false, excludes: '**/*.xml,**/*-sources*', fingerprint: true
                    } catch (error) { throw error }
                }
                stage('Code Quality') {
                    try {
                    echo "="*80 + "\nCode Analysis...".toString() + "\n" + "="*80
                    if (params.MAVEN_GOALS =~ /.*checkstyle.*/) 
                        step([$class: 'CheckStylePublisher'])
                    if (params.MAVEN_GOALS =~ /.*dry.*/) 
                        step([$class: 'DryPublisher'])
                    if (params.MAVEN_GOALS =~ /.*pmd.*/) 
                        step([$class: 'PmdPublisher'])
                    if (params.MAVEN_GOALS =~ /.*findbugs.*/) 
                        step([$class: 'FindBugsPublisher'])
                    if (params.MAVEN_GOALS =~ /.*clover.*/) 
                       step([$class: 'CloverPublisher'])
                    }catch (error) {throw (error) }       
                }
                /*stage ('Deploy') {  //deploy via artifactory directly and now with maven
                    try {
                        echo "="*80 + "\nDeploying...".toString() + "\n" + "="*80
                        concurrency: 1
                        def uploadSpec = """{
                            "files": [{"pattern": ".*${pom.name}-${newVersion}.*\\..*",
                            "target":"cisco-spvss-staging/com/nds/ch/vgs3/${pom.name}/${newVersion}",
                            "recursive": "true",
                            "regexp": "true" 
                            }]}"""
                        server.upload(uploadSpec)
                        server.publishBuildInfo buildInfo
                        //buildInfo.retention maxBuilds: 10, maxDays: 7, doNotDiscardBuilds: ["3", "4"], deleteBuildArtifacts: true
                        //newVersion=pom.version.split('-')
                        //newVersion[1]= newVersion[1].toInteger()+1       
                        //newVersion = newVersion.join('-')
                        //echo "===> Version set to:" + newVersion
                        //rtMaven.run pom: "${params.MAVEN_POM}".toString(), goals: "build-helper:parse-version versions:set -DnewVersion=${newVersion} versions:commit scm:checkin -Dusername=${env.USERNAME} -Dpassword=${env.PASSWORD} -Dmessage='Build: Version update by jenkins (${env.BUILD_ID})'-DpushChanges".toString()
                    } catch (error) { throw error }
                }*/
        }//end BuildUser
        }//end withCredentials
        } catch (error) { throw error }
    }
    node ("drm") {   
        try {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e',usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            wrap([$class: 'BuildUser']) {
            stage ('Dockerize') {  //deploy via artifactory directly and now with maven
                try {
                    echo "="*80 + "\nDockerizing...".toString() + "\n" + "="*80
                    tool name: params.MAVEN_VERISON, type: 'maven'
                    checkout([$class: 'GitSCM', branches: [[name: '*/master']],doGenerateSubmoduleConfigurations: false, extensions: [],submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'c2b9fdc3-7562-4bc4-b4f6-3de05444999e', 
                        url: "https://github3.cisco.com/vgehe-devops/docker"]]])
                    /*def download = """{
                        "files": [{"pattern": ".*${pom.name}-${newVersion}.*\\..*",
                        "target":"cisco-spvss-staging/com/nds/ch/vgs3/${pom.name}/${newVersion}",
                        "recursive": "true",
                        "regexp": "true" 
                    }]}"""*/
                    sh returnStatus: true, script: """
                        cd sgw
        				if [[ -f "build-params.properties" ]]; then
    					   sed -i "s/project::name=general/project::name=general/g" build-params.properties
        				else
        				    echo "using project_name=general"
        				fi
        				./build.sh push
    			        fi"""
                    //server.upload(uploadSpec)
                    //server.publishBuildInfo buildInfo
                    //buildInfo.retention maxBuilds: 10, maxDays: 7, doNotDiscardBuilds: ["3", "4"], deleteBuildArtifacts: true
                    //newVersion=pom.version.split('-')
                    //newVersion[1]= newVersion[1].toInteger()+1       
                    //newVersion = newVersion.join('-')
                    //echo "===> Version set to:" + newVersion
                    //rtMaven.run pom: "${params.MAVEN_POM}".toString(), goals: "build-helper:parse-version versions:set -DnewVersion=${newVersion} versions:commit scm:checkin -Dusername=${env.USERNAME} -Dpassword=${env.PASSWORD} -Dmessage='Build: Version update by jenkins (${env.BUILD_ID})'-DpushChanges".toString()
                } catch (error) { throw error }
            }
            stage('DummyStage') {}
            }//end BuildUser
            }//end withCredentials
        } catch (error) { throw error }
    }
} catch (error) {
    node {
        currentBuild.result = "FAILED"
        emailext (attachLog: true,compressLog: true, mimeType: 'text/html', preSendScript: """msg.addHeader("X-Priority", "1 (Highest)"); msg.addHeader("Importance", "High");""",
            subject: "Attention required: ${env.BUILD_TAG} [${currentBuild.result}!]".toString(),
            body: '${JELLY_SCRIPT,template="html-test"}',
            to:"${params.MAIL}".toString(),
            recipientProviders: [[$class: 'CulpritsRecipientProvider'],[$class: 'RequesterRecipientProvider'],[$class: 'FirstFailingBuildSuspectsRecipientProvider']]        )
        }
} finally { node { step([$class: 'LogParserPublisher', parsingRulesPath: 'parser_maven_build.txt', useProjectRule: false])} }
}

@NonCPS
def nodeNames() {
  return jenkins.model.Jenkins.instance.nodes.collect { node -> node.name }
}
