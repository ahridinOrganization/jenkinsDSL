#!groovy
import com.cloudbees.groovy.cps.NonCPS

def somename
environment = load '/environment.groovy'
 //the you can call the function in file1 as

@NonCPS
def run() { environment.dumpEnvVars()}
	
return this;



 //the you can call the function in file1 as


