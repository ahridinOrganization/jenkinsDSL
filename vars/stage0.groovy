#!groovy
/**
 * Prints a stub message for testing purposes.
 */
def pipeline


def helloworld = fileLoader.load('vars/helloworld'); 

node {
    //helloworld.printHello("Hello from STAGE-0!")
    //InputStream streamFileFromWorkspace('helloworld.groovy')
    
    //helloworld.printHello("Hello from STAGE-0!")
    //pipeline = load 'pipeline.groovy'
    //pipeline.devQAStaging()
    //pipeline = load 'vars/helloworld.groovy'
    helloworld.printHello("Hello from STAGE-0!")
    
}
//pipeline.production()

return this;



