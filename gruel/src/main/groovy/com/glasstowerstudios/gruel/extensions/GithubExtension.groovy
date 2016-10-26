package com.glasstowerstudios.gruel.extensions;

import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRepository

/**
 * Plugin extension file. This allows specific parameters to be defined in the
 * plugin itself. (Think of it like mini-plugins within the Gruel plugin).
 *
 * For the Github extension, the repository must always be provided, along with
 * either a username/password combination, or an auth token.
 */
class GithubExtension {
  String auth_token;
  String username;
  String password;
  String repositoryName;

  GitHub connect() {
    // We can safely assume that, by now, the github extension is valid. It's
    // checked in the gruel plugin initialization code.
    GitHub gh;
    if (auth_token) {
      gh = GitHub.connectUsingOAuth(auth_token)
    } else if (username && password) {
      gh = GitHub.connectUsingPassword(username, password)
    }

    return gh;
  }

  GHRepository connectToRepository() {
    GitHub gh = connect()
    return gh.getRepository(repositoryName)
  }

  boolean isValid() {
    return repositoryName != null && !repositoryName.isEmpty() && ((auth_token != null && !auth_token.isEmpty()) || (username != null && !username.isEmpty() && password != null && !password.isEmpty()));
  }

  String getUsername() {
    return username;
  }

  String getPassword() {
    return password;
  }

  String getRepositoryName() {
    return repositoryName;
  }

  String getAuthToken() {
    return auth_token;
  }

  void setAuthToken(String aToken) {
    auth_token = aToken;
  }

  void setUsername(String aUsername) {
    username = aUsername;
  }

  void setPassword(String aPassword) {
    password = aPassword;
  }

  void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }
}
