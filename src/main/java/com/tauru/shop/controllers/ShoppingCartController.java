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

    private static final String PRODUCT_LIST = "productList";
    private static final String TOTAL_PRICE_FOR_PRODUCTS = "totalPrice";
    private static final String DELIVERY_ADDRESS_ERROR = "deliveryAddressError";
    private static final String CURRENT_ORDER = "currentOrder";
    private static final String HIDE_SEND_ORDER = "hideSendOrder";
    private static final String ORDER_RROCESED = "orderProcesed";

    @RequestMapping(value = {"/shoppingCart", "/address", "/completeOrder"})
    public String shoppingCartView( Model model, HttpServletRequest request,
                                    String fullName, String email,
                                    String deliveryAddress, String county,
                                    String city, String zipCode, String sameadr,
                                    String billingAddress, String billingCity, String billingCounty, String billingZipCode ) throws BullShopError {

        HttpSession session;
        Address userAddress = new Address();
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);

        session.setAttribute(PRODUCT_LIST, productList );
        model.addAttribute(PRODUCT_LIST, productList);
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
            model.addAttribute(TOTAL_PRICE_FOR_PRODUCTS, totalPriceForProducts);
        }


        if (StringUtils.isNullOrEmpty(deliveryAddress) || StringUtils.isNullOrEmpty(county) ||
                StringUtils.isNullOrEmpty(city) || StringUtils.isNullOrEmpty(zipCode) || StringUtils.isNullOrEmpty(email)){

            model.addAttribute(DELIVERY_ADDRESS_ERROR, "Completati toate campurile");
            return "shoppingCart";


        } else if (sameadr == null && (StringUtils.isNullOrEmpty(billingAddress) ||
                StringUtils.isNullOrEmpty(billingCounty ) || StringUtils.isNullOrEmpty(billingCity) || StringUtils.isNullOrEmpty(billingZipCode))){

            model.addAttribute(DELIVERY_ADDRESS_ERROR, "Completati toate campurile");
            return "shoppingCart";

        } else {

            if (checkAddress == null) {
                userAddress.setCity(city);
                userAddress.setCounty(county);
                userAddress.setStreet(deliveryAddress);
                userAddress.setZipCode(Integer.valueOf(zipCode));
                userAddress.setUser(loggedUser);
                addressService.saveAddress(userAddress);
                loggedUser.setAddressList(Arrays.asList(userAddress));
                userService.saveUser(loggedUser);
            }
        }

        Order order = new Order();
        order.setEmail(email);
        order.setFirstName(loggedUser.getFirstName());
        order.setLastName(loggedUser.getLastName());

        //construiesc adresa userului din street + zipcode + city + county
        String delAddress = deliveryAddress + " " + zipCode + " " + city + " " + county;

        if (sameadr == null) {
            String billAddress = billingAddress + " " + billingZipCode + " " + billingCity + " " + billingCounty;
            order.setBillingAddress(billAddress);
            order.setDeliveryAddress(delAddress);

        } else {

            order.setDeliveryAddress(delAddress);
            order.setBillingAddress(delAddress);
        }
        order.setOrderIsProcessed(Boolean.FALSE);

        String orderName = "";
        for (Product product : productList) {
            orderName += product.getName() + " ";
        }

        order.setName(orderName);
        order.setTotalOrderPrice(totalPriceForProducts);

        session.setAttribute( CURRENT_ORDER, order);
        model.addAttribute(HIDE_SEND_ORDER, false);

        return "completeOrder";
    }

    @RequestMapping("/finalizeOrder")
    public String finalizeOrderView(Model model, HttpServletRequest request) {


        HttpSession session;
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute("productList");

        Order currentOrder = (Order) session.getAttribute("currentOrder");

        model.addAttribute(PRODUCT_LIST, productList);

        Double totalPriceForProducts = 0.0;
        if (productList != null) {

            for (Product product : productList) {
                totalPriceForProducts += product.getPrice();
                Integer currentStock = product.getStockNumber();
                if (currentStock > 0) {
                    product.setStockNumber(currentStock - 1);
                }
                productService.saveProduct(product);
            }
            model.addAttribute(TOTAL_PRICE_FOR_PRODUCTS, totalPriceForProducts);
        }

        orderService.saveOrder(currentOrder);

        model.addAttribute(HIDE_SEND_ORDER, true);
        model.addAttribute(ORDER_RROCESED, "Va multumim pentru comanda !");
        session.removeAttribute(PRODUCT_LIST);

        return "completeOrder";
    }


}
