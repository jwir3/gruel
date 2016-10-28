package com.glasstowerstudios.gruel.extensions;

/**
 * Plugin extension file. This allows specific parameters to be defined in the
 * plugin itself. (Think of it like mini-plugins within the Gruel plugin).
 *
 * For the Jira extension, the rootUrl, username, and password must always be
 * provided.
 */
class JiraExtension {
  private String username;
  private String password;
  private String rootUrl;

  String getUsername() {
    return this.username;
  }

  void setUsername(String username) {
    this.username = username;
  }

  String getPassword() {
    return this.password;
  }

  void setPassword(String password) {
    this.password = password;
  }

  String getRootUrl() {
    return this.rootUrl;
  }

  void setRootUrl(String rootUrl) {
    this.rootUrl = rootUrl;
  }

  boolean isValid() {
    return this.rootUrl != null && !this.rootUrl.isEmpty() && this.username != null && !this.username.isEmpty() && this.password != null && !this.password.isEmpty();
  }
}
