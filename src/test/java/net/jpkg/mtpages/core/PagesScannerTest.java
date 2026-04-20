package net.jpkg.mtpages.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.excp.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PagesScannerTest {

  @TempDir
  Path tempDir;

  @BeforeEach
  void setPagesPath() {
    System.setProperty("pages_path", tempDir.toString());
  }

  @Test
  void scanPagesReadsFiles() throws IOException {
    Files.writeString(tempDir.resolve("mypage"), "My Title\ntag1 tag2\npublic\nSome content");
    Files.writeString(tempDir.resolve("another"), "Another\njava\nprivate\nMore content");

    List<Page> pages = PagesScanner.scanPages();
    assertEquals(2, pages.size());
  }

  @Test
  void scanPagesParsesCorrectly() throws IOException {
    Files.writeString(tempDir.resolve("mypage"), "My Title\ntag1 tag2\npublic\nSome content");

    List<Page> pages = PagesScanner.scanPages();
    Page page = pages.get(0);
    assertEquals("mypage", page.id());
    assertEquals("My Title", page.title());
    assertEquals(List.of("tag1", "tag2"), page.tags());
    assertTrue(page.pblic());
  }

  @Test
  void scanPagesPrivatePage() throws IOException {
    Files.writeString(tempDir.resolve("secret"), "Secret\nfoo\nprivate\nHidden");

    List<Page> pages = PagesScanner.scanPages();
    assertFalse(pages.get(0).pblic());
  }

  @Test
  void scanPagesEmptyTags() throws IOException {
    Files.writeString(tempDir.resolve("notags"), "No Tags\n\npublic\nContent");

    List<Page> pages = PagesScanner.scanPages();
    assertTrue(pages.get(0).tags().isEmpty());
  }

  @Test
  void scanPagesEmptyDirectory() {
    List<Page> pages = PagesScanner.scanPages();
    assertTrue(pages.isEmpty());
  }

  @Test
  void scanPagesNonexistentDirectoryThrows() {
    System.setProperty("pages_path", "/nonexistent/path/12345");
    assertThrows(ServiceException.InternalServer.class, PagesScanner::scanPages);
  }

  @Test
  void resolvePagePathValid() {
    Path resolved = PagesScanner.resolvePagePath("mypage");
    assertEquals(tempDir.resolve("mypage"), resolved);
  }

  @Test
  void resolvePagePathRejectsDoubleDot() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath(".."));
  }

  @Test
  void resolvePagePathRejectsTraversal() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath("../etc/passwd"));
  }

  @Test
  void resolvePagePathRejectsSlash() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath("sub/page"));
  }

  @Test
  void resolvePagePathRejectsBackslash() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath("sub\\page"));
  }

  @Test
  void resolvePagePathRejectsNull() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath(null));
  }

  @Test
  void resolvePagePathRejectsEmpty() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath(""));
  }

  @Test
  void resolvePagePathRejectsNullByte() {
    assertThrows(ServiceException.NotFound.class, () -> PagesScanner.resolvePagePath("page\0evil"));
  }
}
