package com.aniket.mirror.replicator.repository;

import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MirrorProviderRepository extends JpaRepository<MirrorProvider, Long> {

  List<MirrorProvider> findTop50ByFileStatusAndNextPollAtLessThanEqual(
      FileStatus fileStatus,
      Instant now
  );
}
