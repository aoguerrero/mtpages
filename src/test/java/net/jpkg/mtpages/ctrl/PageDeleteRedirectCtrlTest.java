package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.excp.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PageDeleteRedirectCtrlTest {

  @TempDir
  Path tempDir;

  private PageDeleteRedirectCtrl ctrl;
  private List<Page> pages;

  @BeforeEach
  void setUp() {
    System.setProperty("pages_path", tempDir.toString());
    String sessionId = UUID.randomUUID().toString();
    System.setProperty("session_id", sessionId);

    ctrl = new PageDeleteRedirectCtrl();
    pages = new ArrayList<>();
    ctrl.setPages(pages);
  }

  private HttpHeaders authHeaders() {
    var headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    headers.set("Cookie", "sessionId=" + System.getProperty("session_id"));
    return headers;
  }

  @Test
  void unauthenticatedThrows() {
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/test/delete");
    when(request.headers()).thenReturn(headers);

    assertThrows(ServiceException.Unauthorized.class,
        () -> ctrl.execute(headers, request));
  }

  @Test
  void deletePageMovesToTrash() throws Exception {
    Files.writeString(tempDir.resolve("mypage"), "Title\ntag\npublic\nContent");
    pages.add(new Page("mypage", "Title", List.of("tag"), true));

    ctrl.setPath("/pages/(.*)/delete");
    HttpHeaders headers = authHeaders();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/mypage/delete");
    when(request.headers()).thenReturn(headers);

    ctrl.execute(headers, request);

    assertFalse(Files.exists(tempDir.resolve("mypage")));
    assertTrue(Files.exists(tempDir.resolve("trash/mypage")));
    assertTrue(pages.isEmpty());
  }

  @Test
  void deleteCreatesTrashDir() throws Exception {
    Files.writeString(tempDir.resolve("page1"), "Title\ntag\npublic\nContent");
    pages.add(new Page("page1", "Title", List.of("tag"), true));

    ctrl.setPath("/pages/(.*)/delete");
    HttpHeaders headers = authHeaders();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/page1/delete");
    when(request.headers()).thenReturn(headers);

    ctrl.execute(headers, request);

    assertTrue(Files.isDirectory(tempDir.resolve("trash")));
  }

  @Test
  void deleteDuplicateIdInTrashAppendsTimestamp() throws Exception {
    Files.writeString(tempDir.resolve("dup"), "Title\ntag\npublic\nContent");
    pages.add(new Page("dup", "Title", List.of("tag"), true));

    // Create existing trash entry
    Files.createDirectories(tempDir.resolve("trash"));
    Files.writeString(tempDir.resolve("trash/dup"), "Old deleted content");

    ctrl.setPath("/pages/(.*)/delete");
    HttpHeaders headers = authHeaders();
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/dup/delete");
    when(request.headers()).thenReturn(headers);

    ctrl.execute(headers, request);

    // Original file gone
    assertFalse(Files.exists(tempDir.resolve("dup")));
    // Both old and new trash entries exist
    assertTrue(Files.exists(tempDir.resolve("trash/dup")));
    // New entry has timestamp suffix
    long trashCount = Files.list(tempDir.resolve("trash")).count();
    assertEquals(2, trashCount);
  }
}
