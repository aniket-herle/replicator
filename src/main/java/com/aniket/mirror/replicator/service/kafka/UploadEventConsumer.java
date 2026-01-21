package com.aniket.mirror.replicator.service.kafka;

import com.aniket.mirror.events.FileUploadEvent;
import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.service.FileReplicationJobService;
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

    FileReplicationJobService fileReplicationJobService;

  @KafkaListener(topics = "file_upload", groupId = "file-upload-consumer-group")
  public void consume(FileUploadEvent event, Acknowledgment ack) {

    log.info("Received FileUploadEvent: {}", event);

    boolean isValidEvent = validateEvent(event);
    if (isValidEvent) {
      try{
        processEvent(event);
      }catch (Exception e){
        log.error(e.getMessage());
      }finally {
        ack.acknowledge();
      }

    }else{
      log.error("Received an unexpected event {}", event);
      ack.acknowledge();
    }
  }

  private boolean validateEvent(FileUploadEvent event) {
    return fileReplicationJobService.validateFileUploadEvent(event);
  }

  //process event here
  private void processEvent(FileUploadEvent event) {
    log.info("Processing FileUploadEvent: {}", event);
    //process here
    log.info("End of processing FileUploadEvent");
  }



}
