package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Role;
import com.jobportal.talenthub.entity.User;

import com.jobportal.talenthub.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByDeletedFalse();

    Optional<User> findByIdAndDeletedFalse(Long id);

    Long countByDeletedFalse();

    Long countByRoleAndDeletedFalse(Role role);

    Long countByStatusAndDeletedFalse(UserStatus status);

}