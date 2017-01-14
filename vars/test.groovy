def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
        def job=stage0(MESSAGE:config.MESSAGE)
        //jobDsl scriptText:
    }
}
