package com.earnedvaluemanagement.evm.repository;

import com.earnedvaluemanagement.evm.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByProjectId(Long projectId);

    Optional<Activity> findByIdAndProjectId(Long id, Long projectId);
}
