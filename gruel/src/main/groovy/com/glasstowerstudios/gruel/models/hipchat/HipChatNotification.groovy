package com.glasstowerstudios.gruel.models.hipchat;

import java.lang.System;

/**
 * Data model for a notification to a HipChat room.
 */
public class HipChatNotification {
  def message;
  def color;
  def message_format;

  HipChatNotification(message, color, message_format) {
    this.message = message;
    this.color = color;
    this.message_format = message_format;
  }

  void setMessage(String aMessage) {
    message = aMessage;
  }

  String getMessage() {
    return message;
  }

  void setColor(String aColor) {
    color = aColor;
  }

  String getColor() {
    return color;
  }

  void setMessageFormat(String aMessageFormat) {
    message_format = aMessageFormat;
  }

  String getMessageFormat() {
    return message_format;
  }
}
