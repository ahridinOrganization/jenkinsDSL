#!groovy
/**
 * Prints a stub message for testing purposes.
 */
def helloworld = fileLoader.load('vars/helloworld'); 
//def call() {
    node {
    //    echo ("Hello I am stage0 groovy and I am calling helloworld")
        helloworld.printHello("Hello from STAGE-00000!")
      //  checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: '29bae92d-6b9c-4f76-a54e-5b72f851a397', depthOption: 'infinity', ignoreExternalsOption: false, local: '.', remote: scmRemote]], workspaceUpdater: [$class: scmUpdater]])
   }
//def myJob = freeStyleJob('SimpleJob')
//myJob.with {
  //  description 'A Simple Job'
//}
//}
//pipeline.production()

return this;

