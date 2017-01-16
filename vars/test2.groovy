class Utils {
    static def makeMeABasicJob(def context) {
        context.freeStyleJob() {
            //generic stuff
            name "something"
            description "something else"
        }

    }
}

def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    node {
        def job1 = Utils.makeMeABasicJob(this) //Passing the groovy file class as the resolution context
        job1.with({ 
               steps {  shell('echo Hello') }     
      })
    jobDsl targets: ['*.groovy'].join('\n'),
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',
           
    }
    
    /*jobDsl targets: "mavenJob.groovy",
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',           
*/
 /*public static def job = { 
     
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
    */

