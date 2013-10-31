package depsearch.indexer

import depsearch.common.model.Dependency
import java.io.InputStream

trait DependencyParser {
  def parse(in: InputStream): Dependency
}