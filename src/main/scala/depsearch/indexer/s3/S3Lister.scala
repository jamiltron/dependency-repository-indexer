package depsearch.indexer.s3

import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.services.s3.model.ListObjectsRequest
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.AmazonS3

class S3Lister(s3: AmazonS3) {
  def list(r: ListObjectsRequest)(handler: (S3ObjectSummary) => Unit) {
    var listing = s3.listObjects(r)
    
    for (o <- listing.getObjectSummaries.asScala) {
      handler(o)
    }
    
    while (listing.getNextMarker != null) {
      listing = s3.listNextBatchOfObjects(listing)
      for (o <- listing.getObjectSummaries.asScala) {
        handler(o)
      }
    }
  }
  
  def listBatch(r: ListObjectsRequest)(handler: (Iterable[S3ObjectSummary]) => Unit) {
    var listing = s3.listObjects(r)
    handler(listing.getObjectSummaries().asScala)
  
    while (listing.getNextMarker != null) {
      listing = s3.listNextBatchOfObjects(listing)
      handler(listing.getObjectSummaries().asScala)
    }
  }
}

object S3Lister {
  implicit def amazonClientToS3Lister(c: AmazonS3): S3Lister = new S3Lister(c)
}