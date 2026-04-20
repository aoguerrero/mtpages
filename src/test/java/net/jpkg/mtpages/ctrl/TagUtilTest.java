package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import net.jpkg.mtpages.mdl.Page;
import org.junit.jupiter.api.Test;

class TagUtilTest {

  private final Page publicPage = new Page("p1", "Page One", List.of("java", "web"), true);
  private final Page privatePage = new Page("p2", "Page Two", List.of("java", "security"), false);
  private final Page anotherPublic = new Page("p3", "Page Three", List.of("web", "docker"), true);
  private final List<Page> allPages = List.of(publicPage, privatePage, anotherPublic);

  @Test
  void filterPagesSingleTag() {
    List<Page> result = TagUtil.filterPages(List.of("java"), allPages);
    assertEquals(2, result.size());
    assertTrue(result.contains(publicPage));
    assertTrue(result.contains(privatePage));
  }

  @Test
  void filterPagesMultipleTags() {
    List<Page> result = TagUtil.filterPages(List.of("java", "web"), allPages);
    assertEquals(1, result.size());
    assertEquals(publicPage, result.get(0));
  }

  @Test
  void filterPagesNoMatch() {
    List<Page> result = TagUtil.filterPages(List.of("nonexistent"), allPages);
    assertTrue(result.isEmpty());
  }

  @Test
  void filterPagesEmptyTags() {
    List<Page> result = TagUtil.filterPages(Collections.emptyList(), allPages);
    assertEquals(3, result.size());
  }

  @Test
  void getTagsAuthenticatedReturnsAll() {
    List<String> tags = TagUtil.getTags(true, allPages);
    assertEquals(List.of("docker", "java", "security", "web"), tags);
  }

  @Test
  void getTagsUnauthenticatedReturnsOnlyPublic() {
    List<String> tags = TagUtil.getTags(false, allPages);
    assertEquals(List.of("docker", "java", "web"), tags);
  }

  @Test
  void getTagsDeduplicatesAndSorts() {
    List<String> tags = TagUtil.getTags(true, allPages);
    long javaCount = tags.stream().filter(t -> t.equals("java")).count();
    assertEquals(1, javaCount);
  }

  @Test
  void getUnselectedTags() {
    List<String> result = TagUtil.getUnselectedTags(List.of("java"), allPages);
    assertEquals(List.of("docker", "security", "web"), result);
  }

  @Test
  void getUnselectedTagsAllSelected() {
    List<String> result = TagUtil.getUnselectedTags(List.of("java", "web"), allPages);
    assertEquals(List.of("docker", "security"), result);
  }

  @Test
  void getUnselectedTagsNoneSelected() {
    List<String> result = TagUtil.getUnselectedTags(Collections.emptyList(), allPages);
    assertEquals(List.of("docker", "java", "security", "web"), result);
  }
}
