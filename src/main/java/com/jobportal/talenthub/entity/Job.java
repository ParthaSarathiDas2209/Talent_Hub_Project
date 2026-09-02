package com.jobportal.talenthub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyEmail;

    @Column(nullable = false)
    private String companyPhone;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Long salary;

    // Many jobs can belong to one recruiter.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.DRAFT;

    // Soft delete keeps the database record instead of physically deleting it.
    @Column(nullable = false)
    private boolean deleted = false;

    // Candidates can apply only within this time window.
    @Column(name = "application_start_time")
    private LocalDateTime applicationStartTime;

    @Column(name = "application_end_time")
    private LocalDateTime applicationEndTime;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Application.job owns this relationship.
    @OneToMany(mappedBy = "job")
    private List<Application> applications;
}