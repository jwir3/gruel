package com.glasstowerstudios.gruel.extensions;

/**
 * Plugin extension file. This allows specific parameters to be defined in the
 * plugin itself. (Think of it like mini-plugins within the Gruel plugin).
 *
 * For the HipChat extension, the token must always be provided.
 */
class HipChatExtension {

    public HipChatExtension(auth_token) {
      this.auth_token = auth_token;
    }

    String auth_token;

    String getAuthToken() {
      return auth_token;
    }

    void setAuthToken(String aToken) {
      auth_token = aToken;
    }
}
