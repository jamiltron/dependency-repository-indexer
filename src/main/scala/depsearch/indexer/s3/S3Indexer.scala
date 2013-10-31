package depsearch.indexer.s3

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectSummary
import java.util.concurrent.TimeUnit
import com.amazonaws.services.s3.model.ListObjectsRequest
import S3Lister._
import depsearch.indexer.IvyDependencyParser
import depsearch.db.DependencyDB
import depsearch.common.model.Dependency

class S3Indexer {
  val s3: AmazonS3 = new AmazonS3Client
  val executor = Executors.newFixedThreadPool(5)
  
  val db = new DependencyDB() {
    def update(d: Dependency): Unit = {
      println(d)
    }
  }
  
  def index(bucket: String) {
    val r = new ListObjectsRequest().withBucketName(bucket)
    
    s3.listBatch(r) { list =>
      list.grouped(100) foreach { g =>
        executor.submit(new IndexWorker(s3, db, g))
      }
    }
    
    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.MINUTES)
  }
  
  class IndexWorker(s3: AmazonS3, db: DependencyDB, list: Iterable[S3ObjectSummary]) extends Callable[Boolean] {
    val parser = new IvyDependencyParser
    
    val ivyFilePattern = """.*/ivy-[^/]+.xml$""".r
    
    def call(): Boolean = {
      for (elem <- list) {
        if (ivyFilePattern.findFirstIn(elem.getKey()).isDefined) {
          val obj = s3.getObject(new GetObjectRequest(elem.getBucketName(), elem.getKey()))
          val in = obj.getObjectContent()
          
          try {
            db.update(parser.parse(in))
          } catch {
            case e: Exception => {
              System.err.println("+" * 50)
              System.err.println(elem.getKey() + ", " + e.getMessage())
              System.err.println("+" * 50)              
            }
          } finally {
            in.close()
            obj.close()
          }
        }
      }

      true
    }
  }
}



object S3Indexer {
  def main(args: Array[String]) {
    new S3Indexer().index(args(0))
  }
}