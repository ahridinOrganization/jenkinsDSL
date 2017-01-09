#!groovy
/**
 * Prints a stub message for testing purposes.
 */
def version = '1.0'
def helloworld
node {
    helloworld = fileLoader.load('helloworld.groovy'); 
    
}()


return this;
