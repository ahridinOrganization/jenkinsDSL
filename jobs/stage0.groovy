String basePath = 'example'

folder(basePath) {
    description 'This example shows hwp to use automatically generated DSL provided by other plugins.'
}

freeStyleJob("$basePath/${config.MESSAGE}") {
   parameters {
     stringParam(MESSAGE, ${config.MESSAGE}) 
   }
   steps {   bat('echo $MESSAGE')  }
}

