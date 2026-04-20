package net.jpkg.mtpages.core;

import static net.jpkg.mtpages.core.AppParameters.PAGES_PATH;
import static net.jpkg.mtpages.core.AppParameters.PASSWORD;
import static net.jpkg.mtpages.core.AppParameters.SESSION_ID;
import static net.jpkg.mtpages.core.AppParameters.USERNAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jpkg.mtpages.ctrl.HomeTemplateCtrl;
import net.jpkg.mtpages.ctrl.LoginTemplateCtrl;
import net.jpkg.mtpages.ctrl.LoginValidateFormCtrl;
import net.jpkg.mtpages.ctrl.LogoutRedirectCtrl;
import net.jpkg.mtpages.ctrl.PageDeleteConfirmationTemplateCtrl;
import net.jpkg.mtpages.ctrl.PageDeleteRedirectCtrl;
import net.jpkg.mtpages.ctrl.PageListTemplateCtrl;
import net.jpkg.mtpages.ctrl.PageNewTemplateCtrl;
import net.jpkg.mtpages.ctrl.PageSaveFormCtrl;
import net.jpkg.mtpages.ctrl.PageViewTemplateCtrl;
import net.jpkg.mtpages.mdl.Page;
import net.jpkg.mvcly.core.Application;
import net.jpkg.mvcly.ctrl.ControllerFactory;
import net.jpkg.mvcly.ctrl.ControllersConfig;
import net.jpkg.mvcly.ctrl.StaticController;


public class Main {

  private static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {

    logger.info(
        "Accepted JVM parameters: '{}', '{}', '{}'",
        PAGES_PATH.getName(),
        USERNAME.getName(),
        PASSWORD.getName());

    logger.info("Pages path: {}", PAGES_PATH.get());

    System.setProperty(SESSION_ID.getName(), UUID.randomUUID().toString());

    List<Page> pages = new CopyOnWriteArrayList<>(PagesScanner.scanPages());

    Map<String, Object> dependencies = new HashMap<>();
    dependencies.put("templateMap", new HashMap<>());
    dependencies.put("staticMap", new HashMap<>());
    dependencies.put("pages", pages);
    ControllerFactory cf = new ControllerFactory(dependencies);

    ControllersConfig config = new ControllersConfig();

    config.add(cf.getController(HomeTemplateCtrl.class), "/", "home.vm");
    config.add(cf.getController(LoginTemplateCtrl.class), "/login", "login.vm");
    config.add(cf.getController(LoginValidateFormCtrl.class), "/login/validate", "/");
    config.add(cf.getController(LogoutRedirectCtrl.class), "/logout", "/");
    config.add(cf.getController(StaticController.class), "/files/(.*)", "");
    config.add(cf.getController(StaticController.class), "/favicon\\.ico", "favicon.ico");
    config.add(cf.getController(PageListTemplateCtrl.class), "/pages/list(/.*)?", "list.vm");
    config.add(cf.getController(PageViewTemplateCtrl.class), "/pages/(.*)/view", "view.vm");
    config.add(cf.getController(PageNewTemplateCtrl.class), "/pages/new/(.*)", "new.vm");
    config.add(cf.getController(PageSaveFormCtrl.class), "/pages/save", "/pages/{id}/view");
    config.add(cf.getController(PageSaveFormCtrl.class), "/pages/(.*)/save", "/pages/{id}/view");
    config.add(cf.getController(PageViewTemplateCtrl.class), "/pages/(.*)/edit", "edit.vm");
    config.add(cf.getController(PageDeleteConfirmationTemplateCtrl.class),
        "/pages/(.*)/delete/confirmation", "delete_confirmation.vm");
    config.add(cf.getController(PageDeleteRedirectCtrl.class), "/pages/(.*)/delete", "/pages/list");
    new Application().start(config);
  }
}
