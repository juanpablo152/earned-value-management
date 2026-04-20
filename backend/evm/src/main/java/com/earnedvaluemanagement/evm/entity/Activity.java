package com.earnedvaluemanagement.evm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "activities")
@Getter
@Setter
public class Activity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetAtCompletion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal plannedProgress;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal actualProgress;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal actualCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
