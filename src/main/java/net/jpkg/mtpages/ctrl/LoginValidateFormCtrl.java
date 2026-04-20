package net.jpkg.mtpages.ctrl;

import static net.jpkg.mtpages.core.AppParameters.PASSWORD;
import static net.jpkg.mtpages.core.AppParameters.SESSION_ID;
import static net.jpkg.mtpages.core.AppParameters.USERNAME;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import net.jpkg.mvcly.ctrl.FormController;
import net.jpkg.mvcly.excp.ServiceException;

public class LoginValidateFormCtrl extends FormController {

  @Override
  public void execute(HttpHeaders responseHeaders, HttpRequest request, Map<String, String> formData) {
    String username = formData.get("username");
    String hash = getMd5(formData.get("password"));

    if (USERNAME.get() != null && PASSWORD.get() != null &&
        username.equals(USERNAME.get()) && hash.equals(PASSWORD.get())) {
      responseHeaders.add("Set-Cookie", "sessionId=" + SESSION_ID.get() + "; Path=/");
    } else {
      throw new ServiceException.Unauthorized();
    }
  }

  private String getMd5(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(input.getBytes("UTF-8"));
      return String.format("%032x", new BigInteger(1, md.digest()));
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      throw new ServiceException.InternalServer();
    }
  }

}
