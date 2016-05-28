package com.qqj.org.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class PendingApprovalCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String certificateNumber;

    @ManyToOne
    private Customer parent;

    private Short level;

    private String username;

    private String password;

    private String telephone;

    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Short status;

    @ManyToOne
    private Team team;

    private Short stage;
}
