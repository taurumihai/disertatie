package com.tauru.shop.controllers;


import com.tauru.shop.entities.Order;
import com.tauru.shop.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = {"/viewOrders"})
    public String viewOrdersView(Model model, String processCommand) {

        List<Order> unprocessedOrders = orderService.getAllUnprocessedOrders();

        if (unprocessedOrders != null) {
            model.addAttribute("unprocessedOrders", unprocessedOrders);
        }


        return "viewOrders";
    }

    @RequestMapping(value = "/confirmOrder/{id}")
    public String confirmOrderView(@PathVariable(name = "id") String orderId, Model model, HttpSession session, HttpServletRequest request, String processCommand) {

        session = request.getSession(true);
        Order currentOrder = orderService.findOrderById(Long.valueOf(orderId));

        if (currentOrder != null) {
            model.addAttribute("currentOrder", currentOrder);
        }

        if (currentOrder != null && processCommand != null && processCommand.equals("on")) {

            currentOrder.setOrderIsProcessed(Boolean.TRUE);
            orderService.saveOrder(currentOrder);
        }

        session.setAttribute("completeOrder", currentOrder);
        model.addAttribute("orderCompletedAlready", currentOrder.getOrderIsProcessed());

        return "confirmOrder";
    }

    @RequestMapping(value = "/adminView")
    public String welcomeView(HttpServletRequest request, String processCommand, Model model){

        HttpSession session = request.getSession(true);
        Boolean orderCheckedByAdmin = Boolean.FALSE;
        Order confirmOrder = (Order) session.getAttribute("completeOrder");

        if (processCommand != null && processCommand.equals("on")) {
            orderCheckedByAdmin = Boolean.TRUE;
        }

        model.addAttribute("orderCompletedAlready", Boolean.FALSE);
        if (confirmOrder != null && confirmOrder.getOrderIsProcessed()) {

            model.addAttribute("orderCompletedAlready", Boolean.TRUE);
        }

        if (confirmOrder != null &&  orderCheckedByAdmin) {

            confirmOrder.setOrderIsProcessed(Boolean.TRUE);
            orderService.saveOrder(confirmOrder);
        }


        return "adminView";
    }
}
