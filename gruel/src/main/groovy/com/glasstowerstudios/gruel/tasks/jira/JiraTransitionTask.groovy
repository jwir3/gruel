package com.glasstowerstudios.gruel.tasks.jira

import com.glasstowerstudios.gruel.tasks.jira.JiraTask
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Issue.FluentTransition
import org.gradle.api.tasks.TaskAction

/**
 * A type of {@link GruelTask} that allows the update of JIRA issues from within
 * the gradle build system, while at the same time transitioning the JIRA issue
 * to a new state.
 */
class JiraTransitionTask extends JiraTask {

  private jql
  private toStatus
  private fieldUpdates = new LinkedHashMap<String, Object>()

  void setJql(String jql) {
    this.jql = jql
  }

  void setToStatus(String toStatus) {
    this.toStatus = toStatus
  }

  void setFieldUpdates(LinkedHashMap<String, Object> fieldUpdates) {
    this.fieldUpdates = fieldUpdates
  }

  @TaskAction
  def doTask() {
    def result = searchForIssues(jql);

    for (Issue issue : result.issues) {
      FluentTransition fluentTransition = issue.transition()
      for(Map.Entry<String, Object> entry : fieldUpdates.entrySet()) {
        fluentTransition.field(entry.getKey(), entry.getValue())
      }

      if (this.toStatus == null) {
        fluentTransition.execute();
      } else {
        fluentTransition.execute(toStatus);
      }
    }
  }
}
