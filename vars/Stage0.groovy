
import com.cloudbees.groovy.cps.NonCPS
def version = '1.0'
@NonCPS
def run() {
  def helloworld = fileLoader.load('helloworld');
  def environment = fileLoader.load('environment');
  
  stage ('Preparations') {
	helloworld.printHello("Good morning!")
	environment.dumpEnvVars()			
	}
	
}
return this;



 //the you can call the function in file1 as


