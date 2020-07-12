package com.tauru.shop.services;

import com.tauru.shop.entities.Address;
import com.tauru.shop.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AddressService {


    @Autowired
    private AddressRepository addressRepository;


    public void saveAddress(Address address) {

        if (address != null) {

            addressRepository.save(address);
        }
    }

    public Address findAddressByUserId(Long userId) {

        List<Address> addressList = addressRepository.findAll();

        for (Address address : addressList) {
            if (address.getUser().getId().equals(userId)) {
                return address;
            }
        }
        return null;
    }
}
