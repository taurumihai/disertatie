package com.tauru.shop.repositories;

import com.tauru.shop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


//    @Query("select users from User users where users.username = ?1 and users.password = ?2")
//    User findUserByUserNameAndPassword(String userName, String password);
//
    @Query("select users from User users where users.username = ?1")
    User findUserByUsername(String username);

//    @Query("select users from User users where users.id = ?1")
//    User findUserById(Long userId);


}
