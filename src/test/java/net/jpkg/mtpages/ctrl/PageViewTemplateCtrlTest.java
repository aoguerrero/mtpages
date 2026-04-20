package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import net.jpkg.mvcly.excp.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PageViewTemplateCtrlTest {

  @TempDir
  Path tempDir;

  private PageViewTemplateCtrl ctrl;

  @BeforeEach
  void setUp() {
    System.setProperty("pages_path", tempDir.toString());
    ctrl = new PageViewTemplateCtrl();
  }

  private HttpRequest authRequest(boolean authenticated, String uri) {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn(uri);
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
  void viewPublicPage() throws Exception {
    Files.writeString(tempDir.resolve("testpage"), "Test Title\njava web\npublic\n# Hello");

    HttpRequest request = authRequest(false, "/pages/testpage/view");
    Map<String, Object> ctx = ctrl.getContext(request);

    assertEquals("Test Title", ctx.get("page_title"));
    assertEquals("Test Title", ctx.get("title"));
    assertEquals("java web", ctx.get("tags"));
    assertTrue((boolean) ctx.get("public"));
    assertFalse((boolean) ctx.get("auth"));
    assertTrue(((String) ctx.get("content")).contains("<h1>Hello</h1>"));
  }

  @Test
  void viewPrivatePageUnauthenticatedThrows() throws Exception {
    Files.writeString(tempDir.resolve("secret"), "Secret\nfoo\nprivate\nHidden");

    HttpRequest request = authRequest(false, "/pages/secret/view");
    assertThrows(ServiceException.Unauthorized.class, () -> ctrl.getContext(request));
  }

  @Test
  void viewPrivatePageAuthenticated() throws Exception {
    Files.writeString(tempDir.resolve("secret"), "Secret\nfoo\nprivate\nHidden");

    HttpRequest request = authRequest(true, "/pages/secret/view");
    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("Secret", ctx.get("title"));
    assertTrue((boolean) ctx.get("auth"));
  }

  @Test
  void editPageUnauthenticatedThrows() throws Exception {
    Files.writeString(tempDir.resolve("testpage"), "Title\ntag\npublic\nContent");

    HttpRequest request = authRequest(false, "/pages/testpage/edit");
    assertThrows(ServiceException.Unauthorized.class, () -> ctrl.getContext(request));
  }

  @Test
  void editPageAuthenticatedReturnsRawContent() throws Exception {
    Files.writeString(tempDir.resolve("testpage"), "Title\ntag\npublic\n**bold**");

    HttpRequest request = authRequest(true, "/pages/testpage/edit");
    Map<String, Object> ctx = ctrl.getContext(request);
    // Edit mode returns raw markdown, not rendered HTML
    // Edit mode returns raw markdown (with trailing newline from file split)
    String content = (String) ctx.get("content");
    assertTrue(content.contains("**bold**"));
    assertEquals("testpage", ctx.get("id"));
    assertEquals("testpage", ctx.get("identifier"));
  }

  @Test
  void viewNonexistentPageThrows() {
    HttpRequest request = authRequest(false, "/pages/nonexistent/view");
    assertThrows(Exception.class, () -> ctrl.getContext(request));
  }

  @Test
  void viewPageWithNoContent() throws Exception {
    Files.writeString(tempDir.resolve("empty"), "Empty\n\npublic\n");

    HttpRequest request = authRequest(false, "/pages/empty/view");
    Map<String, Object> ctx = ctrl.getContext(request);
    assertEquals("", ctx.get("content"));
  }

  @Test
  void viewPageWithMultilineContent() throws Exception {
    Files.writeString(tempDir.resolve("multi"), "Multi\ntag\npublic\nLine 1\nLine 2\nLine 3");

    HttpRequest request = authRequest(false, "/pages/multi/view");
    Map<String, Object> ctx = ctrl.getContext(request);
    String content = (String) ctx.get("content");
    assertTrue(content.contains("Line 1"));
    assertTrue(content.contains("Line 2"));
    assertTrue(content.contains("Line 3"));
  }
}
