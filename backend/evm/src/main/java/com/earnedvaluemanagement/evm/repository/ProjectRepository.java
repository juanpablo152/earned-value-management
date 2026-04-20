package com.earnedvaluemanagement.evm.repository;

import com.earnedvaluemanagement.evm.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
