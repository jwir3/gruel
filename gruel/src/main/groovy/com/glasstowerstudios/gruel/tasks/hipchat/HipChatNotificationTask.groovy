package com.glasstowerstudios.gruel.tasks.hipchat

import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskInputs

import java.lang.System;
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import com.glasstowerstudios.gruel.tasks.GruelTask
import com.glasstowerstudios.gruel.api.HipChatRestAdapter
import com.glasstowerstudios.gruel.models.hipchat.HipChatNotification

/**
 * A {@link GruelTask} which notifies HipChat on a specific channel with a given
 * message.
 */
class HipChatNotificationTask extends GruelTask {

  private String message;
  private String channelName;
  private String color;

  void setMessage(String aMessage) {
    message = aMessage;
  }

  String getMessage() {
    message == null ? "" : message;
  }

  void setChannelName(String aChannelName) {
    channelName = aChannelName;
  }

  String getChannelName() {
    channelName == null ? "" : channelName;
  }

  void setColor(String aColor) {
    this.color = aColor;
  }

  String getColor() {
    return this.color;
  }

  @TaskAction
  def doTask() {
    def cdl = new CountDownLatch(1);
    def hipchatNotification = new HipChatNotification(message, this.color, "html");
    def hcApi = HipChatRestAdapter.getApi();
    hcApi.postNotification('Bearer ' + project.hipchat.auth_token, channelName, hipchatNotification)
          .subscribe({ arg -> cdl.countDown()});
    cdl.await(30, TimeUnit.SECONDS);
  }
}
