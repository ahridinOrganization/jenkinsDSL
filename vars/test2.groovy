def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    /*jobDsl targets: "mavenJob.groovy",
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',           
*/
 public static def job = { 
     dslFactory, jobName -> return dslFactory.job("MyJob") {
          parameters {
            stringParam('CONFIGURATION_REPO', 'https://github.com/edx/configuration.git')                      
            stringParam('CONFIGURATION_BRANCH', 'master')                      
                 }
           }
                              
     }     /*
               Run arbitrary remote commands on a host belonging to a target environment, deployment and cluster,
               in a specified region.
             */
}
