package com.tauru.shop.controllers;


import com.tauru.shop.entities.Order;
import com.tauru.shop.entities.Roles;
import com.tauru.shop.entities.User;
import com.tauru.shop.services.OrderService;
import com.tauru.shop.services.RolesService;
import com.tauru.shop.services.UserService;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import com.tauru.shop.utilitare.UserUtil;
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

    @Autowired
    private OrderService orderService;

    private static final String EMAIL_ERROR = "emailError";
    private static final String EXISTING_USER = "existingUser";
    private static final String LOGGED_USER = "loggedUser";
    private static final String UNCONFIRMED_ORDERS = "unconfirmedOrders";
    private static final String NUMBER_OF_ORDERS_CONFIRMED_BY_ADMIN = "numberOfOrdersConfirmedByAdmin";
    private static final String PASSWORD_ERROR = "passError";

    @RequestMapping("/")
    public String viewHome() {

        return "index";
    }

    @RequestMapping("/register")
    public String viewLogin(String username, String password, String email, Model model) throws BullShopError {

        User user = new User();
        boolean checkEmail = userService.checkEmailAvailability(email);

        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password) || StringUtils.isNullOrEmpty(email)) {

            return "register";
        }

        User searchUser = userService.findUserByUsername(username);
        Roles userRole = rolesService.findRoleById((long) 2);

        if (searchUser == null) {
            user.setUsername(username);

            if (checkEmail) {
                user.setEmail(email);

            } else {

                model.addAttribute(EMAIL_ERROR,"Email is already used.");
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

            model.addAttribute(EXISTING_USER,"Username already used. Please chose another one.");
            return "register";
        }
    }

    @RequestMapping("/welcome")
    public String loginView(String username, String password, Model model, HttpSession session, HttpServletRequest request) throws BullShopError {

        session = request.getSession(true);
        Roles adminRole = rolesService.findRoleById((long) 1);
        Roles userRole = rolesService.findRoleById((long) 2);

        if ((User) session.getAttribute(LOGGED_USER) != null) {
            return "welcome";
        }

        User checkUser = userService.findUserByUsername(username);
        if (checkUser != null && checkUser.getRoles() == null) {

            if (UserUtil.isAdminUser(checkUser)) {
                checkUser.setRoles(adminRole);
            } else {
                checkUser.setRoles(userRole);
            }
        }

        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
            return "login";
        }

        if (username.equals("Admin123!@#") && password.equals("TauruMihai95")) {

            if (checkUser.getRoles() == null) {
                checkUser.setRoles(adminRole);
            }

            userService.saveUser(checkUser);
            Integer numberOfOrdersConfirmedByAdmin = 0;
            Integer unconfirmedOrders = 0;
            for (Order order : orderService.findAllOrders()) {
                if (order.getOrderIsProcessed()){
                    numberOfOrdersConfirmedByAdmin ++;

                } else {
                    unconfirmedOrders ++;
                }
            }
            model.addAttribute(NUMBER_OF_ORDERS_CONFIRMED_BY_ADMIN, numberOfOrdersConfirmedByAdmin);
            model.addAttribute(UNCONFIRMED_ORDERS, unconfirmedOrders);
            
            session.setAttribute(LOGGED_USER, checkUser);
            return "adminView";
        }

        if (checkUser != null && checkUser.getRoles().equals(userRole)) {

            session.setAttribute(LOGGED_USER, checkUser);

            if (password.equals(checkUser.getPassword())) {
                session.setAttribute(LOGGED_USER, checkUser);
                return "welcome";

            } else {

                model.addAttribute(PASSWORD_ERROR, "Parola introdusa este incorecta. Incercati din nou!");
                return "login";
            }
        }
        return "login";
    }
}
