#!/usr/bin/env groov
def call(body) {
    def config = [:]
    def jobFolder="STAGE-0"
    def job	
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    dslFactory.job("${jobFolder}/${config.Name}")
    println "${config.Name}"
    node () {    
        freeStyleJob("${jobFolder}/${config.Name}")
        git url: 'https://github.com/jfrogdev/project-examples.git'
        
        
        }
    
 }
