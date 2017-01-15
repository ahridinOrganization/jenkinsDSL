#!groovy



//package utilities

//public class Defaults {
  static def getBaseJob(def job, Closure optionalClosure = null){
      job.with { description 'A default description.' }
      if(optionalClosure) {
         optionalClosure.delegate = job
         optionalClosure.run()
      }
   }
return this;
//}
