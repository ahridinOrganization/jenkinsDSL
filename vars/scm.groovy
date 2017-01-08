#!groovy
def credentials="credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397'";

def checkout() {
	println "CSM checkout!"
    checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: 'https://wwwin-svn-jrsm.cisco.com/nds/ch_repo/tags/vgs3/acman']], workspaceUpdater: [$class: 'UpdateUpdater']])
	}

return this;
