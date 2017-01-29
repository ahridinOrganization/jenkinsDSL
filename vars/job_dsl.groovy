def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config      
    body()	 
    def job  
    def params="" 
    def jobFolder="STAGE-0"
    for (item in config) {           
          params=params+"stringParam('${item.key.toString()}','${item.value.toString()}')\n"           
    }       
    node () {    
       println params
       jobDsl ignoreMissingFiles: true, lookupStrategy: 'SEED_JOB', removedJobAction: 'DISABLE', removedViewAction: 'DELETE', scriptText:"""
            folder("${jobFolder}")
            pipelineJob("${jobFolder}/${config.NAME}"){        
                definition {
                          
                } //end definition
            } //end pipelinejob
        """ 
        job = build (job:"${jobFolder}/${config.NAME}")                     
    } //end node
} //end call
