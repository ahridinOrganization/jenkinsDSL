def call(body) {
    
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
        pipelineJob('pipelineJob') {
            definition {
                cps {
                    script('''
                            pipeline {
                                tools { // Make sure that the tools we need are installed and on the path.
                                    maven "Maven 3.0.4"
                                    jdk "Oracle JDK 8u40"
                                }
                                
                                agent any // Run on any executor.
                                stages {
                                    stage("build") {
                                        steps {
                                            echo "hello from stage-0 ${config.Name}"                                                                                        
                                        }
                                    }
                                }

                            }
                            ''')
                    sandbox()
                    }
                }
            }
        }        
}
