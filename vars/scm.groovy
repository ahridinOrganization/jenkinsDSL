#!groovy
def scmCredentialsId='29bae92d-6b9c-4f76-a54e-5b72f851a397'
	def scmRemote='https://wwwin-svn-jrsm.cisco.com/nds/ch_repo/tags/vgs3/acman'
	def scmUpdater='UpdateUpdater'
	def scmType='SubversionSCM'

def checkout() {
	println "========= CSM checkout ========= "
    	checkout([$class: scmType, additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: scmCredentialsId, depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: scmRemote]], workspaceUpdater: [$class: scmUpdater]])
	}


return this;




 



////catchError {
    // some block
//}

//ckean workspac when done step([$class: 'WsCleanup', deleteDirs: true, notFailBuild: true])
//log parser step([$class: 'LogParserPublisher', showGraphs: true, useProjectRule: false])
//figlet 'SVN checkout'
