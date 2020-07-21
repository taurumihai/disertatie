package com.tauru.shop.controllers;


import com.tauru.shop.entities.Order;
import com.tauru.shop.entities.Product;
import com.tauru.shop.enums.ProductCategoryEnum;
import com.tauru.shop.services.OrderService;
import com.tauru.shop.services.ProductService;
import com.tauru.shop.utilitare.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    private static final String UNPROCESSED_ORDERS = "unprocessedOrders";
    private static final String CURRENT_ORDER = "currentOrder";
    private static final String COMPLETE_ORDER = "completeOrder";
    private static final String ORDER_COMPLETED_ALREADY = "orderCompletedAlready";
    private static final String NUMBER_OF_ORDERS_CONFIRMED_BY_ADMIN = "numberOfOrdersConfirmedByAdmin";
    private static final String UNCONFIRMED_ORDERS = "unconfirmedOrders";
    private static final String MISSING_FIELDS = "missingFields";
    private static final String PRODUCTS_WITHOUT_STOCK = "productsWithoutStock";
    private static final String CURRENT_PRODUCT = "currentProduct";
    private static final String PRODUCT_TO_BE_UPDATED = "productToBeUpdated";
    private static final String EMPTY_FIELDS = "emptyFields";

    @RequestMapping(value = {"/viewOrders"})
    public String viewOrdersView(Model model, String processCommand) {

        List<Order> unprocessedOrders = orderService.getAllUnprocessedOrders();

        if (unprocessedOrders != null) {
            model.addAttribute(UNPROCESSED_ORDERS, unprocessedOrders);
        }

        return "viewOrders";
    }

    @RequestMapping(value = "/confirmOrder/{id}")
    public String confirmOrderView(@PathVariable(name = "id") String orderId,
                                   Model model, HttpSession session,
                                   HttpServletRequest request,
                                   String processCommand) {

        session = request.getSession(true);
        Order currentOrder = orderService.findOrderById(Long.valueOf(orderId));

        if (currentOrder != null) {
            model.addAttribute(CURRENT_ORDER, currentOrder);
        }

        if (currentOrder != null && processCommand != null && processCommand.equals("on")) {

            currentOrder.setOrderIsProcessed(Boolean.TRUE);
            orderService.saveOrder(currentOrder);
        }

        session.setAttribute(COMPLETE_ORDER, currentOrder);
        model.addAttribute(ORDER_COMPLETED_ALREADY, currentOrder.getOrderIsProcessed());

        return "confirmOrder";
    }

    @RequestMapping(value = "/adminView")
    public String welcomeView(HttpSession session, HttpServletRequest request, String processCommand, Model model, String updateStock){


        session = request.getSession(true);

        if (StringUtils.isNullOrEmpty(updateStock)) {
            model.addAttribute(EMPTY_FIELDS,"Va rugam completati toate campurile!");
        }

        Product productToBeUpdated = (Product) session.getAttribute(PRODUCT_TO_BE_UPDATED);

        if (!StringUtils.isNullOrEmpty(updateStock) && productToBeUpdated != null) {
            productToBeUpdated.setStockNumber(Integer.valueOf(updateStock));
            productService.saveProduct(productToBeUpdated);
        }

        Boolean orderCheckedByAdmin = Boolean.FALSE;
        Order confirmOrder = (Order) session.getAttribute(COMPLETE_ORDER);

        if (processCommand != null && processCommand.equals("on")) {
            orderCheckedByAdmin = Boolean.TRUE;
        }

        model.addAttribute(ORDER_COMPLETED_ALREADY, Boolean.FALSE);
        if (confirmOrder != null && confirmOrder.getOrderIsProcessed()) {

            model.addAttribute(ORDER_COMPLETED_ALREADY, Boolean.TRUE);
        }

        if (confirmOrder != null &&  orderCheckedByAdmin) {

            confirmOrder.setOrderIsProcessed(Boolean.TRUE);
            orderService.saveOrder(confirmOrder);
        }

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

        return "adminView";
    }

    @RequestMapping(value = {"/addProducts"})
    public String addProductsView(String category, String productName,
                                  String description, String details,
                                  String price, String stock, Model model) {

        if (StringUtils.isNullOrEmpty(productName) || StringUtils.isNullOrEmpty(description) || StringUtils.isNullOrEmpty(details)
                || StringUtils.isNullOrEmpty(price) || StringUtils.isNullOrEmpty(stock)) {

            model.addAttribute(MISSING_FIELDS, "Completati toate campurile");
            return "addProducts";
        }

        Product newProduct = new Product(productName, Double.parseDouble(price), Integer.parseInt(stock), ProductCategoryEnum.valueOf(category));
        newProduct.setDescription(description);
        newProduct.setDetails(details);

        productService.saveProduct(newProduct);

        return "addProducts";
    }

    @RequestMapping("/viewStock")
    public String updateStockView(Model model) {

        List<Product> productsWithoutStocks = productService.getAllProductsWithoutStock();
        model.addAttribute(PRODUCTS_WITHOUT_STOCK, productsWithoutStocks);

        return "viewStock";
    }

    @RequestMapping("/stockUpdate/{id}")
    public String stockUpdateView(@PathVariable(name = "id") String productId, Model model,
                                  HttpSession session, HttpServletRequest request) {

        session = request.getSession(true);
        Product currentProduct = productService.findProductById(Long.valueOf(productId));

        if (currentProduct != null) {
            model.addAttribute(CURRENT_PRODUCT, currentProduct);
        }

        session.setAttribute(PRODUCT_TO_BE_UPDATED, currentProduct);

        return "stockUpdate";
    }
}
