package com.tauru.shop.services;


import com.tauru.shop.entities.User;
import com.tauru.shop.repositories.UserRepository;
import com.tauru.shop.utilitare.BullShopError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;


    public void saveUser(User user) throws BullShopError {

        if (user != null) {
            LOGGER.info("Saving user into database");
            userRepository.save(user);

        } else {

            throw new BullShopError("Error tryng to save a nul user !");
        }
    }

    public User findUserByUsername(String username) throws BullShopError {

        try {

            return userRepository.findUserByUsername(username);

        } catch (Exception ex) {

            LOGGER.warning("Query should retun unic results. ");
            throw new BullShopError("Error while trying to retrieve username." + ex);
        }
    }

    public boolean checkEmailAvailability(String email) {
        List<User> allUsers = userRepository.findAll();
        List<String> usersEmails = new ArrayList<String>();

        for (User user : allUsers) {

            usersEmails.add(user.getEmail());
            if (usersEmails.contains(email)) return false;
        }

        return true;
    }
}
