package com.glasstowerstudios.gruel.tasks;

import org.gradle.api.tasks.TaskAction
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.Delete
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.Project

/**
 * A task for uninstalling a library from the local maven repository.
 */
class UninstallTask extends Delete {

  @TaskAction
  def uninstall() {
    def mavenRepoUrlString = project.repositories.mavenLocal().getUrl().toString()
    def baseMavenRepo = mavenRepoUrlString.substring(5, mavenRepoUrlString.size() - 1)
    def groupDir = project.group.toString().replaceAll('\\.', File.separator)
    def paths = [ baseMavenRepo, groupDir, project.name]
    def mavenPath = paths.join(File.separator);
    File dir = project.file(mavenPath)
    ConfigurableFileCollection  collection = project.files {dir.listFiles()}
    collection.each {File file ->
      project.delete(file)
    }

    // Now, proceed up the directory hierarchy and delete empty directories.
    def nextDir = dir;
    def baseMavenRepoFile = project.file(baseMavenRepo + File.separator)
    while (!baseMavenRepoFile.absolutePath.equals(nextDir.absolutePath)) {
      nextDir = project.file([nextDir, '..'].join(File.separator))
      def nextDirFiles = nextDir.listFiles()
      ConfigurableFileCollection filesInNextDir = project.files { nextDirFiles }
      filesInNextDir.each { File subDirFiles ->
        println "Checking ${subDirFiles}"
        if (project.files { subDirFiles.listFiles() }.getFiles().size() == 0) {
          project.delete(subDirFiles)
        }
      }
    }
  }
}
