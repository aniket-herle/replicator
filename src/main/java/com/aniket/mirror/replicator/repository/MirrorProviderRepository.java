package com.aniket.mirror.replicator.repository;

import com.aniket.mirror.replicator.entity.MirrorProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MirrorProviderRepository extends JpaRepository<MirrorProvider, Long> {

}
