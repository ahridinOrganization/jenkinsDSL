#!groovy

freeStyleJob('testMe') {
   parameters {
     stringParam('MESSAGE', 'Hello world!') 
   }
   properties {
     rebuild {
       autoRebuild()
     }
   }
  steps {
    bat('echo $MESSAGE')
  }
}
