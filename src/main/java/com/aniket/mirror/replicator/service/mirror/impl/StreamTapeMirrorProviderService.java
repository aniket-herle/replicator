package com.aniket.mirror.replicator.service.mirror.impl;

import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.dto.response.streamtape.poll.STRemoteUploadPollResponse;
import com.aniket.mirror.replicator.dto.response.streamtape.poll.STVideoData;
import com.aniket.mirror.replicator.dto.response.streamtape.upload.STRemoteUploadResponse;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.repository.MirrorProviderRepository;
import com.aniket.mirror.replicator.service.api.impl.StreamTapeApiClient;
import com.aniket.mirror.replicator.service.mirror.MirrorProviderService;
import com.aniket.mirror.replicator.service.s3.S3PresignService;
import java.time.Instant;
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

  private final MirrorProviderRepository mirrorProviderRepository;

  @Override
  public ProviderType getType() {
    return ProviderType.STREAM_TAPE;
  }

  @Async("taskExecutor")
  @Override
  public void mirror(MirrorProvider provider) {

    log.info("Mirroring via {}", getType());

    STRemoteUploadResponse response;
    String s3SignedUrl = s3PresignService.generateS3Url(provider.getFileReplicationJob());
    response =  streamTapeApiClient.upload(provider.getFileReplicationJob(),s3SignedUrl);
    provider.setPollAttemptCount(0);
    //Set when next poll should occur
    provider.setNextPollAt(Instant.now().plusSeconds(10));
    if(!validateResponse(response)) {
      log.error("Error uploading stream tape, response={}", response);
      provider.setFileStatus(FileStatus.FAILED);
    }else{
      provider.setRemoteUploadId(response.getResult().getId());
      provider.setFileStatus(FileStatus.SUBMITTED);
    }
    mirrorProviderRepository.save(provider);
    log.info("Stream tape upload response: {}", response);
    log.info("Completed mirroring via {}", getType());
  }


  @Override
  @Async("pollingExecutor")
  public void poll(MirrorProvider job) throws InterruptedException{
    log.info("Started polling for {} for remote Id {}",getType(),job.getRemoteUploadId());
      String remoteId = job.getRemoteUploadId();
    STRemoteUploadPollResponse pollStatus = streamTapeApiClient.pollStatus(remoteId);
    if(pollStatus==null || !pollStatus.validateResponse()){
      log.error("Polling failed for StreamTape for , remoteId={}", remoteId);
      job.setFileStatus(FileStatus.FAILED);
      job.setLastPolledAt(Instant.now());
      job.setPollAttemptCount(job.getPollAttemptCount()+1);
      //Schedule next poll on failure after 3mins, further it requires more complex logic based on
      // pollattempt count etc which can be done later
      job.setNextPollAt(Instant.now().plusSeconds(180));
      if(pollStatus!=null){
        job.setLastError(pollStatus.getMsg());
      }else{
        job.setLastError("Error occurred while polling stream tape");
      }
    }else{
      STVideoData videoData = pollStatus.getResult().get(job.getRemoteUploadId());
      String status = videoData.getStatus();
      if(status.equals("finished")){
        //Download has been completed
        log.info("StreamTape upload complete for remote upload id {}",job.getRemoteUploadId());
        String linkId =(String) videoData.getLinkid();
        job.setFileStatus(FileStatus.SUCCEEDED);
        job.setLastPolledAt(Instant.now());
        job.setPollAttemptCount(job.getPollAttemptCount()+1);
        job.setExternalFileId(linkId);
      }else if(status.equals("downloading")){
        log.info("StreamTape upload in progress for remote upload id {}",job.getRemoteUploadId());
        job.setLastPolledAt(Instant.now());
        job.setPollAttemptCount(job.getPollAttemptCount()+1);
        job.setNextPollAt(Instant.now().plusSeconds(180));
      }
    }
    mirrorProviderRepository.save(job);
  }


  private boolean validateResponse(STRemoteUploadResponse response) {
    return response != null
        && response.getStatus() == 200
        && "OK".equals(response.getMsg())
        && response.getResult() != null
        && response.getResult().getId() != null;
  }


}


