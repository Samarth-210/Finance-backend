package org.example.financeapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="users")
public class User{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @Column(unique=true,nullable=false)
    private String email;

    @Column(nullable=true)
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable=false)
    private String userName;

    private String role="USER";

    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
    @JsonIgnore
    private List<ExpenseEntity> expenses;

    @OneToMany(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonIgnore
    private List<EarningsEntity> earnings;

}

