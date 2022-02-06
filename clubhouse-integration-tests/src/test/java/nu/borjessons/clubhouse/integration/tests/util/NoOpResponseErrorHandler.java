package nu.borjessons.clubhouse.integration.tests.util;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

public class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {
  public static final ResponseErrorHandler INSTANCE = new NoOpResponseErrorHandler();

  private NoOpResponseErrorHandler() {
    // do nothing
  }

  @Override
  public void handleError(ClientHttpResponse clientHttpResponse) {
    // do nothing
  }
}
