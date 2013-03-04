package org.phyous.babelhose;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.phyous.babelhose.processor.TweetProcessor;

public class Main {
  private static final String LANGUAGE = "language";
  private static final String QPS = "qps";
  private static final String MODE = "mode";
  private static final String FILTER_LIST = "filterList";
  private static final String NUM_TWEETS = "numTweets";
  private static final String HELP = "help";

  private static Options options;
  private static Map<String, String> supportedLangauges;

  static {
    options = new Options();
    options.addOption("l", LANGUAGE, true, "Language to translate to. Defaults to 'en'. " +
        "See here for supported list: http://msdn.microsoft.com/en-us/library/hh456380.aspx");
    options.addOption("q", QPS, true, "Queries per second. Defaults to 2.0.");
    options.addOption("m", MODE, true, "Processing mode for tweets. Defaults to 'hose'.");
    options.addOption("n", NUM_TWEETS, true, "The number of tweets to process before exiting. Defaults to 1000.");
    options.addOption("f", FILTER_LIST, true, "Comma delimited list of strings to filter tweets by. Defaults to null.");
    options.addOption("h", HELP, false, "Display help.");

    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(
        Resources.getResource(Main.class, "supportedLanguages.properties"));
    Properties properties = new Properties();
    try {
      properties.load(inputSupplier.getInput());
    } catch (IOException e) {
      System.out.println("Critical error loading supported translations file.");
      System.exit(-1);
    }
    supportedLangauges = Maps.fromProperties(properties);
  }

  public static void main(String[] args) {
    // Parse Command line arguments
    CommandLine cmd = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    checkCredentials();
    if (cmd.hasOption(HELP)) {
      printHelpAndBail();
    }

    String langFilter = parseLanguage(cmd.getOptionValue(LANGUAGE));
    List<String> filterList = parseFilterList(cmd.getOptionValue(FILTER_LIST));
    float qps = parseQps(cmd.getOptionValue(QPS));
    int numTweets = parseNumTweets(cmd.getOptionValue(NUM_TWEETS));
    TweetProcessor.Mode mode = parseMode(cmd.getOptionValue(MODE));

    // Every single line of code in this file gets us to this point so we can run these 2 lines of code
    TweetProcessor tp = new TweetProcessor(langFilter, filterList, qps, numTweets, mode);
    tp.run();
  }

  private static void printHelpAndBail() {
    (new HelpFormatter()).printHelp("babel-hose", options);
    System.exit(0);
  }

  private static String parseLanguage(String languageOpt) {
    String ret = "en";
    if (languageOpt == null) {
      // Use default
    } else if (supportedLangauges.keySet().contains(languageOpt)) {
      ret = languageOpt;
    } else {
      System.out.println("Invalid langauge parameter: " + languageOpt);
      System.out.println("Acceptable languages are: " + Joiner.on(",").join(supportedLangauges.keySet()));
      printHelpAndBail();
    }
    return ret;
  }

  private static List<String> parseFilterList(String filterListOpt) {
    List<String> ret = Lists.newArrayList();
    if (filterListOpt == null) {
      // Use default
    } else {
      ret = Lists.newArrayList(filterListOpt.split(","));
    }
    return ret;
  }

  private static float parseQps(String qpsOpt) {
    float ret = 2;
    if (qpsOpt == null) {
      // Use default
    } else {
      ret = Float.parseFloat(qpsOpt);
      if (ret < 0) {
        System.out.println("Invalid " + QPS + " parameter: " + qpsOpt);
        printHelpAndBail();
      }
    }
    return ret;
  }

  private static int parseNumTweets(String numTweetsOpt) {
    int ret = 1000;
    if (numTweetsOpt == null) {
      // Use default
    } else {
      ret = Integer.parseInt(numTweetsOpt);
      if (ret < 0) {
        System.out.println("Invalid " + NUM_TWEETS + " parameter: " + numTweetsOpt);
        printHelpAndBail();
      }
    }
    return ret;
  }

  private static TweetProcessor.Mode parseMode(String modeOpt) {
    TweetProcessor.Mode ret = TweetProcessor.Mode.HOSE;
    if(modeOpt == null) {
      // Use default
    } else if(modeOpt.equals("entity")) {
      ret = TweetProcessor.Mode.ENTITY;
    } else if(modeOpt.equals("hose")) {
      ret = TweetProcessor.Mode.HOSE;
    }  else {
      System.out.println("Invalid " + MODE + " parameter: " + modeOpt);
      printHelpAndBail();
    }

    return ret;
  }

  private static void checkCredentials(){
    if(Strings.isNullOrEmpty(Settings.getKey(Settings.Client.BING, "clientId")) ||
        Strings.isNullOrEmpty(Settings.getKey(Settings.Client.TWITTER, "consumerKey")) ) {
      System.out.println("Did you forget to add your developer credentials to settings.json? " +
          "Find out more by reading the README.md");
      printHelpAndBail();
    }
  }
}
