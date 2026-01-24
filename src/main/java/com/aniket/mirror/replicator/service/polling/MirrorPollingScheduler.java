package com.aniket.mirror.replicator.service.polling;

import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import com.aniket.mirror.replicator.repository.MirrorProviderRepository;
import com.aniket.mirror.replicator.service.mirror.MirrorProviderService;
import com.aniket.mirror.replicator.service.orchestrator.MirrorPollingOrchestrator;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MirrorPollingScheduler {

  private final MirrorProviderRepository mirrorProviderRepository;

  private final MirrorPollingOrchestrator mirrorPollingOrchestrator;

  /**
   * Dispatcher only.
   * Runs on Spring scheduler thread.
   * Heavy work is delegated to @Async pollingExecutor.
   */
  @Scheduled(fixedDelay = 30_000) // every 30 seconds
  public void dispatchPollingJobs() {

    log.debug("Dispatching polling jobs");

    List<MirrorProvider> providers =
        mirrorProviderRepository
            .findTop50ByFileStatusAndNextPollAtLessThanEqual(
                FileStatus.SUBMITTED,
                Instant.now()
            );

    if (providers.isEmpty()) {
      return;
    }
    mirrorPollingOrchestrator.orchestrate(providers);

  }
}
