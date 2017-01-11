def call(body) {
    def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
node {
    BuildFramework.ant(this, config.name, config.goals) 
}
}
