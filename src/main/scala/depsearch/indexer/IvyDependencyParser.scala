package depsearch.indexer

import java.io.InputStream
import scala.xml.XML
import depsearch.common.model._

class IvyDependencyParser extends DependencyParser {
  def parse(in: InputStream): Dependency = {
    val module = XML.load(in)
    
    val info = module \ "info"
    
    val version = Version(info \ "@revision" text, info \ "@branch" text, info \ "@status" text)
    
    val description = {
      val d = info \ "description"
      
      if (d.isEmpty) {
        None
      } else {
        val t = d.text
        val h = d \ "@homepage" text
        
        if (t.isEmpty && h.isEmpty) {
          None
        } else {
          Some(Description(d.text, d \ "@homepage" text))
        }
      }
    }
    
    val license = {
      val l = info \ "license"
      if (l.isEmpty) {
        None
      } else {
        Some(License(l \ "@name" text, l \ "@url" text))
      }
    }
    
    val org = info \ "@organisation" text
    val group = info \ "@module" text
    val publication = (info \ "@publication" text) toLong
    
    val artifacts = module \ "publications" \ "artifact" map { a =>
      Artifact(a \ "@name" text, a \ "@type" text, a \ "@ext" text)
    }
    
    Dependency(org, group, version, publication, artifacts, description, license)
  }
}