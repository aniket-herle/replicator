package com.aniket.mirror.replicator.repository;

import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.entity.MirrorProvider;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MirrorProviderRepository extends JpaRepository<MirrorProvider, Long> {

  List<MirrorProvider> findTop50ByFileStatusAndNextPollAtLessThanEqual(
      FileStatus fileStatus,
      Instant now
  );

  List<MirrorProvider> findByFileStatusAndNextPollAtLessThanEqual(
      FileStatus fileStatus,
      Instant now,
      Pageable pageable
  );

  @Query("SELECT mp FROM MirrorProvider mp WHERE " +
      "(mp.lastPolledAt < :threshold OR (mp.lastPolledAt IS NULL AND mp.createdAt < :threshold))")
  List<MirrorProvider> findForRecheck(@Param("threshold") Instant threshold, Pageable pageable);

  Optional<MirrorProvider> findByFileReplicationJob_File_FileIdAndProviderName(String fileId, ProviderType providerName);
}
