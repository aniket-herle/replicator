package com.aniket.mirror.replicator.service.mirror;

import com.aniket.mirror.replicator.constants.ProviderType;
import com.aniket.mirror.replicator.entity.MirrorProvider;

public interface MirrorProviderService {

  void mirror(MirrorProvider job) throws InterruptedException;

  ProviderType getType();
}
