package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PageNewTemplateCtrlTest {

  private PageNewTemplateCtrl ctrl;

  @BeforeEach
  void setUp() {
    ctrl = new PageNewTemplateCtrl();
    ctrl.setPath("/pages/new/(.*)");
  }

  @Test
  void getContextWithTags() {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/new/java$web");

    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("New Page", ctx.get("page_title"));
    assertEquals("java$web", ctx.get("selected_tags_str"));
    assertEquals("java web", ctx.get("selected_tags_str_sp"));
  }

  @Test
  void getContextNoTags() {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/new/");

    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("New Page", ctx.get("page_title"));
    assertEquals("", ctx.get("selected_tags_str"));
    assertEquals("", ctx.get("selected_tags_str_sp"));
  }
}
