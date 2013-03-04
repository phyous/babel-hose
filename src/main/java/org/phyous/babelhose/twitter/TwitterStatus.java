package org.phyous.babelhose.twitter;

import twitter4j.internal.org.json.JSONObject;

/**
 * A very simple object wrapping a status response
 */
public class TwitterStatus {
  private boolean isValid;
  private String userLang;
  private String userScreenName;
  private String userLocation;
  private String tweetCreatedAt;
  private String text;

  public TwitterStatus(String json) {
    try {
      JSONObject obj = new JSONObject(json);
      if(obj.has("delete")) {
        isValid = false;
      } else {
        isValid = true;
        text = obj.getString("text");
        userLang = obj.getJSONObject("user").getString("lang");
        userScreenName = obj.getJSONObject("user").getString("screen_name");
        userLocation = obj.getJSONObject("user").getString("location");
        tweetCreatedAt = obj.getString("created_at");
      }
    } catch (Exception e) {
      isValid = false;
    }
  }

  public boolean isValid() {
    return isValid;
  }

  public String getUserScreenName(){
    return userScreenName;
  }

  public String getUserLang() {
    return userLang;
  }

  public String getUserLocation() {
    return userLocation;
  }

  public String getText() {
    return text;
  }

  public String getTweetCreatedAt(){
    return tweetCreatedAt;
  }
}
