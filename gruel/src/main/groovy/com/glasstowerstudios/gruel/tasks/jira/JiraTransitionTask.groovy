package com.glasstowerstudios.gruel.tasks.jira

import com.glasstowerstudios.gruel.tasks.GruelTask
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Issue.FluentTransition
import net.rcarz.jiraclient.JiraClient
import org.gradle.api.tasks.TaskAction

class JiraTransitionTask extends GruelTask {

  private userName
  private password
  private jiraRootUrl
  private jql
  private toStatus
  private fieldUpdates

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

  void setToStatus(String toStatus) {
    this.toStatus = toStatus
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
      FluentTransition fluentTransition
      for(Map.Entry<String, Object> entry : fieldUpdates.entrySet()) {
        if (!fluentTransition) {
          fluentTransition = issue.transition()
        }
        fluentTransition.field(entry.getKey(), entry.getValue())
      }
      fluentTransition?.execute(toStatus)
    }
  }
}
