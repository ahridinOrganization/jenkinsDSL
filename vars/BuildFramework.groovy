class BuildFramework {
    
    println (jobName)
    println (antTargets)
    static ant(dslFactory, jobName, antTargets) {
        dslFactory.freeStyleJob(jobName) {
              }
    }
}
