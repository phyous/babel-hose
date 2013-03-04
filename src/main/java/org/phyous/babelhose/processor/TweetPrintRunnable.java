package org.phyous.babelhose.processor;

import com.google.common.base.Strings;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.phyous.babelhose.twitter.TwitterStatus;

public class TweetPrintRunnable implements Runnable {
  private TwitterStatus status;
  private String langFilter;

  public TweetPrintRunnable(TwitterStatus status, String langFilter) {
    this.status = status;
    this.langFilter = langFilter;
  }

  @Override
  public void run() {
    if (status.isValid() && !status.getUserLang().equals(langFilter)) {
      try {
        String translation = Translate.execute(status.getText(), Language.fromString(langFilter));
        if (!translation.equals(status.getText())) {
          printTranslation(status, langFilter, translation);
        }
      } catch (Exception e) {
        // Swallow exception. Since we're processing many messages and bing will fail from time to
        // time, we can move on.
      }

    }
  }

  private static void printTranslation(TwitterStatus status, String destLang, String translation) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("--- @%s : %s ---", status.getUserScreenName(), status.getTweetCreatedAt()));
    if (!Strings.isNullOrEmpty(status.getUserLocation())) {
      sb.append(String.format("> [Location: %s]", status.getUserLocation()));
    }
    sb.append(String.format("\n[%s] %s =>\n[%s] %s\n",
        status.getUserLang(), status.getText(),
        destLang, translation));

    System.out.println(sb.toString());
  }
}
