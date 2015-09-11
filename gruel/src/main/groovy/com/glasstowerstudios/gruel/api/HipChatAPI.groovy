package com.glasstowerstudios.gruel.api

import retrofit.http.Body
import retrofit.http.DELETE
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.POST
import retrofit.http.PUT
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable;

import com.glasstowerstudios.gruel.models.hipchat.HipChatNotification;

/**
 * REST API to access HipChat services.
 */
public interface HipChatAPI {
  @POST('/room/{roomName}/notification')
  Observable<Void> postNotification(@Header("Authorization") String aAuthCodeString,
                                    @Path("roomName") String aRoomName,
                                    @Body HipChatNotification aNotification);
}
