def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()	
    node () {
        echo config.NAME
        echo config.MAIL
        jobDsl scriptText:"""folder("${jobFolder}")"""
        //jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: 'stage_0_pipeline.groovy', unstableOnDeprecation: true        
        jobDsl scriptText:"""
            pipelineJob("${jobFolder}/${config.NAME}")
                {
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
                        string(name: 'MAIL', defaultValue: "${config.MAIL}", description: ''),
                        string(name: 'REPO_URL', defaultValue: 'https://github3.cisco.com/TestVGE/secure-gateway', description: ''),
                        string(name: 'NODE_LABEL', defaultValue: 'vgs', description: ''),
                    ])
                   ])   
                   definition {
                        cps {
                            script(readFileFromWorkspace("${config.SCRIPT}"))
                            sandbox()
                        } //end cps
                    } //end definition
               } //end pipelinejob
        """        
        job = build job: "${jobFolder}/${config.NAME}"	
    } //end node
} //end call
