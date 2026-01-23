package com.aniket.mirror.replicator.entity;


import com.aniket.mirror.events.FileUploadEvent;
import com.aniket.mirror.replicator.constants.FileStatus;
import com.aniket.mirror.replicator.constants.JobStatus;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

  private JobStatus jobStatus;

  @OneToMany(mappedBy="fileReplicationJob",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<MirrorProvider> mirrorProviderList;

  private Instant fileCreatedAt;

  @CreationTimestamp
  private Instant jobCreatedAt;

  @UpdateTimestamp
  private String jobUpdatedAt;


  public FileReplicationJob(FileUploadEvent event) {
    this.fileId = event.getFileId();
    this.eventId = event.getEventId();
    this.fileName = event.getFileName();
    this.jobStatus = JobStatus.CREATED;
    this.checksum = event.getChecksum();
    this.s3Bucket = event.getS3Bucket();
    this.s3Key = event.getS3Key();
    this.s3Url = event.getS3Url();
    this.contentType = event.getContentType();
    this.sizeBytes = event.getSizeBytes();
    this.fileCreatedAt = event.getCreatedAt();
  }


}
