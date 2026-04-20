package net.jpkg.mtpages.vm;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateUtil {

  private TemplateUtil() {
  }

  public static String removeTagFromList(String tagsStr, String tag) {
    List<String> tags = Arrays.asList(tagsStr.split(Pattern.quote("$")));
    return tags.stream().filter(t -> !t.equals(tag)).collect(Collectors.joining("$"));
  }
}
