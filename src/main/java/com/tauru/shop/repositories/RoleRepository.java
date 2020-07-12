package com.tauru.shop.repositories;

import com.tauru.shop.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    @Query("select roles from Roles roles where roles.id = ?1")
    Roles findRoleById(Long roleId);
}
