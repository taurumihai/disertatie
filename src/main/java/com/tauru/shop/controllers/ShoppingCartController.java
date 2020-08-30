package com.tauru.shop.controllers;


import com.tauru.shop.entities.*;
import com.tauru.shop.services.*;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;

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

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ShoppingCartController.class.getName());

    private static final String PRODUCT_LIST = "productList";
    private static final String TOTAL_PRICE_FOR_PRODUCTS = "totalPrice";
    private static final String DELIVERY_ADDRESS_ERROR = "deliveryAddressError";
    private static final String CURRENT_ORDER = "currentOrder";
    private static final String HIDE_SEND_ORDER = "hideSendOrder";
    private static final String ORDER_RROCESED = "orderProcesed";
    private static final String EMPTY_CART = "emptyCart";
    private static final String INSUFFICIENT_STOCK  = "insufficentStock";
    private static final String TOTAL_NUMBER_OF_PRODUCTS = "totalNumberOfProducts";
    private static final String SHOPPING_CART_ITEMS = "shoppingCartItems";
    private static final String NO_PRODUCTS = "noProducts";

    @SuppressWarnings("unchecked")
    @RequestMapping("/shoppingCart")
    public String shoppingCartView(Model model, HttpServletRequest request) throws BullShopError {

        HttpSession session;
        session = request.getSession(true);
        ShoppingCart shoppingCart= new ShoppingCart();

        /*
         * Mesaje de eroare care pot aparea cand stergi/adaugi produse in cos din cart
         * view-urile respective nu au asociate niciun template si nu pot afisa mesaje de eroare decat daca le pun pe sesiune
         * mesajele de eroare sunt puse pe sesiune si aruncate de controller in browser
         *
         * */
        if ((session.getAttribute(PRODUCT_LIST)) != null && ((List<Product>) session.getAttribute(PRODUCT_LIST)).isEmpty()) {

            model.addAttribute(EMPTY_CART, "Nu aveti niciun produs in cos!");
        }

        if ((session.getAttribute(INSUFFICIENT_STOCK)) != null) {

            model.addAttribute(INSUFFICIENT_STOCK, session.getAttribute(INSUFFICIENT_STOCK));
        }

        if ((session.getAttribute(NO_PRODUCTS)) != null) {

            model.addAttribute(NO_PRODUCTS, session.getAttribute(NO_PRODUCTS));
        }

        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);

        if ((session.getAttribute(SHOPPING_CART_ITEMS) != null)) {
            productList = (List<Product>) session.getAttribute(SHOPPING_CART_ITEMS);
        }

        shoppingCart.setProductList(productList);

        Double totalPriceForProducts = 0.0;
        if (shoppingCart.getProductList() != null) {

            for (Product product : shoppingCart.getProductList()) {
                totalPriceForProducts += product.getPrice();

            }
            model.addAttribute(TOTAL_PRICE_FOR_PRODUCTS, totalPriceForProducts);
        }

        model.addAttribute(PRODUCT_LIST, shoppingCart.getProductList());

        return "shoppingCart";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"/completeOrder"})
    public String formAddressView(Model model, HttpServletRequest request,
                                  String fullName, String email,
                                  String deliveryAddress, String county,
                                  String city, String zipCode, String sameadr,
                                  String billingAddress, String billingCity, String billingCounty, String billingZipCode ) throws BullShopError {

        HttpSession session;
        Address userAddress = new Address();
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setProductList(productList);

        model.addAttribute(PRODUCT_LIST, productList);
        User loggedUser = (User) session.getAttribute("loggedUser");
        Address checkAddress = new Address();

        if (loggedUser != null && loggedUser.getRoles() != rolesService.findRoleById((long) 2)) {
            checkAddress = addressService.findAddressByUserId(loggedUser.getId());
        }

        if (productList == null || productList.isEmpty()) {
            model.addAttribute(NO_PRODUCTS, "Nu puteti continua comanda deaorece nu aveti niciun produs in cos.");
            session.setAttribute(NO_PRODUCTS, model.getAttribute(NO_PRODUCTS));
            return "redirect:/shoppingCart";
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
                loggedUser.setAddressList( Collections.singletonList(userAddress));
                userService.saveUser(loggedUser);
            }
        }

        String firstName = "";
        String lastName = "";
        if (!StringUtils.isNullOrEmpty(fullName)) {
            String userName [] = fullName.split(" ");
            firstName = userName[0];
            lastName = userName[1];
        }

        Order order = new Order();
        order.setEmail(email);
        order.setFirstName(firstName);
        order.setLastName(lastName);

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

        session.setAttribute( CURRENT_ORDER, order);
        model.addAttribute(HIDE_SEND_ORDER, false);

        return "completeOrder";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/finalizeOrder")
    @Transactional
    public String finalizeOrderView(Model model, HttpServletRequest request) throws BullShopError {


        HttpSession session;
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute(SHOPPING_CART_ITEMS);
        Order currentOrder = (Order) session.getAttribute(CURRENT_ORDER);

        model.addAttribute(PRODUCT_LIST, productList);

        Double totalPriceForProducts = 0.0;
        if (productList != null) {

            for (Product product : productList) {
                productService.checkAndActualizeStockForProductsInFinalizeOrder(product.getId());
                totalPriceForProducts += product.getPrice();
            }
            currentOrder.setTotalOrderPrice(totalPriceForProducts);
            model.addAttribute(TOTAL_PRICE_FOR_PRODUCTS, totalPriceForProducts);
        }

        orderService.saveOrder(currentOrder);
        currentOrder.setOrderNumber(String.valueOf(currentOrder.getId()));
        currentOrder.setProductList(productList);

        orderService.saveOrder(currentOrder);

        model.addAttribute(HIDE_SEND_ORDER, true);
        model.addAttribute(ORDER_RROCESED, "Va multumim pentru comanda !");
        session.removeAttribute(PRODUCT_LIST);

        return "completeOrder";
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/productDetails/{id}", params = "action=remove")
    public String removeProductView(@PathVariable(name = "id") String productId, HttpServletRequest request) throws BullShopError {

        HttpSession session = request.getSession(true);

        Product currentProduct = productService.findProductById(Long.parseLong(productId));
        List<Product> shoppingCartList;
        Boolean firstItemInCart = Boolean.FALSE;

        if (session.getAttribute(PRODUCT_LIST) != null) {
            shoppingCartList = (List<Product>) session.getAttribute(PRODUCT_LIST);
            firstItemInCart = Boolean.TRUE;

        } else {
           shoppingCartList = (List<Product>) session.getAttribute(SHOPPING_CART_ITEMS);
        }
        List<Product> duplicateElements = findAllDuplicateProducts(shoppingCartList, currentProduct);

        if (duplicateElements != null || duplicateElements.isEmpty()) {
            shoppingCartList.removeAll(duplicateElements);
            duplicateElements.remove(0);
            shoppingCartList.addAll(duplicateElements);

        } else {

            throw new BullShopError("System error. Trying to delete products from a null or empty list.");
        }

        if (firstItemInCart) {
            session.setAttribute(PRODUCT_LIST, shoppingCartList);
        }
        session.setAttribute(SHOPPING_CART_ITEMS, shoppingCartList);

        return "redirect:/shoppingCart";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/productDetails/{id}", params = "action=add")
    public String addProductView(@PathVariable(name = "id") String productId, HttpServletRequest request, Model model) throws BullShopError {

        HttpSession session = request.getSession(true);
        Product currentProduct = productService.findProductById(Long.parseLong(productId));
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setProductList(productList);

        int numberOfSameProductInCart = 0;
        for (Product product : productList) {
            if (currentProduct != null && product.getId().equals(currentProduct.getId())) {
                numberOfSameProductInCart ++;
            }
        }

        if (currentProduct != null) {

            boolean checkProductByIdAndStock = productService.checkStockForCurrentProductById(currentProduct.getId(), numberOfSameProductInCart);

            if (checkProductByIdAndStock) {
                shoppingCart.getProductList().add(currentProduct);

            } else {
                // pun pe sesiune erorile, le iau din controller, unde mai fac o verificare, apoi le arunc in template
                session.setAttribute(INSUFFICIENT_STOCK,"Nu exista suficient stoc pentru comandarea a mai mult de " + numberOfSameProductInCart + " produse " + currentProduct.getName());
            }
        }

        session.setAttribute(SHOPPING_CART_ITEMS, shoppingCart.getProductList());
        session.setAttribute(TOTAL_NUMBER_OF_PRODUCTS, numberOfSameProductInCart);

        return "redirect:/shoppingCart";
    }

    private List<Product> findAllDuplicateProducts(List<Product> productList, Product duplicateProduct) {

        int totalNumberOfProducts = 1;
        List<Product> duplicateProductList = new ArrayList<>();

        for (Product product : productList) {
            if (product.equals(duplicateProduct)) {
                duplicateProductList.add(duplicateProduct);
                totalNumberOfProducts ++;
            }
        }

        return duplicateProductList;
    }
}
