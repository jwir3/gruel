package com.glasstowerstudios.gruel.tasks.github

import com.glasstowerstudios.gruel.tasks.github.GithubTask
import org.gradle.api.tasks.TaskAction

/**
 * A type of {@link GithubTask} that will remove a set of labels from the github
 * issue having a specified issue number.
 */
class RemoveLabelsTask extends GithubTask {
  private int issueNumber;
  private List<String> labels;

  void setIssueNumber(int issueNumber) {
    this.issueNumber = issueNumber;
  }

  void setLabels(List<String> labels) {
    this.labels = labels;
  }

  List<String> getLabels() {
    return this.labels;
  }

  int getIssueNumber() {
    return this.issueNumber;
  }

  @TaskAction
  def doTask() {
    def repo = project.github.connectToRepository()
    def issue = repo.getIssue(this.issueNumber)
    def issueLabels = issue.getLabels()
    def labelsToKeep = []
    for (def nextLabel : issueLabels) {
      if (!this.labels.contains(nextLabel.getName())) {
        labelsToKeep << nextLabel.getName()
      }
    }

    issue.setLabels(labelsToKeep as String[])
  }
}
