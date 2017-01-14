import utilities.Defaults

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
    //    def job=stage0(MESSAGE:config.MESSAGE)
        //jobDsl scriptText:
  
 
    Defaults.getBaseJob(freeStyleJob('myJob')){
    scm {
      git ('git@github.com/william/the-juggler.git')
   }
          }
}
}
