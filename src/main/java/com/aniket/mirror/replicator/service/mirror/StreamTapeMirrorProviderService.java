package com.aniket.mirror.replicator.service.mirror;

import com.aniket.mirror.replicator.entity.FileReplicationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamTapeMirrorProviderService implements MirrorProviderService {

  private static final  String providerName = "Stream Tape";

  @Override
  public void mirror(FileReplicationJob job) {
    System.out.println("Uploading....");
    System.out.println("Done");
  }

  @Override
  public String getName() {
    return providerName;
  }

}
