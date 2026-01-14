package com.aniket.mirror.replicator.service.impl;

import com.aniket.mirror.replicator.dto.kafka.FileUploadEvent;
import com.aniket.mirror.replicator.service.FileUploadEventConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class FileUploadEventConsumerImpl implements FileUploadEventConsumer {

  @KafkaListener(topics = "file_upload", groupId = "file-upload-consumer-group")
  public void consume(
      FileUploadEvent event,
      Acknowledgment ack) {

    try {
      // process event
      System.out.println(event.getFileName());
      ack.acknowledge();
    } catch (Exception e) {
      // retry / DLQ
    }
  }

}
