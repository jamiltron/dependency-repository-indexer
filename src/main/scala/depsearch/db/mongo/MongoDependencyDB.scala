package depsearch.db.mongo

import depsearch.db.DependencyDB
import depsearch.common.model._
import com.mongodb.DB
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBList
import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.model.License

class MongoDependencyDB(db: DB) extends DependencyDB {
  def query(query: String): Unit = {
    
  }
  
  def update(d: Dependency): Unit = {
    val repo = db.getCollection("repo_v2")
    
    val o = new BasicDBObject
    o.put("org", d.org)
    o.put("group", d.group)
    o.put("publication", d.publication)

    val version = {
      val v = new BasicDBObject
      v.put("version", d.version.version)
      v.put("branch", d.version.branch)
      v.put("status", d.version.status)
      v
    }
    
    o.put("version", version)
    
    val license = d.license.map { l =>
      val o = new BasicDBObject
      o.put("name", l.name)
      o.put("url", l.url)
    }
    
    val description = d.description.map { d =>
      val o = new BasicDBObject
      o.put("text", d.text)
      o.put("homepage", d.homepage)
    }
    
    if (license.isDefined) {
      o.put("license", license.get)
    }
    
    if (description.isDefined) {
      o.put("description", description.get)
    }
    
    
    val artifacts = d.artifacts.filter(_.aType == "jar").map(_.name)
    
    o.put("artifacts", artifacts.asJava)
    
    repo.insert(o)
  }
}