package net.jpkg.mtpages.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jpkg.mtpages.auth.AuthValidator;
import net.jpkg.mtpages.core.AppParameters;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mtpages.vm.TemplateUtil;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;

public class PageListTemplateCtrl extends BaseTemplateCtrl {

  private static final Pattern LIST_PATTERN = Pattern.compile("/pages/list/(.+)");

  private List<Page> pages;

  public Map<String, Object> getContext(HttpRequest request) {
    boolean auth = AuthValidator.isAuthenticated(request);

    List<String> selectedTags;
    Matcher matcher = LIST_PATTERN.matcher(request.uri());

    boolean filtered = matcher.find();
    List<Page> unsortedPages;
    if (filtered) {
      selectedTags = Arrays.asList(matcher.group(1).split(Pattern.quote("$")));
      unsortedPages = TagUtil.filterPages(selectedTags, pages).stream()
          .filter(p -> p.pblic() || auth)
          .toList();
    } else {
      selectedTags = Collections.emptyList();
      unsortedPages = pages.stream().filter(p -> p.pblic() || auth).toList();
    }

    List<String> unselectedTags = TagUtil.getUnselectedTags(selectedTags, unsortedPages);
        

    if (!filtered && !auth) {
      unsortedPages = Collections.emptyList();
    }

    Map<String, Object> data = new HashMap<>();
    data.put("page_title", AppParameters.WEBSITE.get());
    data.put("auth", auth);
    data.put("selected_tags_str", String.join("$", selectedTags));
    data.put("selected_tags", selectedTags);
    data.put("tags", unselectedTags);
    List<Page> sortedPages = new ArrayList<>(unsortedPages);
    sortedPages.sort(Comparator.comparing((Page p) -> p.title().toLowerCase()));
    data.put("items", sortedPages);
    data.put("template_util", TemplateUtil.class);
    data.put("filtered", filtered);

    return data;
  }

  public void setPages(List<Page> pages) {
    this.pages = pages;
  }
}
