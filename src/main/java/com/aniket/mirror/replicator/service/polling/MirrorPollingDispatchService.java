package com.aniket.mirror.replicator.service.polling;

import com.aniket.mirror.replicator.config.properties.MirrorPollingProperties;
import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.repository.MirrorProviderRepository;
import com.aniket.mirror.replicator.service.orchestrator.MirrorPollingOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MirrorPollingDispatchService {

  private final MirrorProviderRepository mirrorProviderRepository;
  private final MirrorPollingOrchestrator mirrorPollingOrchestrator;
  private final MirrorPollingProperties properties;

  public void dispatchDuePollingJobs() {
    List<MirrorProvider> providers = mirrorProviderRepository
        .findByFileStatusAndNextPollAtLessThanEqual(
            FileStatus.SUBMITTED,
            Instant.now(),
            PageRequest.of(0, properties.getBatchSize())
        );

    if (providers.isEmpty()) {
      log.debug("No providers due for polling");
      return;
    }

    log.info("Dispatching polling batch | size={}", providers.size());
    mirrorPollingOrchestrator.orchestrate(providers);
  }
}
