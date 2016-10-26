package com.glasstowerstudios.gruel.tasks.jira

import com.glasstowerstudios.gruel.tasks.GruelTask
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Issue.FluentUpdate
import net.rcarz.jiraclient.JiraClient
import org.gradle.api.tasks.TaskAction

/**
 * A type of {@link GruelTask} that allows the update of JIRA issues from within
 * the gradle build system.
 */
class JiraUpdateTask extends GruelTask {

  private userName
  private password
  private jiraRootUrl
  private jql
  private fieldUpdates = new LinkedHashMap<String, Object>()

  void setUserName(String userName) {
    this.userName = userName
  }

  void setPassword(String password) {
    this.password = password
  }

  void setJiraRootUrl(String jiraRootUrl) {
    this.jiraRootUrl = jiraRootUrl
  }

  void setJql(String jql) {
    this.jql = jql
  }

  void setFieldUpdates(LinkedHashMap<String, Object> fieldUpdates) {
    this.fieldUpdates = fieldUpdates
  }

  @TaskAction
  def doTask() {
    def credentials = new BasicCredentials(userName, password)
    def client = new JiraClient(jiraRootUrl, credentials)
    def result = Issue.search(client.getRestClient(), jql)

    for (Issue issue : result.issues) {
      FluentUpdate fluentUpdate = issue.update()
      for(Map.Entry<String, Object> entry : fieldUpdates.entrySet()) {
        fluentUpdate.field(entry.getKey(), entry.getValue())
      }

      fluentUpdate.execute();
    }
  }
}
