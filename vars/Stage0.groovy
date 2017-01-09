
def helloworld = fileLoader.load('helloworld');
def environment = fileLoader.load('environment');
 //the you can call the function in file1 as

stage ('Preparations') {
	helloworld.printHello("Good morning!")
	environment.dumpEnvVars()			
}
