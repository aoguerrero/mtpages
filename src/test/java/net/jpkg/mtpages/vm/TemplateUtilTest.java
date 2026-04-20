package net.jpkg.mtpages.vm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TemplateUtilTest {

  @Test
  void removeTagFromListMiddle() {
    String result = TemplateUtil.removeTagFromList("java$web$docker", "web");
    assertEquals("java$docker", result);
  }

  @Test
  void removeTagFromListStart() {
    String result = TemplateUtil.removeTagFromList("java$web$docker", "java");
    assertEquals("web$docker", result);
  }

  @Test
  void removeTagFromListEnd() {
    String result = TemplateUtil.removeTagFromList("java$web$docker", "docker");
    assertEquals("java$web", result);
  }

  @Test
  void removeTagFromListSingleTag() {
    String result = TemplateUtil.removeTagFromList("java", "java");
    assertEquals("", result);
  }

  @Test
  void removeTagFromListNotPresent() {
    String result = TemplateUtil.removeTagFromList("java$web", "docker");
    assertEquals("java$web", result);
  }
}
