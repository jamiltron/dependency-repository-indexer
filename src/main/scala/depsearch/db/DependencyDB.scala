package depsearch.db

import depsearch.common.model.Dependency

trait DependencyDB {
  def update(d: Dependency): Unit
}