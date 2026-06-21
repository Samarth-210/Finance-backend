package org.example.financeapp.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="earnings")
public class EarningsEntity {
    private String category;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long amount;
    private Date date;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    //above step ensures that the java object has full User entity but when it is stored in database it stores just the user id
    //we dont store only the id because:
    //You wouldn't be able to fetch the user information automatically when you load an earning.
    //You would have to manually write a second query to find the user by that ID every single time.
    //JPA handles the "Linking" for you. If you set expense.setUser(myUser), Hibernate automatically extracts the ID from myUser and puts it into the user_id column in MySQL.
}
