package com.glasstowerstudios.gruel.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * One-stop configuration place for {@link Gson} objects.
 *
 * If you need a {@link Gson} object, use {@link #getGson}, rather than constructing it yourself.
 * This ensures consistency of configuration across all users of Gson-based parsing. If, on the
 * other hand, you need a specifically-configured {@link Gson} object, consider creating a new
 * method in this class to provide it, so that it can be reused.
 */
public class GruelGsonHelper {

  public static Gson getGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    return gson;
  }
}
