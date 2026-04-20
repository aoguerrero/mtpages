package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.jupiter.api.Test;

class LogoutRedirectCtrlTest {

  @Test
  void executeClearsSessionCookie() {
    LogoutRedirectCtrl ctrl = new LogoutRedirectCtrl();
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);

    ctrl.execute(headers, request);
    assertEquals("sessionId=; Path=/", headers.get("Set-Cookie"));
  }
}
