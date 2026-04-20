package net.jpkg.mtpages.ctrl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import net.jpkg.mvcly.excp.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

class LoginValidateFormCtrlTest {

  @BeforeEach
  void setUp() {
    // user:secret -> MD5 of "secret" = 5ebe2294ecd0e0f08eab7690d2a6ee69
    System.setProperty("username", "testuser");
    System.setProperty("password", "5ebe2294ecd0e0f08eab7690d2a6ee69");
  }

  @Test
  void validLoginSetsCookie() {
    LoginValidateFormCtrl ctrl = new LoginValidateFormCtrl();
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    Map<String, String> formData = Map.of("username", "testuser", "password", "secret");

    ctrl.execute(headers, request, formData);

    String cookie = headers.get("Set-Cookie");
    assertNotNull(cookie);
    assertTrue(cookie.startsWith("sessionId="));
    assertTrue(cookie.contains("Path=/"));
  }

  @Test
  void wrongPasswordThrows() {
    LoginValidateFormCtrl ctrl = new LoginValidateFormCtrl();
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    Map<String, String> formData = Map.of("username", "testuser", "password", "wrong");

    assertThrows(ServiceException.Unauthorized.class,
        () -> ctrl.execute(headers, request, formData));
  }

  @Test
  void wrongUsernameThrows() {
    LoginValidateFormCtrl ctrl = new LoginValidateFormCtrl();
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    Map<String, String> formData = Map.of("username", "wronguser", "password", "secret");

    assertThrows(ServiceException.Unauthorized.class,
        () -> ctrl.execute(headers, request, formData));
  }

  @Test
  void emptyCredentialsThrows() {
    LoginValidateFormCtrl ctrl = new LoginValidateFormCtrl();
    HttpHeaders headers = new io.netty.handler.codec.http.DefaultHttpHeaders();
    HttpRequest request = mock(HttpRequest.class);
    Map<String, String> formData = Map.of("username", "", "password", "");

    assertThrows(ServiceException.Unauthorized.class,
        () -> ctrl.execute(headers, request, formData));
  }
}
