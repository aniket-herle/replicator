package com.aniket.mirror.replicator.service.api.impl;

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
    try {
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

      return response;
    }catch (RestClientResponseException ex){
      log.error("Error occurred while initiating remote upload for {} for jobId {}", this.getType(),
          job.getEventId());
      log.error(ex.getResponseBodyAsString());
      throw ex;
    }
  }

  @Override
  public STRemoteUploadPollResponse pollStatus(String remoteUploadId) {
     return restClient.get()
        .uri(uriBuilder -> uriBuilder
        .path("/remotedl/status")
        .queryParam("login", STREAM_TAPE_API_LOGIN)
        .queryParam("key", STREAM_TAPE_API_KEY)
        .queryParam("id", remoteUploadId)
        .build()
          )
          .retrieve()
        .body(STRemoteUploadPollResponse.class);
  }

}
