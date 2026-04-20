package net.jpkg.mtpages.ctrl;

import static net.jpkg.mtpages.core.AppParameters.PAGES_PATH;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jpkg.mtpages.auth.AuthValidator;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.ctrl.FormController;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.utl.FileSystemUtils;

public class PageSaveFormCtrl extends FormController {

  private List<Page> pages;

  @Override
  public void execute(HttpHeaders responseHeaders, HttpRequest request, Map<String, String> formData) {
    if (!AuthValidator.isAuthenticated(request)) {
      throw new ServiceException.Unauthorized();
    }
    String title = formData.get("title");

    String sourcePath = getPath();
    Matcher matcher = Pattern.compile(sourcePath).matcher(request.uri());
    String id = getId(title);
    if (matcher.find() && matcher.groupCount() > 0) {
      id = matcher.group(1);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(title).append("\n");
    String tags = formData.get("tags");
    tags = tags.replaceAll("\\s+", " ").trim();
    sb.append(tags).append("\n");
    String pblic = formData.get("public");
    if (pblic != null) {
      sb.append("public").append("\n");
    } else {
      sb.append("private").append("\n");
    }
    sb.append(formData.get("content"));
    String newId = formData.get("identifier");

    Path savePath = Paths.get(PAGES_PATH.get(), id).toAbsolutePath();
    FileSystemUtils.writeStringToFile(savePath, sb.toString());
    pages.remove(new Page(id));
    String redirectPath = getTarget();
    List<String> tagList = Arrays.asList(tags.split(" "));
    if (newId != null && !newId.equals(id)) {
      Path newPath = Paths.get(PAGES_PATH.get(), newId).toAbsolutePath();
      FileSystemUtils.renameFile(savePath, newPath);
      pages.add(new Page(newId, title, tagList, pblic != null));
      setTarget(redirectPath.replace("{id}", newId));
    } else {
      pages.add(new Page(id, title, tagList, pblic != null));
      setTarget(redirectPath.replace("{id}", id));
    }
  }

  private String getId(String title) {
    StringBuilder id = new StringBuilder(
        Normalizer.normalize(title, Normalizer.Form.NFD).toLowerCase().replaceAll("\\W+", "_"));
    boolean valid = false;
    int counter = 0;
    while (!valid) {
      Path savePath = Paths.get(PAGES_PATH.get(), id.toString()).toAbsolutePath();
      if (Files.exists(savePath)) {
        id.append("-").append(++counter);
      } else {
        valid = true;
      }
    }
    return id.toString();
  }

  public void setPages(List<Page> pages) {
    this.pages = pages;
  }
}
