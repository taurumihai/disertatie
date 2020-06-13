package com.tauru.shop.services;


import com.tauru.shop.entities.Roles;
import com.tauru.shop.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RolesService {

    @Autowired
    private RoleRepository roleRepository;


    public Roles findRoleById(Long roleId) {

        return roleRepository.findRoleById(roleId);
    }

}
