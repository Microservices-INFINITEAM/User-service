package com.example.userservice.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="usersProductCount")
public class CountEntity {
    @Id
    @Column(nullable = false, unique = false)
    private String userId;
    @Column(nullable = false, unique = false)
    private String product;
    @Column(nullable = false, unique = false)
    private Integer count;
}
