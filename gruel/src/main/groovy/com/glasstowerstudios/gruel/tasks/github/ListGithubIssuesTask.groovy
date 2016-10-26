package com.glasstowerstudios.gruel.tasks.github

import com.glasstowerstudios.gruel.tasks.github.GithubTask
import org.gradle.api.tasks.TaskAction
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GHIssueState

class ListGithubIssuesTask extends GithubTask {
  @TaskAction
  def doTask() {
    def repo = getRepository()
    def issues = repo.getIssues(GHIssueState.ALL);
    for (def nextIssue : issues) {
      println "Issue #" + nextIssue.number + ": " + nextIssue.title
    }
  }
}
