package com.glasstowerstudios.gruel.extensions;

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
  String repo;

  boolean isValid() {
    return repo != null && !repo.isEmpty() && ((auth_token != null && !auth_token.isEmpty()) || (username != null && !username.isEmpty() && password != null && !password.isEmpty()));
  }

  String getUsername() {
    return username;
  }

  String getPassword() {
    return password;
  }

  String getRepo() {
    return repo;
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

  void setRepo(String aRepo) {
    repo = aRepo;
  }
}
