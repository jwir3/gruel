package com.glasstowerstudios.gruel.api;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.RequestInterceptor;
import retrofit.client.Header;

import com.google.gson.Gson;

import com.glasstowerstudios.gruel.api.HipChatAPI;

/**
 * An adapter for a Retrofit REST interface. In this case, for the HipChat API.
 */
public class HipChatRestAdapter {

  // Note the use of the volatile keyword. This is important for double-checked locking to
  // work correctly.
  private volatile static HipChatAPI sApi;

  /**
   * Retrieve an instance of {@link HipChatAPI} for use with your application.
   *
   * @return An implementation of {@link HipChatAPI} that can be used to make
   *         calls to api.hipchat.com.
   */
  public static HipChatAPI getApi() {
    // This variable seems unnecessary, but is actually necessary for the semantics of lazy
    // initialization.
    HipChatAPI localApi = sApi;
    if (localApi == null) {
      synchronized (HipChatRestAdapter.class) {
        localApi = sApi;
        if (localApi == null) {
          Gson gson = GruelGsonHelper.getGson();
          RestAdapter.Builder builder = new RestAdapter.Builder()
          // builder.setLogLevel(RestAdapter.LogLevel.FULL);
          RestAdapter adapter =
            builder.setConverter(new GsonConverter(gson))
                  .setEndpoint("https://api.hipchat.com/v2")
                  .build();
          localApi = sApi = adapter.create(HipChatAPI.class);
        }
      }
    }

    return localApi;
  }
}
