def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def job  
    def params=""    
    for (item in config) {           
          params=params+"stringParam('${item.key.toString()}','${item.value.toString()}')\n"           
    }       
    println params
    node () {
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
        folder("${config.JOB_FOLDER}")
            
        """ 
        job = build (job:"${config.JOB_FOLDER}/${config.JOB}")                     
    } //end node
} //end call
