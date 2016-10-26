package com.glasstowerstudios.gruel.tasks.github

import com.glasstowerstudios.gruel.tasks.GruelTask
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRepository

class GithubTask extends GruelTask {
  def getGithubConnection() {
    return project.github.connect()
  }

  def getRepository() {
    return project.github.connectToRepository()
  }
}
