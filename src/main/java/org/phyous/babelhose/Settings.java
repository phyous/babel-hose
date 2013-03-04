package org.phyous.babelhose;

import java.nio.charset.Charset;

import com.google.common.io.Resources;

import twitter4j.internal.org.json.JSONObject;

public class Settings {
  static JSONObject permissionsMap;
  static {
    try {
      String resourceJson = Resources.toString(
          Resources.getResource(Settings.class, "settings.json"),
          Charset.forName("UTF-8"));
      permissionsMap = new JSONObject(resourceJson);
    } catch (Exception e) {
      System.out.println(String.format("Error loading resource settings.json.\n%s", e.toString()));
    }
  }

  public static String getKey(String clientKey, String key){
    String val = "";
    try {
      val = permissionsMap
          .getJSONObject("settings")
          .getJSONObject(clientKey)
          .getString(key);
    } catch (Exception e) {
      System.out.println(String.format("Error reading key %s:%s\n%s", clientKey, key, e.toString()));
    }
    return val;
  }

  public class Client {
    public static final String TWITTER = "twitter";
    public static final String BING = "bing-translator";
  }

}
