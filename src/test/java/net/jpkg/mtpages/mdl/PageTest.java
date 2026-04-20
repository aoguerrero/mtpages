package net.jpkg.mtpages.mdl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void constructorWithIdOnly() {
    Page page = new Page("test-id");
    assertEquals("test-id", page.id());
    assertNull(page.title());
    assertNull(page.tags());
    assertFalse(page.pblic());
  }

  @Test
  void fullConstructor() {
    Page page = new Page("id", "Title", List.of("tag1", "tag2"), true);
    assertEquals("id", page.id());
    assertEquals("Title", page.title());
    assertEquals(List.of("tag1", "tag2"), page.tags());
    assertTrue(page.pblic());
  }

  @Test
  void equalsSameId() {
    Page a = new Page("id", "Title A", List.of("t1"), false);
    Page b = new Page("id", "Title B", List.of("t2"), true);
    assertEquals(a, b);
  }

  @Test
  void equalsDifferentId() {
    Page a = new Page("id1");
    Page b = new Page("id2");
    assertNotEquals(a, b);
  }

  @Test
  void equalsNull() {
    Page page = new Page("id");
    assertNotEquals(null, page);
  }

  @Test
  void equalsDifferentType() {
    Page page = new Page("id");
    assertNotEquals("id", page);
  }

  @Test
  void equalsSameInstance() {
    Page page = new Page("id");
    assertEquals(page, page);
  }
}
