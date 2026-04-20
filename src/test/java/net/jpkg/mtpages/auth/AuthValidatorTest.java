package net.jpkg.mtpages.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.netty.handler.codec.http.HttpRequest;
import java.util.UUID;
import net.jpkg.mtpages.core.AppParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthValidatorTest {

  private static final String TEST_SESSION = UUID.randomUUID().toString();

  @BeforeEach
  void setSessionId() {
    System.setProperty("session_id", TEST_SESSION);
  }

  private HttpRequest mockRequestWithCookie(String cookie) {
    HttpRequest request = mock(HttpRequest.class);
    when(request.headers()).thenReturn(new io.netty.handler.codec.http.DefaultHttpHeaders());
    if (cookie != null) {
      request.headers().set("Cookie", cookie);
    }
    return request;
  }

  @Test
  void isAuthenticatedWithValidSession() {
    HttpRequest request = mockRequestWithCookie("sessionId=" + TEST_SESSION);
    assertTrue(AuthValidator.isAuthenticated(request));
  }

  @Test
  void isAuthenticatedWithWrongSession() {
    HttpRequest request = mockRequestWithCookie("sessionId=wrong-value");
    assertFalse(AuthValidator.isAuthenticated(request));
  }

  @Test
  void isAuthenticatedWithNoCookie() {
    HttpRequest request = mockRequestWithCookie(null);
    assertFalse(AuthValidator.isAuthenticated(request));
  }

  @Test
  void isAuthenticatedWithOtherCookies() {
    HttpRequest request = mockRequestWithCookie("other=abc; sessionId=" + TEST_SESSION + "; foo=bar");
    assertTrue(AuthValidator.isAuthenticated(request));
  }

  @Test
  void isAuthenticatedWithEmptyCookie() {
    HttpRequest request = mockRequestWithCookie("");
    assertFalse(AuthValidator.isAuthenticated(request));
  }
}
