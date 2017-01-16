def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    /*jobDsl targets: "mavenJob.groovy",
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',           
*/
 public static def job = { dslFactory, jobName ->
        return dslFactory.job(${jobName}") {
                 parameters {
                stringParam('CONFIGURATION_REPO', extraVars.get('CONFIGURATION_REPO', 'https://github.com/edx/configuration.git'),
                            'Git repo containing the analytics pipeline configuration automation.')
                stringParam('CONFIGURATION_BRANCH', extraVars.get('CONFIGURATION_BRANCH', 'master'),
                            'e.g. tagname or origin/branchname')
            }
                              
            /*
               Run arbitrary remote commands on a host belonging to a target environment, deployment and cluster,
               in a specified region.
             */
}
