package net.jpkg.mtpages.auth;

import static net.jpkg.mtpages.core.AppParameters.SESSION_ID;

import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import net.jpkg.mvcly.utl.HttpUtils;

public class AuthValidator {

  private AuthValidator() {
  }

  public static boolean isAuthenticated(HttpRequest request) {
    String cookiesStr = request.headers().get("Cookie");
    Map<String, String> cookies = HttpUtils.cookiesToMap(cookiesStr);
    String sessionId = cookies.get("sessionId");
    return sessionId != null && sessionId.equals(SESSION_ID.get());
  }
}
