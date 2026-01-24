package com.aniket.mirror.replicator.service.orchestrator;

import com.aniket.mirror.replicator.entity.FileReplicationJob;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.service.registry.MirrorProviderRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MirrorPollingOrchestrator {

  private final MirrorProviderRegistry providerRegistry;

  public void orchestrate(List<MirrorProvider> mirrorProviderList) {

    for (MirrorProvider provider : mirrorProviderList) {

      try {
        providerRegistry
            .get(provider.getProviderName())
            .poll(provider);
      } catch (Exception ex) {
        log.error("Polling failed for provider {}",
            provider.getProviderName(), ex);
      }
    }
  }
}
