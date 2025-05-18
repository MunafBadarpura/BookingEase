package com.munaf.airBnbApp.entities;

import com.munaf.airBnbApp.entities.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

}


/*
✅ @ElementCollection
Tells JPA that this is a collection of basic or embeddable types, not entities.
JPA will create a separate table to store these values.
Used when you want to store simple types like String, Integer, or Enum in collections.

✅ EnumType.STRING → stores "ADMIN", "USER", etc.
❌ EnumType.ORDINAL → stores 0, 1, etc. (fragile if enum order changes)
* */
