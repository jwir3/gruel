package com.glasstowerstudios.gruel.tasks.github

import com.glasstowerstudios.gruel.tasks.GruelTask
import org.gradle.api.tasks.TaskAction

class GithubTask extends GruelTask {
  @TaskAction
  def doTask() {
    println "Github Username: " + project.github.username;
    println "Github Password: " + project.github.password;
    println "Github Repo: " + project.github.repo;
  }
}
