package com.tiffany.paymod.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
}
