class BuildFramework {
    static ant(dslFactory, jobName, antTargets) {
        dslFactory.job(jobName) {
            steps {
                ant(antTargets)
            }
        }
    }
}
