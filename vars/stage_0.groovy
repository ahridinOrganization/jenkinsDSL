def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def slave = nodeNames()
    node (slave){
         build job: 'test_jobs', parameters: [[$class: 'StringParameterValue', name: 'param1', value:
            'test_param'], [$class: 'StringParameterValue', name:'dummy', value: "${index}"]]
         }
         stage("Checkout") {
            checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: 'https://wwwin-svn-jrsm.cisco.com/nds/ch_repo/tags/vgs3/acman']], workspaceUpdater: [$class: 'CheckoutUpdater']])        
         }
        stage('Main') {
            docker.image(config.environment).inside { sh config.mainScript}
        }
        stage('Post') { sh config.postScript}
        }
}

// This method collects a list of Node names from the current Jenkins instance
@NonCPS
def nodeNames() {
  return jenkins.model.Jenkins.instance.nodes.collect { node -> (node.name=="linux")? node:null}
}


