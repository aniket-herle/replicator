package com.aniket.mirror.replicator.service.mirror.impl;

import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.dto.response.streamtape.StreamTapeRemoteUploadResponse;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.service.api.impl.StreamTapeApiClient;
import com.aniket.mirror.replicator.service.mirror.MirrorProviderService;
import com.aniket.mirror.replicator.service.s3.S3PresignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamTapeMirrorProviderService
    implements MirrorProviderService {


  private final StreamTapeApiClient streamTapeApiClient;

  private final S3PresignService s3PresignService;

  @Override
  public ProviderType getType() {
    return ProviderType.STREAM_TAPE;
  }

  @Async("taskExecutor")
  @Override
  public void mirror(MirrorProvider provider) {

    log.info("Mirroring via {}", getType());

    StreamTapeRemoteUploadResponse  response;
    String s3SignedUrl = s3PresignService.generateS3Url(provider.getFileReplicationJob());
    response = (StreamTapeRemoteUploadResponse) streamTapeApiClient.upload(provider.getFileReplicationJob(),s3SignedUrl);

    log.info("Stream tape upload response: {}", response);
    log.info("Completed mirroring via {}", getType());
  }



}


