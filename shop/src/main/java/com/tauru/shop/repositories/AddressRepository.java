package com.tauru.shop.repositories;

import com.tauru.shop.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

//    @Query(value = "select address from Address address where address.user.id = :userId", nativeQuery = true)
//    Address findAddressByUserId(@Param("userId") Long userId);


}
