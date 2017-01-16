def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    println "${config.Name}"
    node () {    
        freeStyleJob("${jobFolder}/${config.Name}")
        pipelineJob('example') {
            definition {
                 cps {
                      script(readFileFromWorkspace('project-a-workflow.groovy'))
                    sandbox()
                  }
                             }
        }
         }
    
 }
