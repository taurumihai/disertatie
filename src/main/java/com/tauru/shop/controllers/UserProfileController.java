package com.tauru.shop.controllers;

import com.tauru.shop.entities.User;
import com.tauru.shop.services.UserService;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserProfileController {

    Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

    private static final String EMPTY_FIELDS = "emptyFields";
    private static final String NO_USER_FOUND_BY_PASSWORD = "noUserFound";
    private static final String PASSWORD_ERROD = "passwordError";


    @Autowired
    private UserService userService;

    @RequestMapping("/userProfile")
    public String userProfileView(HttpSession session, HttpServletRequest request, Model model,
                                  final String currentPassword, final String newPassword, final String confirmPassword) throws BullShopError {

        if (StringUtils.isNullOrEmpty(currentPassword) || StringUtils.isNullOrEmpty(newPassword) || StringUtils.isNullOrEmpty(confirmPassword)) {

            model.addAttribute(EMPTY_FIELDS,"Va rugam completati toate campurile!");
            return "userProfile";
        }

        User findUser = userService.findUserByPassword(currentPassword);

        if (findUser != null && newPassword.equals(confirmPassword) && !newPassword.equals(currentPassword)) {

            findUser.setPassword(newPassword);

            LOGGER.debug("User changed password !");
            userService.saveUser(findUser);
        } else if (currentPassword.equals(newPassword)) {

            model.addAttribute(PASSWORD_ERROD, "Noua parola nu poate coincide cu cea actuala!");

        } else {
            LOGGER.error("No user found with this password!");
            model.addAttribute(NO_USER_FOUND_BY_PASSWORD,"Niciun utilizator gasit cu parola introdusa!");
            throw new BullShopError("Eroare de sistem !");
        }

        return "userProfile";
    }
}
