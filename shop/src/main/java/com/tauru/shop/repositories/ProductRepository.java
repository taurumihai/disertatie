package com.tauru.shop.repositories;

import com.tauru.shop.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //    @Query("select users from User users where users.username = ?1 and users.password = ?2")
//    User findUserByUserNameAndPassword(String userName, String password);
//
//    @Query("select users from User users where users.username = ?1")
//    User findUserByUsername(String username);
//
//    @Query("select users from User users where users.email = ?1")
//    List<User> findUserByEmail(String email);
//
    @Query("select product from Product product where product.id = ?1")
    Product findProductById(Long productId);

}
