package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PageDeleteConfirmationTemplateCtrlTest {

  @Test
  void getContextExtractsId() {
    PageDeleteConfirmationTemplateCtrl ctrl = new PageDeleteConfirmationTemplateCtrl();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/mypage/delete/confirmation");

    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("Delete Confirmation", ctx.get("page_title"));
    assertEquals("mypage", ctx.get("id"));
  }

  @Test
  void getContextNoMatch() {
    PageDeleteConfirmationTemplateCtrl ctrl = new PageDeleteConfirmationTemplateCtrl();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/other");

    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("Delete Confirmation", ctx.get("page_title"));
    assertNull(ctx.get("id"));
  }
}
