@Library('github.com/ahridinOrganization/jenkinsDSL') _

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
        echo "===> Job to be run = " + config.JOB_SCRIPT
        jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', targets: "${config.JOB_SCRIPT}", unstableOnDeprecation: true        
    }
}






