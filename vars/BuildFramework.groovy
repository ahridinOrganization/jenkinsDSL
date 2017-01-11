class BuildFramework {
    static ant(dslFactory, jobName, antTargets) {
        dslFactory.freeStyleJob(jobName) {
            steps {
                ant(antTargets)
            }
        }
    }
}
