package net.jpkg.mtpages.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;

public class PageDeleteConfirmationTemplateCtrl extends BaseTemplateCtrl {

  private static final Pattern DELETE_CONFIRM_PATTERN = Pattern.compile("/pages/(.*)/delete/confirmation");

  @Override
  public Map<String, Object> getContext(HttpRequest request) {
    Map<String, Object> data = new HashMap<>();
    data.put("page_title", "Delete Confirmation");
    Matcher matcher = DELETE_CONFIRM_PATTERN.matcher(request.uri());
    if (matcher.find()) {
      data.put("id", matcher.group(1));
    }
    return data;
  }

}
