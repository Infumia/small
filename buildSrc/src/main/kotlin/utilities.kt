fun AbstractArchiveTask.define(
  name: String = project.getQualifiedProjectName(),
  classifier: String? = null,
  version: String? = null
) {
  archiveClassifier.set(classifier)
  archiveClassifier.convention(classifier)
  archiveBaseName.set(name)
  archiveBaseName.convention(name)
  archiveVersion.set(version)
  archiveVersion.convention(version)
}
