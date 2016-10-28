package com.glasstowerstudios.gruel.tasks.github

import com.glasstowerstudios.gruel.tasks.github.GithubTask
import org.kohsuke.github.GHUser
import org.gradle.api.tasks.TaskAction

class ChangeAssigneeTask extends GithubTask {
  private String assignee;
  private int issueNumber;

  int getIssueNumber() {
    return issueNumber
  }

  void setIssueNumber(int issueNumber) {
    this.issueNumber = issueNumber
  }

  String getAssignee() {
    return this.assignee;
  }

  void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  @TaskAction
  def doTask() {
    def repo = project.github.connectToRepository()
    def issue = repo.getIssue(this.issueNumber)
    def collaborators = repo.listCollaborators()
    for (GHUser nextUser : collaborators) {
      if (nextUser.login == getAssignee()) {
        issue.assignTo(nextUser)
        return;
      }
    }
  }
}
