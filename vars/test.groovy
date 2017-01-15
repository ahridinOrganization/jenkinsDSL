//@Library('github.com/ahridinOrganization/jenkinsDSL') _

//import utilities.Defaults
fileLoader.withGit('https://github.com/ahridinOrganization/jenkinsDSL.git', 'master', null, '') {
	//def stage0 = fileLoader.load('vars/stage0'); 
	utilities = fileLoader.load('vars/utilities'); 
}

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
    //    def job=stage0(MESSAGE:config.MESSAGE)
        //jobDsl scriptText:
  
   jobDsl targets: ['stage0.groovy'].join('\n'),
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',
           //additionalClasspath: ['libA.jar', 'libB.jar'].join('\n')
    //utilities.getBaseJob(freeStyleJob('myJob')){
    scm {
      git ('git@github.com/william/the-juggler.git')
   }
          }
}
