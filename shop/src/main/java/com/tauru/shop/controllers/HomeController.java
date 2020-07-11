package com.tauru.shop.controllers;


import com.tauru.shop.entities.Roles;
import com.tauru.shop.entities.User;
import com.tauru.shop.services.RolesService;
import com.tauru.shop.services.UserService;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    @RequestMapping("/")
    public String viewHome() {

        return "index";
    }

    @RequestMapping("/register")
    public String viewLogin(String username, String password, String email, Model model) throws BullShopError {

        User user = new User();
        boolean checkEmail = userService.checkEmailAvailability(email);
        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password) || StringUtils.isNullOrEmpty(email)) {
            // eroarea aici si nu in if pt ca la prima accesare apare mesajul. Vreau sa apara cand nu completeaza vreun camp
            // model.addAttribute("registerError", "Va rugam completati toate campurile.");

            return "register";
        }

        User searchUser = userService.findUserByUsername(username);
        Roles userRole = rolesService.findRoleById((long) 2);

        if (searchUser == null) {
            user.setUsername(username);

            if (checkEmail) {
                user.setEmail(email);

            } else {

                model.addAttribute("emailError", "Email is already used.");
                return "register";
            }
            if (!StringUtils.isNullOrEmpty(password)) {
                user.setPassword(password);
            }
            user.setRoles(userRole);
            LOGGER.info("Creating new user " + username + email);
            userService.saveUser(user);
            return "login";

        } else {

            model.addAttribute("existingUser", "Username already used. Please chose another one.");
            return "register";
        }
    }

    @RequestMapping("/welcome")
    public String loginView(String username, String password, Model model, HttpSession session, HttpServletRequest request) throws BullShopError {

        session = request.getSession(true);

        if ((User) session.getAttribute("loggedUser") != null) {
            return "welcome";
        }

        User checkUser = userService.findUserByUsername(username);


        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
            // eroarea aici si nu in if pt ca la prima accesare apare mesajul. Vreau sa apara cand nu completeaza vreun camp
            // model.addAttribute("registerError", "Va rugam completati toate campurile.");

            return "login";
        }

        Roles adminRole = rolesService.findRoleById((long) 1);
        Roles userRole = rolesService.findRoleById((long) 2);

        if (username.equals("Admin123!@#") && password.equals("TauruMihai95")) {

            if (checkUser.getRoles() == null) {
                checkUser.setRoles(adminRole);
            }

            userService.saveUser(checkUser);
            session.setAttribute("loggedUser", checkUser);
            return "adminView";
        }

        if (checkUser != null && checkUser.getRoles() != adminRole) {

            session.setAttribute("loggedUser", checkUser);

            if (password.equals(checkUser.getPassword())) {

                if (checkUser.getRoles() == null) {
                    checkUser.setRoles(userRole);
                    userService.saveUser(checkUser);
                }
                session.setAttribute("loggedUser", checkUser);
                return "welcome";
            } else {

                model.addAttribute("passError", "Incorrect password. Try again.");
                session.setAttribute("loggedUser", checkUser);
                return "login";
            }
        }
        return "login";
    }
}
