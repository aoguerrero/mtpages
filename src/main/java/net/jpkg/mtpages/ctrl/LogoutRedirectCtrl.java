package net.jpkg.mtpages.ctrl;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mvcly.ctrl.RedirectController;

public class LogoutRedirectCtrl extends RedirectController {

  public LogoutRedirectCtrl() {
  }

  @Override
  public void execute(HttpHeaders responseheaders, HttpRequest request) {
    responseheaders.add("Set-Cookie", "sessionId=; Path=/");
  }
}
