package depsearch.indexer

import org.junit.Test
import org.junit.Assert._
import depsearch.common.model.Version

class IvyDependencyParserTest {
  @Test
  def test() {
    val s = getClass().getClassLoader().getResourceAsStream("depsearch/indexer/test-ivy-files/ivy-1.6.2.xml")
    val dep = new IvyDependencyParser().parse(s)
    s.close()
    
    assertEquals(("com.amazonaws", "aws-java-sdk"), (dep.org, dep.group))
    assertEquals(Version("1.6.2", "default", "release"), dep.version)
  }
}