package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.excp.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PageSaveFormCtrlTest {

  @TempDir
  Path tempDir;

  private PageSaveFormCtrl ctrl;
  private List<Page> pages;

  @BeforeEach
  void setUp() {
    System.setProperty("pages_path", tempDir.toString());
    String sessionId = UUID.randomUUID().toString();
    System.setProperty("session_id", sessionId);

    ctrl = new PageSaveFormCtrl();
    pages = new ArrayList<>();
    ctrl.setPages(pages);
  }

  private HttpHeaders authHeaders() {
    var headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    headers.set("Cookie", "sessionId=" + System.getProperty("session_id"));
    return headers;
  }

  private HttpRequest mockRequest(String uri) {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn(uri);
    when(request.headers()).thenReturn(authHeaders());
    return request;
  }

  @Test
  void unauthenticatedThrows() {
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/save");
    when(request.headers()).thenReturn(headers);

    assertThrows(ServiceException.Unauthorized.class,
        () -> ctrl.execute(headers, request, Map.of()));
  }

  @Test
  void saveNewPage() {
    ctrl.setPath("/pages/save");
    ctrl.setTarget("/pages/{id}/view");
    HttpRequest request = mockRequest("/pages/save");

    Map<String, String> formData = new HashMap<>();
    formData.put("title", "My New Page");
    formData.put("tags", "java web");
    formData.put("content", "Hello world");
    formData.put("public", "true");
    formData.put("identifier", "my_new_page");

    ctrl.execute(authHeaders(), request, formData);

    assertTrue(Files.exists(tempDir.resolve("my_new_page")));
    assertEquals(1, pages.size());
    assertEquals("my_new_page", pages.get(0).id());
  }

  @Test
  void saveExistingPage() throws Exception {
    Files.writeString(tempDir.resolve("existing"), "Old Title\nold\npublic\nOld content");
    pages.add(new Page("existing", "Old Title", List.of("old"), true));

    ctrl.setPath("/pages/(.*)/save");
    ctrl.setTarget("/pages/{id}/view");
    HttpRequest request = mockRequest("/pages/existing/save");

    Map<String, String> formData = new HashMap<>();
    formData.put("title", "Updated Title");
    formData.put("tags", "new tags");
    formData.put("content", "New content");
    formData.put("public", "true");
    formData.put("identifier", "existing");

    ctrl.execute(authHeaders(), request, formData);

    String content = Files.readString(tempDir.resolve("existing"));
    assertTrue(content.startsWith("Updated Title"));
    assertEquals(1, pages.size());
    assertEquals("Updated Title", pages.get(0).title());
  }

  @Test
  void savePageAsPrivate() {
    ctrl.setPath("/pages/save");
    ctrl.setTarget("/pages/{id}/view");
    HttpRequest request = mockRequest("/pages/save");

    Map<String, String> formData = new HashMap<>();
    formData.put("title", "Private Page");
    formData.put("tags", "secret");
    formData.put("content", "Hidden");
    // No "public" key -> private
    formData.put("identifier", "private_page");

    ctrl.execute(authHeaders(), request, formData);

    String content = assertDoesNotThrow(() -> Files.readString(tempDir.resolve("private_page")));
    assertTrue(content.contains("private"));
    assertFalse(pages.get(0).pblic());
  }

  @Test
  void normalizeSpacesInTags() {
    ctrl.setPath("/pages/save");
    ctrl.setTarget("/pages/{id}/view");
    HttpRequest request = mockRequest("/pages/save");

    Map<String, String> formData = new HashMap<>();
    formData.put("title", "Spaced Tags");
    formData.put("tags", "tag1   tag2   tag3");
    formData.put("content", "Content");
    formData.put("identifier", "spaced");

    ctrl.execute(authHeaders(), request, formData);

    String content = assertDoesNotThrow(() -> Files.readString(tempDir.resolve("spaced")));
    assertTrue(content.contains("tag1 tag2 tag3"));
  }

  @Test
  void renamePage() throws Exception {
    Files.writeString(tempDir.resolve("old_id"), "Title\ntag\npublic\nContent");
    pages.add(new Page("old_id", "Title", List.of("tag"), true));

    ctrl.setPath("/pages/(.*)/save");
    ctrl.setTarget("/pages/{id}/view");
    HttpRequest request = mockRequest("/pages/old_id/save");

    Map<String, String> formData = new HashMap<>();
    formData.put("title", "Title");
    formData.put("tags", "tag");
    formData.put("content", "Content");
    formData.put("public", "true");
    formData.put("identifier", "new_id");

    ctrl.execute(authHeaders(), request, formData);

    assertFalse(Files.exists(tempDir.resolve("old_id")));
    assertTrue(Files.exists(tempDir.resolve("new_id")));
    assertEquals(1, pages.size());
    assertEquals("new_id", pages.get(0).id());
  }
}
