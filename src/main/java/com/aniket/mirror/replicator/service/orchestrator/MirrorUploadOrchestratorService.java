package com.aniket.mirror.replicator.service.orchestrator;

import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.service.registry.MirrorProviderRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MirrorUploadOrchestratorService {

  private final MirrorProviderRegistry providerRegistry;


  public void orchestrate(FileReplicationJob job) {

    for (MirrorProvider provider : job.getMirrorProviderList()) {

          try {
        providerRegistry
            .get(provider.getProviderName())
            .mirror(provider);
      } catch (Exception ex) {
        log.error("Mirror failed for provider {}",
            provider.getProviderName(), ex);
      }
    }
  }
}
