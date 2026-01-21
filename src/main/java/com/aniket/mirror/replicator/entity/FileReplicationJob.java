package com.aniket.mirror.replicator.entity;


import com.aniket.mirror.replicator.constants.FileStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileReplicationJob {

  @Id
  private String eventId;

  @Column(unique = true)
  private String fileId;

  private String fileName;

  private String contentType;

  private long sizeBytes;

  private String s3Bucket;

  private String s3Key;

  private String s3Url;

  private String checksum;

  private FileStatus jobStatus;

  @OneToMany(mappedBy="fileReplicationJob",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<MirrorProvider> mirrorProviderList;

  private Instant fileCreatedAt;

  private Instant jobCreatedAt;

  private String jobUpdatedAt;

}
