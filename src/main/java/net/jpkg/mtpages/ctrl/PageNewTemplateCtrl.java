package net.jpkg.mtpages.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;

public class PageNewTemplateCtrl extends BaseTemplateCtrl {

  public Map<String, Object> getContext(HttpRequest request) {

    String sourcePath = getPath();
    Matcher matcher = Pattern.compile(sourcePath).matcher(request.uri());

    String selectedTagsStr = "";
    String selectedTagsStrSp = "";
    if (matcher.find() && matcher.groupCount() > 0) {
      selectedTagsStr = matcher.group(1);
      selectedTagsStrSp = selectedTagsStr.replace("$", " ");
    }

    Map<String, Object> data = new HashMap<>();
    data.put("page_title", "New Page");
    data.put("selected_tags_str", selectedTagsStr);
    data.put("selected_tags_str_sp", selectedTagsStrSp);
    return data;
  }
}
