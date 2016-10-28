package com.glasstowerstudios.gruel.tasks.jira

import org.gradle.api.tasks.TaskAction
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.Issue
import com.glasstowerstudios.gruel.tasks.GruelTask

/**
 * A general implementation of {@link GruelTask} that provides functionality
 * necessary for any Jira integration tasks.
 *
 * This class can't be used directly without an implementation of the task
 * action; It's an abstract class.
 */
class JiraTask extends GruelTask {
  String getUsername() {
    if (project.jira.username == null) {
      throw new Exception("You must specify a username prior to using the Jira extension")
    }

    return project.jira.username
  }

  String getPassword() {
    if (project.jira.password == null) {
      throw new Exception("You must specify a password prior to using the Jira extension")
    }

    return project.jira.password
  }

  JiraClient getClient() {
    return new JiraClient(getRootUrl(), getCredentials())
  }

  BasicCredentials getCredentials() {
    return new BasicCredentials(getUsername(), getPassword())
  }

  String getRootUrl() {
    if (project.jira.rootUrl == null) {
      throw new Exception("You must specify a root url prior to using the Jira extension")
    }

    return project.jira.rootUrl
  }

  /**
   * Search for Jira issues conforming to a specified Jira Query Language (JQL)
   * string.
   *
   * @param jql The JQL string to use to query the Jira system for issues
   */
  def searchForIssues(String jql) {
    return Issue.search(getClient().getRestClient(), jql)
  }
}
