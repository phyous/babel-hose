package org.phyous.babelhose.processor;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.json.simple.JSONArray;
import org.phyous.babelhose.twitter.TwitterStatus;

import com.twitter.Extractor;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityAnalysisRunnable implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(EntityAnalysisRunnable.class);
  private TwitterStatus status;

  public EntityAnalysisRunnable(TwitterStatus status) {
    this.status = status;
  }

  @Override
  public void run() {
    if (status.isValid()) {
      try {
        Extractor extractor = new Extractor();
        List<Extractor.Entity> originalEntities = extractor.extractEntitiesWithIndices(status.getText());
        if (originalEntities.size() > 0) {
          // Pick a random destination langauge for translation
          int langIndex = (new Random()).nextInt(Language.values().length);
          Language destLang = Language.values()[langIndex];

          // Get translation from bing
          String translation = Translate.execute(status.getText(), destLang);

          // Extract entities from translation and compare with original
          List<Extractor.Entity> translationEntities = extractor.extractEntitiesWithIndices(translation);
          if (!compareEntitySets(originalEntities,translationEntities)) {
            System.out.println(entityComparisonString(
                status.getText(), originalEntities, status.getUserLang(),
                translation, translationEntities, destLang.toString()));
          }
        }

      } catch (Exception e) {
        // Swallow exception. Since we're processing many messages and bing will fail from time to
        // time, we can move on.
      }

    }
  }

  private static boolean compareEntitySets(List<Extractor.Entity> original,
                                           List<Extractor.Entity> translated) {
    Set<String> originalEntityStrings = EntityListToSet(original);
    Set<String> translatedEntityStrings = EntityListToSet(translated);
    return originalEntityStrings.containsAll(translatedEntityStrings);
  }

  private static Set<String> EntityListToSet(List<Extractor.Entity> original) {
    Set<String> stringSet = Sets.newHashSet();
    for(Extractor.Entity e: original)
      stringSet.add(String.format("%s%s", e.getType(), e.getValue()));
    return stringSet;
  }

  private static String entityComparisonString(String originalText,
                                               List<Extractor.Entity> originalEntities,
                                               String originalLang,
                                               String translatedText,
                                               List<Extractor.Entity> translatedEntities,
                                               String translatedLang) {
    JSONObject original = new JSONObject();
    original.put("text", originalText);
    original.put("entities", entityListToJSON(originalEntities));
    original.put("lang", originalLang);
    JSONObject translated = new JSONObject();
    translated.put("text", translatedText);
    translated.put("entities", entityListToJSON(translatedEntities));
    translated.put("lang", translatedLang);

    JSONObject result = new JSONObject();
    result.put("original", original);
    result.put("translated", translated);
    return result.toJSONString();
  }

  private static JSONArray entityListToJSON(List<Extractor.Entity> entityList) {
    JSONArray array = new JSONArray();
    for (Extractor.Entity e : entityList) {
      JSONObject obj = new JSONObject();
      obj.put("type", e.getType().toString());
      obj.put("value", e.getValue());
      array.add(obj);
    }
    return array;
  }
}