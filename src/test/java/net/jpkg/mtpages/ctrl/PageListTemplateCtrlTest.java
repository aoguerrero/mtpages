package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mtpages.vm.TemplateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PageListTemplateCtrlTest {

  private PageListTemplateCtrl ctrl;
  private List<Page> pages;

  @BeforeEach
  void setUp() {
    ctrl = new PageListTemplateCtrl();
    pages = new ArrayList<>();
    pages.add(new Page("p1", "Alpha", List.of("java", "web"), true));
    pages.add(new Page("p2", "Beta", List.of("java", "security"), false));
    pages.add(new Page("p3", "Gamma", List.of("docker"), true));
    ctrl.setPages(pages);

    // Mock AuthValidator to return false (unauthenticated) by default
    // We use a mock request; AuthValidator checks cookies directly
  }

  @Test
  void getContextUnauthenticatedNoFilter() {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/list");
    when(request.headers()).thenReturn(new io.netty.handler.codec.http.DefaultHttpHeaders());

    Map<String, Object> ctx = ctrl.getContext(request);
    assertFalse((boolean) ctx.get("auth"));
    assertFalse((boolean) ctx.get("filtered"));
    // Unauthenticated + no filter = empty items
    @SuppressWarnings("unchecked")
    List<Page> items = (List<Page>) ctx.get("items");
    assertTrue(items.isEmpty());
  }

  @Test
  void getContextWithFilterAndAuth() {
    // Set a valid session so AuthValidator returns true
    String sessionId = java.util.UUID.randomUUID().toString();
    System.setProperty("session_id", sessionId);

    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/list/java$web");
    // Add cookie header
    var headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    headers.set("Cookie", "sessionId=" + sessionId);
    when(request.headers()).thenReturn(headers);

    Map<String, Object> ctx = ctrl.getContext(request);
    assertTrue((boolean) ctx.get("auth"));
    assertTrue((boolean) ctx.get("filtered"));
    assertEquals("java$web", ctx.get("selected_tags_str"));

    @SuppressWarnings("unchecked")
    List<String> selectedTags = (List<String>) ctx.get("selected_tags");
    assertEquals(List.of("java", "web"), selectedTags);

    @SuppressWarnings("unchecked")
    List<Page> items = (List<Page>) ctx.get("items");
    assertEquals(1, items.size());
    assertEquals("Alpha", items.get(0).title());
  }

  @Test
  void getContextUnauthenticatedWithFilterShowsPublicOnly() {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/list/java");
    when(request.headers()).thenReturn(new io.netty.handler.codec.http.DefaultHttpHeaders());

    Map<String, Object> ctx = ctrl.getContext(request);
    assertFalse((boolean) ctx.get("auth"));

    @SuppressWarnings("unchecked")
    List<Page> items = (List<Page>) ctx.get("items");
    // Should only show public pages matching tag "java"
    assertEquals(1, items.size());
    assertEquals("Alpha", items.get(0).title());
  }

  @Test
  void getContextReturnsTags() {
    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/list");
    when(request.headers()).thenReturn(new io.netty.handler.codec.http.DefaultHttpHeaders());

    Map<String, Object> ctx = ctrl.getContext(request);
    assertNotNull(ctx.get("tags"));
    assertNotNull(ctx.get("template_util"));
    assertEquals(TemplateUtil.class, ctx.get("template_util"));
  }

  @Test
  void getContextAuthenticatedNoFilterShowsAll() {
    String sessionId = java.util.UUID.randomUUID().toString();
    System.setProperty("session_id", sessionId);

    HttpRequest request = mock(HttpRequest.class);
    when(request.uri()).thenReturn("/pages/list");
    var headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    headers.set("Cookie", "sessionId=" + sessionId);
    when(request.headers()).thenReturn(headers);

    Map<String, Object> ctx = ctrl.getContext(request);

    @SuppressWarnings("unchecked")
    List<Page> items = (List<Page>) ctx.get("items");
    assertEquals(3, items.size());
    // Sorted alphabetically by title
    assertEquals("Alpha", items.get(0).title());
    assertEquals("Beta", items.get(1).title());
    assertEquals("Gamma", items.get(2).title());
  }
}
