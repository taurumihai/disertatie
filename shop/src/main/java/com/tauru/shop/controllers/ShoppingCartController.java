package com.tauru.shop.controllers;


import com.tauru.shop.entities.*;
import com.tauru.shop.services.*;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
public class ShoppingCartController {

    private List<Product> productList;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = {"/shoppingCart", "/address", "/completeOrder"})
    public String shoppingCartView( Model model, HttpServletRequest request,
                                    String fullName, String email,
                                    String deliveryAddress, String county,
                                    String city, String zipCode, String sameadr) throws BullShopError {

        HttpSession session;
        Address userAddress = new Address();
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute("productList");

        session.setAttribute("productList",productList );
        model.addAttribute("productList", productList);
        User loggedUser = (User) session.getAttribute("loggedUser");
        Address checkAddress = new Address();

        if (loggedUser != null && loggedUser.getRoles() != rolesService.findRoleById((long) 2)) {
            checkAddress = addressService.findAddressByUserId(loggedUser.getId());
        }

        if (loggedUser != null) {

            if (StringUtils.isNullOrEmpty(loggedUser.getLastName()) && StringUtils.isNullOrEmpty(loggedUser.getFirstName())){

                if (fullName != null) {
                    String [] userNameSplitted = fullName.split(" ");
                    loggedUser.setFirstName(userNameSplitted[0]);
                    loggedUser.setLastName(userNameSplitted[1]);
                    userService.saveUser(loggedUser);
                }
            }
        }

        Double totalPriceForProducts = 0.0;
        if (productList != null) {

            for (Product product : productList) {
                totalPriceForProducts += product.getPrice();

            }
            model.addAttribute("totalPrice", totalPriceForProducts);
        }

        if (StringUtils.isNullOrEmpty(deliveryAddress) || StringUtils.isNullOrEmpty(county) ||
                StringUtils.isNullOrEmpty(city) || StringUtils.isNullOrEmpty(zipCode) || StringUtils.isNullOrEmpty(email)){

            model.addAttribute("deliveryAddressError", "Completati toate campurile");
            return "shoppingCart";

        } else {

            if (checkAddress == null) {
                userAddress.setCity(city);
                userAddress.setCounty(county);
                userAddress.setStreet(deliveryAddress);
                userAddress.setZipCode(Integer.valueOf(zipCode));
                userAddress.setShippingAddressSameAsBilling(Boolean.valueOf(sameadr));
                userAddress.setUser(loggedUser);
                addressService.saveAddress(userAddress);
                loggedUser.setAddressList(Arrays.asList(userAddress));
                userService.saveUser(loggedUser);
            }
        }

        Order order = new Order();

        order.setCity(city);
        order.setCounty(county);
        order.setEmail(email);
        order.setFirstName(loggedUser.getFirstName());
        order.setLastName(loggedUser.getLastName());
        order.setStreet(deliveryAddress);
        order.setZipCode(Integer.valueOf(zipCode));
        order.setOrderIsProcessed(Boolean.FALSE);

        String orderName = "";
        for (Product product : productList) {
            orderName += product.getName() + " ";
        }

        order.setName(orderName);
        order.setTolalOrderPrice(totalPriceForProducts);

        session.setAttribute("currentOrder", order);
        model.addAttribute("hideSendOrder", false);

        return "completeOrder";
    }

    @RequestMapping("/finalizeOrder")
    public String finalizeOrderView(Model model, HttpServletRequest request) {


        HttpSession session;
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute("productList");

        Order currentOrder = (Order) session.getAttribute("currentOrder");

        model.addAttribute("productList", productList);

        Double totalPriceForProducts = 0.0;
        if (productList != null) {

            for (Product product : productList) {
                totalPriceForProducts += product.getPrice();
                Integer currentStock = product.getStockNumber();
                product.setStockNumber(currentStock - 1);
                productService.saveProduct(product);
            }
            model.addAttribute("totalPrice", totalPriceForProducts);
        }

        orderService.saveOrder(currentOrder);

        model.addAttribute("hideSendOrder", true);
        model.addAttribute("orderProcesed", "Va multumim pentru comanda !");

        return "completeOrder";
    }


}
