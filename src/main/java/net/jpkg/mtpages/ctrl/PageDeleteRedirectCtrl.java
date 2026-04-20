package net.jpkg.mtpages.ctrl;

import static net.jpkg.mtpages.core.AppParameters.PAGES_PATH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mtpages.auth.AuthValidator;
import net.jpkg.mtpages.core.PagesScanner;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.ctrl.RedirectController;
import net.jpkg.mvcly.excp.ServiceException;

public class PageDeleteRedirectCtrl extends RedirectController {

  private List<Page> pages;

  @Override
  public void execute(HttpHeaders responseheaders, HttpRequest request) {
    if (!AuthValidator.isAuthenticated(request)) {
      throw new ServiceException.Unauthorized();
    }
    String sourcePath = getPath();
    Matcher matcher = Pattern.compile(sourcePath).matcher(request.uri());
    if (matcher.find() && matcher.groupCount() > 0) {
      try {
        String id = matcher.group(1);
        Path trashDir = Paths.get(PAGES_PATH.get(), "trash").toAbsolutePath();
        Files.createDirectories(trashDir);
        Path deletePath = PagesScanner.resolvePagePath(id);
        Path trashPath = Paths.get(trashDir.toString(), id).toAbsolutePath();
        if (Files.exists(trashPath)) {
          trashPath = Paths.get(trashDir.toString(), id + "-" + System.currentTimeMillis()).toAbsolutePath();
        }
        Files.move(deletePath, trashPath);
        pages.remove(new Page(id));
      } catch (IOException ioe) {
        throw new ServiceException.InternalServer(ioe);
      }
    }
  }

  public void setPages(List<Page> pages) {
    this.pages = pages;
  }

}
