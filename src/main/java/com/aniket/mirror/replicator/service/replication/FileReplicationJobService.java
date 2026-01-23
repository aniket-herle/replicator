package com.aniket.mirror.replicator.service.replication;

import com.aniket.mirror.events.FileUploadEvent;
import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.repository.FileReplicationJobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileReplicationJobService {

  private final FileReplicationJobRepository repository;

  public boolean validateFileUploadEvent(FileUploadEvent event) {
    if(event!=null && event.getFileName()!=null && !event.getFileName().isEmpty() && event.getEventId()!=null && !event.getEventId().isEmpty()) {
      //check if event already exist
      if(repository.existsById(event.getEventId()) || repository.existsByFileId(event.getFileId())){
        log.error("File already exists with eventId: {} and fileId {}", event.getEventId(),event.getFileId());
        return false;
      };
      return true;
    }else{
      return false;
    }
  }

  public FileReplicationJob saveFileUploadEvent(FileUploadEvent event,
      List<ProviderType> mirrorProviders) {

    FileReplicationJob fileReplicationJob = new FileReplicationJob(event);
    //Create Mirror provider entity objects
    List<MirrorProvider> mirrorProviderEntities = mirrorProviders.stream().map(msp->{
          MirrorProvider mp = new MirrorProvider();
          mp.setProviderName(msp);
          mp.setFileStatus(FileStatus.CREATED);
          mp.setFileReplicationJob(fileReplicationJob);
          return mp;
        }).toList();

    fileReplicationJob.setMirrorProviderList(mirrorProviderEntities);
    return repository.save(fileReplicationJob);
  }
}
