package com.jobportal.talenthub;

import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.entity.*;
import com.jobportal.talenthub.exception.AccessDeniedException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.impl.JobServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobServiceImpl jobService;


    @Test
    void getJobById_ShouldReturnJob_WhenJobExists() {
        Job job = new Job();
        job.setId(10L);
        job.setTitle("Java Developer");
        job.setDescription("Backend development using Java");
        job.setCompanyName("ABC Technologies");
        job.setCompanyEmail("hr@abc.com");
        job.setCompanyPhone("9876543210");
        job.setLocation("Kolkata");
        job.setSalary(50000L);

        User recruiter = new User();
        recruiter.setId(1L);

        job.setRecruiter(recruiter);

        Optional<Job> result = Optional.of(job);

        when(jobRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(result);

        JobResponseDto responseDto = jobService.getJobById(10L);

        assertEquals("Java Developer", responseDto.title());
        assertEquals("ABC Technologies", responseDto.companyName());

    }

    @Test
    void getJobById_WhenJobDoesNotExist_ShouldThrowException() {
        when(jobRepository.findByIdAndDeletedFalse(20L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJobById(20L);
        });
    }


    @Test
    void getAllJobs_ShouldReturnPagedJobs() {
        Pageable pageable = PageRequest.of(0, 5);

        List<Job> jobs = new ArrayList<>();
        Job job = new Job();
        job.setId(10L);
        job.setTitle("Java Developer");
        job.setCompanyName("ABC Technologies");

//        job.getRecruiter().getId();
        User user = new User();
        user.setId(10L);
        job.setRecruiter(user);
        jobs.add(job);

        Page<Job> jobPage = new PageImpl<>(jobs);

        when(jobRepository.findAllByDeletedFalse(pageable))
                .thenReturn(jobPage);

        Page<JobResponseDto> response = jobService.getAllJobs(pageable);

        assertEquals(jobs.size(), response.getContent().size());
        assertEquals("Java Developer", response.getContent().get(0).title());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        verify(jobRepository, times(1))
                .findAllByDeletedFalse(pageable);

//        verify(jobRepository, never())
//                .findAllByDeletedFalse(pageable);
    }


    @Test
    void getAllJobs_WhenNoJobsExist_ShouldReturnEmptyPage() {
        // Page<Job> emptyPage = new PageImpl<>(new ArrayList<>());
        Page<Job> emptyPage = new PageImpl<>(List.of());

        Pageable pageable = PageRequest.of(0, 5);

        when(jobRepository.findAllByDeletedFalse(pageable))
                .thenReturn(emptyPage);

        Page<JobResponseDto> response = jobService.getAllJobs(pageable);

        assertTrue(response.isEmpty());

        verify(jobRepository,
                times(1))
                .findAllByDeletedFalse(pageable);
    }

    @Test
    void deleteJobs_ShouldDeleteJobs() {
        Long jobId = 10L;
        String email = "recruiter@test.com";

        Job job = new Job();
        job.setId(jobId);

        User recruiter = new User();
        recruiter.setId(10L);
        job.setRecruiter(recruiter);

        User loggedInUser = new User();
        loggedInUser.setId(10L);
        loggedInUser.setEmail(email);
        loggedInUser.setRole(Role.RECRUITER);
        loggedInUser.setStatus(UserStatus.ACTIVE);

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.of(job));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(loggedInUser));

        jobService.deleteJob(jobId, email);

        assertTrue(job.isDeleted());
        assertEquals(JobStatus.DELETED, job.getStatus());
        assertNotNull(job.getDeletedAt());

        verify(jobRepository)
                .save(job);
    }


    @Test
    void deleteJob_WhenJobNotFound_ShouldThrowException() {
        Long jobId = 20L;
        String email = "recruiter@test.com";

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.deleteJob(jobId, email);
        });
    }

    @Test
    void deleteJob_WhenRecruiterNotFound_ShouldThrowException() {
        Long jobId = 20L;
        String email = "recruiter@test.com";

        Job job = new Job();
        job.setId(jobId);

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.of(job));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.deleteJob(jobId, email);
        });
    }


    @Test
    void deleteJob_WhenUserIsNotRecruiter_ShouldThrowException() {

        Long userId = 20L;
        Long jobId = 10L;

        String email = "recruiter@test.com";

        User user = new User();
        user.setId(userId);

        Job job = new Job();
        job.setId(jobId);

        user.setRole(Role.JOB_SEEKER);
        user.setStatus(UserStatus.ACTIVE);

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.of(job));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> {
            jobService.deleteJob(jobId, email);
        });
    }

    @Test
    void deleteJob_WhenRecruiterIsInactive_ShouldThrowException() {
        Long userId = 20L;
        Long jobId = 10L;

        String email = "recruiter@test.com";

        User user = new User();
        user.setId(userId);

        Job job = new Job();
        job.setId(jobId);

        user.setRole(Role.RECRUITER);
        user.setStatus(UserStatus.INACTIVE);

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.of(job));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> {
            jobService.deleteJob(jobId, email);
        });
    }

    @Test
    void deleteJob_WhenRecruiterDoesNotOwnJob_ShouldThrowException() {
        Long jobId = 100L;
        Long recruiterId = 10L;
        Long loggedInUserId = 20L;

        String email = "recruiter@test.com";

        User user = new User();
        user.setId(loggedInUserId);

        Job job = new Job();
        job.setId(jobId);

        User jobRecruiter = new User();
        jobRecruiter.setId(recruiterId);

        job.setRecruiter(jobRecruiter);

        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.RECRUITER);

        when(jobRepository.findByIdAndDeletedFalse(jobId))
                .thenReturn(Optional.of(job));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> {
            jobService.deleteJob(jobId, email);
        });

    }
}

