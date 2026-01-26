package com.aniket.mirror.replicator.service.api.impl;

import com.aniket.mirror.common.exception.ExternalServiceException;
import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.dto.response.ApiResponse;
import com.aniket.mirror.replicator.dto.response.streamtape.poll.STRemoteUploadPollResponse;
import com.aniket.mirror.replicator.dto.response.streamtape.upload.STRemoteUploadResponse;
import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.service.api.ProviderApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class StreamTapeApiClient
    implements ProviderApiClient {

  private final RestClient restClient;

  @Value("${mirror.streamTape.api-login}")
  private  String STREAM_TAPE_API_LOGIN;

  @Value("${mirror.streamTape.api-key}")
  private String STREAM_TAPE_API_KEY;

  public StreamTapeApiClient(RestClient.Builder builder) {
    this.restClient = builder
        .baseUrl("https://api.streamtape.com")
        .build();
  }
  @Override
  public ProviderType getType() {
    return ProviderType.STREAM_TAPE;
  }

  @Override
  public STRemoteUploadResponse upload(FileReplicationJob job,String fileURL) {
    long startTime = System.currentTimeMillis();
    try {
      log.info("Starting external call to StreamTape API for upload, jobId: {}", job.getEventId());
      STRemoteUploadResponse response = restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/remotedl/add")
              .queryParam("login", STREAM_TAPE_API_LOGIN)
              .queryParam("key", STREAM_TAPE_API_KEY)
              .queryParam("url", fileURL)
              .build()
          )
          .retrieve()
          .body(STRemoteUploadResponse.class);

      long duration = System.currentTimeMillis() - startTime;
      log.info("External call to StreamTape API for upload completed successfully in {}ms, jobId: {}", duration, job.getEventId());
      return response;
    } catch (RestClientResponseException ex) {
      long duration = System.currentTimeMillis() - startTime;
      log.error("Failed to call StreamTape API for upload, jobId: {}, status: {}, response: {}", job.getEventId(), ex.getStatusCode(), truncateResponseBody(ex.getResponseBodyAsString()));
      throw new ExternalServiceException("Failed to initiate remote upload via StreamTape API", ex);
    } catch (RestClientException ex) {
      long duration = System.currentTimeMillis() - startTime;
      log.error("Failed to call StreamTape API for upload, jobId: {}, error: {}", job.getEventId(), ex.getMessage());
      throw new ExternalServiceException("Failed to initiate remote upload via StreamTape API", ex);
    }
  }

  @Override
  public STRemoteUploadPollResponse pollStatus(String remoteUploadId) {
    long startTime = System.currentTimeMillis();
    try {
      log.info("Starting external call to StreamTape API for poll status, uploadId: {}", remoteUploadId);
      STRemoteUploadPollResponse response = restClient.get()
          .uri(uriBuilder -> uriBuilder
          .path("/remotedl/status")
          .queryParam("login", STREAM_TAPE_API_LOGIN)
          .queryParam("key", STREAM_TAPE_API_KEY)
          .queryParam("id", remoteUploadId)
          .build()
            )
            .retrieve()
          .body(STRemoteUploadPollResponse.class);

      long duration = System.currentTimeMillis() - startTime;
      log.info("External call to StreamTape API for poll status completed successfully in {}ms, uploadId: {}", duration, remoteUploadId);
      return response;
    } catch (RestClientResponseException ex) {
      long duration = System.currentTimeMillis() - startTime;
      log.error("Failed to call StreamTape API for poll status, uploadId: {}, status: {}, response: {}", remoteUploadId, ex.getStatusCode(), truncateResponseBody(ex.getResponseBodyAsString()));
      throw new ExternalServiceException("Failed to poll status from StreamTape API", ex);
    } catch (RestClientException ex) {
      long duration = System.currentTimeMillis() - startTime;
      log.error("Failed to call StreamTape API for poll status, uploadId: {}, error: {}", remoteUploadId, ex.getMessage());
      throw new ExternalServiceException("Failed to poll status from StreamTape API", ex);
    }
  }

  private String truncateResponseBody(String body) {
    if (body == null) return null;
    return body.length() > 500 ? body.substring(0, 500) + "..." : body;
  }
}
