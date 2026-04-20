package net.jpkg.mtpages.ctrl;

import java.util.List;

import net.jpkg.mtpages.mdl.Page;

public class TagUtil {

  public static List<Page> filterPages(List<String> tags, List<Page> pages) {
    return pages.stream().filter(p -> p.tags().containsAll(tags)).toList();
  }

  public static List<String> getTags(boolean auth, List<Page> pages) {
    return pages.stream().filter(p -> p.pblic() || auth).flatMap(p -> p.tags().stream()).distinct().sorted().toList();
  }

  public static List<String> getUnselectedTags(List<String> selectedTags, List<Page> pages) {
    List<String> alltags = pages.stream().flatMap(p -> p.tags().stream()).distinct().sorted().filter(t -> !selectedTags.contains(t))
        .toList();
    return alltags;
  }
}
