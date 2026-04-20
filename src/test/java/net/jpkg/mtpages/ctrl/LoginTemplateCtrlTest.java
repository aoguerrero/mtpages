package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LoginTemplateCtrlTest {

  @Test
  void getContextReturnsTitle() {
    LoginTemplateCtrl ctrl = new LoginTemplateCtrl();
    HttpRequest request = mock(HttpRequest.class);

    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("Login", ctx.get("page_title"));
  }
}
