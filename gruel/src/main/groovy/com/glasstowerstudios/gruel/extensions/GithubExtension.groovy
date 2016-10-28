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
    GitHub gh;
    if (getAuthToken()) {
      gh = GitHub.connectUsingOAuth(getAuthToken())
    } else if (getUsername() && getPassword()) {
      gh = GitHub.connectUsingPassword(getUsername(), getPassword())
    }

    return gh;
  }

  GHRepository connectToRepository() {
    GitHub gh = connect()
    return gh.getRepository(getRepositoryName())
  }

  boolean isValid() {
    return repositoryName != null && !repositoryName.isEmpty() && ((auth_token != null && !auth_token.isEmpty()) || (username != null && !username.isEmpty() && password != null && !password.isEmpty()));
  }

  String getUsername() {
    if (!isValid()) {
      throw new Exception("You must provide a repository name, and either and auth_token or username/password combination to use the github extension")
    }

    return username;
  }

  String getPassword() {
    if (!isValid()) {
      throw new Exception("You must provide a repository name, and either and auth_token or username/password combination to use the github extension")
    }

    return password;
  }

  String getRepositoryName() {
    if (!isValid()) {
      throw new Exception("You must provide a repository name, and either and auth_token or username/password combination to use the github extension")
    }

    return repositoryName;
  }

  String getAuthToken() {
    if (!isValid()) {
      throw new Exception("You must provide a repository name, and either and auth_token or username/password combination to use the github extension")
    }

    return auth_token;
  }

  void setAuthToken(String auth_token) {
    this.auth_token = auth_token;
  }

  void setUsername(String username) {
    this.username = username;
  }

  void setPassword(String password) {
    this.password = password;
  }

  void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }
}
