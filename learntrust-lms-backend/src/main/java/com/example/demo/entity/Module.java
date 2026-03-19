package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int moduleOrder;

    @Column(name = "sequence_no", nullable = false)
    private int sequenceNo;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Module() {}

    
    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}