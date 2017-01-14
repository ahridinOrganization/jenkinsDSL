#!groovy

job('testMe') {
   parameters {
     stringParam('MESSAGE', 'Hello world!') 
   }
   properties {
     rebuild {
       autoRebuild()
     }
   }
  steps {
    shell('echo $MESSAGE')
  }
}
