package com.aniket.mirror.replicator.service;

import com.aniket.mirror.events.FileUploadEvent;
import com.aniket.mirror.replicator.config.properties.MirrorProperties;
import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.service.mirror.MirrorProviderService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobExecutorService {

  private final FileReplicationJobService fileReplicationJobService;

  private final List<MirrorProviderService> mirrorProviders;

  private final MirrorProperties mirrorProperties;

  @Async("taskExecutor")
  @Transactional
  public void processEvent(FileUploadEvent event) {
    log.info("Processing event {}", event);
    //Save the event first
    FileReplicationJob  fileReplicationJob = fileReplicationJobService.saveFileUploadEvent(event);
    //Call each uploader
    List<MirrorProviderService> enabledMirrorProviders = getEnabledMirrorProviders();

    //todo continue here with creating an entity of filereplicationjob and mirrorprovider and then further processing here

  }

  private List<MirrorProviderService> getEnabledMirrorProviders() {
    return mirrorProviders.stream().filter(m->  mirrorProperties.getEnabledProviders().contains(m.getName())).collect(
        Collectors.toList());
  }
}
