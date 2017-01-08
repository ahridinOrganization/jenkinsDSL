#!groovy
def credentials="credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397'";

def checkout() {
  /*checkout([$class: 'GitSCM', 
			branches: [[name: '*/master']], 
			doGenerateSubmoduleConfigurations: false, 
			extensions: [], 
			gitTool: 'Default', 
			submoduleCfg: [], 
			userRemoteConfigs: [[credentialsId: '0a33916f-a110-4108-bb0a-e94ff08420fe', url: 'xxx']]
		])*/
  checkout([$class: 'SubversionSCM', 
			additionalCredentials: [], 
			excludedCommitMessages: '', 
			excludedRegions: '', 
			excludedRevprop: '', 
			excludedUsers: '', 
			filterChangelog: false, 
			ignoreDirPropChanges: false, 
			includedRegions: '', 
			locations: [[
				credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', 
				depthOption: 'infinity', ignoreExternalsOption: false, 
				local: '.', remote: 'https://wwwin-svn-jrsm.cisco.com/nds/ch_repo/tags/vgs3/acman'
			]], 
			workspaceUpdater: [$class: 'UpdateUpdater']
		])
}

return this;
