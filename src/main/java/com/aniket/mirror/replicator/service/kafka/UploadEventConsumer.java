package com.aniket.mirror.replicator.service.kafka;

import com.aniket.mirror.events.FileUploadEvent;
import com.aniket.mirror.replicator.service.replication.FileReplicationJobService;
import com.aniket.mirror.replicator.service.executor.JobExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadEventConsumer
{

  private final FileReplicationJobService fileReplicationJobService;

  private final JobExecutorService jobExecutorService;

  @KafkaListener(topics = "file_upload", groupId = "file-upload-consumer-group")
  public void consume(FileUploadEvent event, Acknowledgment ack) {

    try {
      log.info("Received FileUploadEvent: {}", event);

      boolean isValidEvent = validateEvent(event);

      if (isValidEvent) {
        processEvent(event);
      } else {
        log.error("Received an unexpected event {}", event);
      }

    } catch (Exception e) {
      log.error("Error while consuming event: {}", event, e);
    } finally {
      ack.acknowledge(); //  ensures commit always happens
    }
  }


  private boolean validateEvent(FileUploadEvent event) {
    return fileReplicationJobService.validateFileUploadEvent(event);
  }

  //process event here
  private void processEvent(FileUploadEvent event) {
    log.info("Processing FileUploadEvent: {}", event);
    //process here
    jobExecutorService.processEvent(event);
    log.info("End of processing FileUploadEvent");
  }



}
