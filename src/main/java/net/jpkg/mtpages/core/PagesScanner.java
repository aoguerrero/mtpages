package net.jpkg.mtpages.core;

import static net.jpkg.mtpages.core.AppParameters.PAGES_PATH;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.excp.ServiceException;

public class PagesScanner {

  private PagesScanner() {
  }

  public static List<Page> scanPages() {
    Path pagesPath = Path.of(PAGES_PATH.get()).toAbsolutePath();
    try (Stream<Path> filePathList = Files.list(pagesPath)) {
      return filePathList.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
          .map(PagesScanner::getContent).toList();
    } catch (Exception e) {
      throw new ServiceException.InternalServer(e);
    }
  }

  private static Page getContent(Path path) {
    try (BufferedReader bf = Files.newBufferedReader(path)) {
      String title = bf.readLine();
      String pagesTags = bf.readLine();
      String pblic = bf.readLine();
      return new Page(path.getFileName().toString(), title,
          Arrays.stream(pagesTags.split(" ")).filter(t -> !t.isEmpty()).toList(),
          pblic.startsWith("public"));
    } catch (Exception e) {
      throw new ServiceException.InternalServer(e);
    }
  }

  public static Path resolvePagePath(String id) {
    if (id == null || id.isEmpty() || id.contains("..") || id.contains("/")
        || id.contains("\\") || id.contains("\0")) {
      throw new ServiceException.NotFound();
    }
    Path pagesPath = Path.of(PAGES_PATH.get()).toAbsolutePath();
    Path resolved = pagesPath.resolve(id).toAbsolutePath();
    if (!resolved.startsWith(pagesPath)) {
      throw new ServiceException.NotFound();
    }
    return resolved;
  }
}
