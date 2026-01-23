package com.aniket.mirror.replicator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class RestClientConfig {

  @Bean
  public RestClient.Builder restClientBuilder() {
    HttpComponentsClientHttpRequestFactory apacheFactory = new HttpComponentsClientHttpRequestFactory();
    apacheFactory.setConnectionRequestTimeout(10_000);
    apacheFactory.setReadTimeout(10_000);

    // ✅ Buffering needed so response body can be read in interceptor + downstream code
    ClientHttpRequestFactory bufferingFactory =
        new BufferingClientHttpRequestFactory(apacheFactory);

    return RestClient.builder()
        .requestFactory(bufferingFactory)
        .requestInterceptor(new Slf4jLoggingInterceptor());
  }

  @Slf4j
  static class Slf4jLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request,
        byte[] body,
        ClientHttpRequestExecution execution
    ) throws IOException {

      // ✅ REQUEST LOG
      log.info("===== RestClient REQUEST =====");
      log.info("{} {}", request.getMethod(), request.getURI());
      log.info("Headers: {}", request.getHeaders());

      if (body != null && body.length > 0) {
        log.info("Body: {}", new String(body, StandardCharsets.UTF_8));
      } else {
        log.info("Body: <empty>");
      }

      ClientHttpResponse response = execution.execute(request, body);

      // ✅ RESPONSE LOG
      log.info("===== RestClient RESPONSE =====");
      log.info("Status: {}", response.getStatusCode());
      log.info("Headers: {}", response.getHeaders());

      byte[] responseBody = response.getBody().readAllBytes();
      if (responseBody.length > 0) {
        log.info("Body: {}", new String(responseBody, StandardCharsets.UTF_8));
      } else {
        log.info("Body: <empty>");
      }

      // ✅ Return a new response so downstream code can still read body
      return new CachedBodyClientHttpResponse(response, responseBody);
    }
  }

  static class CachedBodyClientHttpResponse implements ClientHttpResponse {

    private final ClientHttpResponse original;
    private final byte[] cachedBody;

    CachedBodyClientHttpResponse(ClientHttpResponse original, byte[] cachedBody) {
      this.original = original;
      this.cachedBody = cachedBody;
    }

    @Override
    public InputStream getBody() {
      return new ByteArrayInputStream(cachedBody);
    }

    @Override
    public org.springframework.http.HttpHeaders getHeaders() {
      return original.getHeaders();
    }

    @Override
    public org.springframework.http.HttpStatusCode getStatusCode() throws IOException {
      return original.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
      return original.getStatusText();
    }

    @Override
    public void close() {
      original.close();
    }
  }
}
