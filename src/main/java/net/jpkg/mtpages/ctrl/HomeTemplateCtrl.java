package net.jpkg.mtpages.ctrl;

import static net.jpkg.mtpages.core.AppParameters.PAGES_PATH;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jpkg.mtpages.auth.AuthValidator;
import net.jpkg.mtpages.core.AppParameters;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.utl.FileSystemUtils;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class HomeTemplateCtrl extends BaseTemplateCtrl {

  private final HtmlRenderer renderer;
  private final Parser parser;

  private List<Page> pages;

  public HomeTemplateCtrl() {
    Set<Extension> extensions = Set.of(AutolinkExtension.create(), TablesExtension.create(),
        ImageAttributesExtension.create());
    this.parser = Parser.builder().extensions(extensions).build();
    this.renderer = HtmlRenderer.builder().build();
  }

  public Map<String, Object> getContext(HttpRequest request) {
    String[] lines = getLines();
    StringBuilder content = new StringBuilder();
    for (int i = 3; i < lines.length; i++) {
      content.append(lines[i]).append("\n");
    }
    boolean auth = AuthValidator.isAuthenticated(request);
    List<String> allTags = new ArrayList<>(TagUtil.getTags(auth, pages));

    Map<String, Object> data = new HashMap<>();
    data.put("page_title", AppParameters.WEBSITE.get());
    data.put("auth", auth);
    data.put("title", lines[0]);
    data.put("content", renderer.render(parser.parse(content.toString())));
    data.put("tags", allTags);
    return data;
  }

  private String[] getLines() {
    try {
      Path path = Paths.get(PAGES_PATH.get(), "home").toAbsolutePath();
      return new String(FileSystemUtils.getFileContent(path), StandardCharsets.UTF_8).split("\\R");
    } catch (ServiceException.NotFound nf) {
      return new String[]{
          "No home defined",
          "home",
          "public",
          "Create a new page with title **home** to change this content."};
    }
  }

  public void setPages(List<Page> pages) {
    this.pages = pages;
  }
}
