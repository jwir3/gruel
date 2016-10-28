package com.glasstowerstudios.gruel.tasks.jira

import com.glasstowerstudios.gruel.tasks.jira.JiraTask
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Issue.FluentUpdate
import net.rcarz.jiraclient.JiraClient
import org.gradle.api.tasks.TaskAction

/**
 * A type of {@link GruelTask} that allows the update of JIRA issues from within
 * the gradle build system.
 */
class JiraUpdateTask extends JiraTask {

  private jql
  private fieldUpdates = new LinkedHashMap<String, Object>()

  void setJql(String jql) {
    this.jql = jql
  }

  void setFieldUpdates(LinkedHashMap<String, Object> fieldUpdates) {
    this.fieldUpdates = fieldUpdates
  }

  @TaskAction
  def doTask() {
    def result = searchForIssues(jql)

    for (Issue issue : result.issues) {
      FluentUpdate fluentUpdate = issue.update()
      for(Map.Entry<String, Object> entry : fieldUpdates.entrySet()) {
        fluentUpdate.field(entry.getKey(), entry.getValue())
      }

      fluentUpdate.execute();
    }
  }
}
