pipeline {
    // Make sure that the tools we need are installed and on the path.
    tools {
        maven "Maven 3.3.9"
        jdk "Oracle JDK 8u40"
    }

    // Run on any executor.
    agent label:""

    // The order that sections are specified doesn't matter - this will still be run
    // after the stages, even though it's specified before the stages.
    postBuild {
        // No matter what the build status is, run these steps. There are other conditions
        // available as well, such as "success", "failed", "unstable", and "changed".
        always {
            archive "target/**/*"
            junit 'target/surefire-reports/*.xml'
        }
    }

    stages {
        // While there's only one stage here, you can specify as many stages as you like!
        stage("build") {
            sh 'mvn clean install -Dmaven.test.failure.ignore=true'
        }
    }

}
