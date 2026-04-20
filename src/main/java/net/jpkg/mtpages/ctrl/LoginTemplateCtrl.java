package net.jpkg.mtpages.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;

public class LoginTemplateCtrl extends BaseTemplateCtrl {

  public Map<String, Object> getContext(HttpRequest request) {
    Map<String, Object> data = new HashMap<>();
    data.put("page_title", "Login");
    return data;
  }
}
