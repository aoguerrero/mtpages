package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.jpkg.mtpages.mdl.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HomeTemplateCtrlTest {

  @TempDir
  Path tempDir;

  private HomeTemplateCtrl ctrl;
  private List<Page> pages;

  @BeforeEach
  void setUp() {
    System.setProperty("pages_path", tempDir.toString());
    System.setProperty("website", "TestSite");
    ctrl = new HomeTemplateCtrl();
    pages = new ArrayList<>();
    ctrl.setPages(pages);
  }

  private HttpRequest authRequest(boolean authenticated) {
    HttpRequest request = mock(HttpRequest.class);
    var headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    if (authenticated) {
      String sessionId = UUID.randomUUID().toString();
      System.setProperty("session_id", sessionId);
      headers.set("Cookie", "sessionId=" + sessionId);
    }
    when(request.headers()).thenReturn(headers);
    return request;
  }

  @Test
  void getContextWithHomePageFile() throws Exception {
    Files.writeString(tempDir.resolve("home"), "Welcome\ntag1\npublic\n# Hello World");
    pages.add(new Page("home", "Welcome", List.of("tag1"), true));

    HttpRequest request = authRequest(false);
    Map<String, Object> ctx = ctrl.getContext(request);

    assertEquals("TestSite", ctx.get("page_title"));
    assertTrue(((String) ctx.get("content")).contains("<h1>Hello World</h1>"));
    assertFalse((boolean) ctx.get("auth"));
  }

  @Test
  void getContextWithoutHomePageFallsBack() {
    HttpRequest request = authRequest(false);
    Map<String, Object> ctx = ctrl.getContext(request);

    assertEquals("TestSite", ctx.get("page_title"));
    assertTrue(((String) ctx.get("content")).contains("Create a new page"));
  }

  @Test
  void getContextAuthenticated() throws Exception {
    Files.writeString(tempDir.resolve("home"), "Home\ntag1\npublic\nContent");
    pages.add(new Page("home", "Home", List.of("tag1"), true));

    HttpRequest request = authRequest(true);
    Map<String, Object> ctx = ctrl.getContext(request);

    assertTrue((boolean) ctx.get("auth"));
    @SuppressWarnings("unchecked")
    List<String> tags = (List<String>) ctx.get("tags");
    assertFalse(tags.isEmpty());
  }
}
