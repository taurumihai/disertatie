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
import java.util.*;

@Controller
public class ShoppingCartController {

    Logger LOGGER = LoggerFactory.getLogger(ShoppingCartController.class);

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
    private static final String EMPTY_CART = "emptyCart";
    private static final String INSUFFICIENT_STOCK  = "insufficentStock";

    @SuppressWarnings("unchecked")
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

        // Sortare produse in cos dupa Numele produsului, apoi dupa pret
        Collections.sort(productList, new Product.ProductSortingComparator());

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

    @SuppressWarnings("unchecked")
    @RequestMapping("/finalizeOrder")
    public String finalizeOrderView(Model model, HttpServletRequest request) {


        HttpSession session;
        session = request.getSession(true);
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);
        Order currentOrder = (Order) session.getAttribute(CURRENT_ORDER);

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


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/productDetails/{id}", params = "action=remove")
    public String removeProductView(@PathVariable(name = "id") String productId, HttpServletRequest request){

        HttpSession session = request.getSession(true);

        Product currentProduct = productService.findProductById(Long.parseLong(productId));
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);

        List<Product> duplicateElements = findAllDuplicateProducts(productList, currentProduct);
        productList.removeAll(duplicateElements);
        duplicateElements.remove(0);
        productList.addAll(duplicateElements);

        session.setAttribute(PRODUCT_LIST, productList);

        return "redirect:/shoppingCart";
    }

    @SuppressWarnings("unchecked") /*Pentru obiecete pe care le iau de pe sessiune, sa nu ma mai anunte ca nu au fost verificate !!!!!!! se utilizeaza doar daca sunt sigur ca de pe sesiune o sa am mereu o lista de obiecte !!!!!!!!!!!!!!!!!!*/
    @RequestMapping(value = "/productDetails/{id}", params = "action=add")
    public String addProductView(@PathVariable(name = "id") String productId, HttpServletRequest request, Model model) throws BullShopError {

        HttpSession session = request.getSession(true);
        Product currentProduct = productService.findProductById(Long.parseLong(productId));
        productList = (List<Product>) session.getAttribute(PRODUCT_LIST);

        int numberOfSameProductInCart = 0;
        for (Product product : productList) {
            if (product.getId().equals(currentProduct.getId())) {
                numberOfSameProductInCart ++;
            }
        }

        if (currentProduct != null) {

            boolean checkProductByIdAndStock = productService.checkStockForCurrentProductById(currentProduct.getId(), numberOfSameProductInCart);

            if (checkProductByIdAndStock) {
                productList.add(currentProduct);

            } else {

                // pun pe sesiune erorile, le iau din controller, unde mai fac o verificare, apoi le arunc in template
                session.setAttribute(INSUFFICIENT_STOCK,"Nu exista suficient stoc pentru comandarea a mai mult de " + numberOfSameProductInCart + " produse " + currentProduct.getName());
            }
        }

        session.setAttribute(PRODUCT_LIST, productList);

        return "redirect:/shoppingCart";
    }

    private List<Product> findAllDuplicateProducts(List<Product> productList, Product duplicateProduct) {

        List<Product> duplicateProductList = new ArrayList<>();

        for (Product product : productList) {
            if (product.equals(duplicateProduct)) {
                duplicateProductList.add(duplicateProduct);
            }
        }

        return duplicateProductList;
    }
}
