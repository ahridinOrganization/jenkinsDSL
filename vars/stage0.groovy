#!groovy
/**
 * Prints a stub message for testing purposes.
 */
def pipeline
node {
    //pipeline = load 'pipeline.groovy'
    //pipeline.devQAStaging()
    pipeline = load 'helloworld.groovy'
    helloworld.printHello("Hello from STAGE-0!")
    
}
//pipeline.production()

return this;



