package depsearch.common.model

case class Dependency(
  org: String,
  group: String,
  version: Version,
  publication: Long,
  artifacts: Seq[Artifact],
  description: Option[Description],
  license: Option[License]
)

case class Artifact(
  name: String,
  aType: String,
  ext: String
)  

case class Version(
  version: String,
  branch: String,  
  status: String
)  

case class License(
  name: String,
  url: String
)

case class Description(
  text: String,
  homepage: String
)