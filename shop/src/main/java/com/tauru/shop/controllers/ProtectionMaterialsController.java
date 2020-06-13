package com.tauru.shop.controllers;


import com.tauru.shop.entities.Product;
import com.tauru.shop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProtectionMaterialsController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/protection")
    public String protectionMaterialView(Model model) {

        List<Product> allProductsLits = productService.getAllProducts();
        model.addAttribute("allProductsLits", allProductsLits);

        return "protection";
    }

    @RequestMapping(value = "/productDetails/{id}")
    public String getViewProductDetailsView(@PathVariable(name = "id") String productId, HttpSession session,
                                            Model model, HttpServletResponse response, HttpServletRequest request){


        List<Product> productCompleteList = (List<Product>) session.getAttribute("productList");
        if (productCompleteList == null) {
            productCompleteList = new ArrayList<>();
        }

        session = request.getSession(true);
        Product currentProduct = productService.findProductById(Long.valueOf(productId));

        if (currentProduct != null) {

            productCompleteList.add(currentProduct);
            model.addAttribute("currentProduct", currentProduct);
        }

        session.setAttribute("productList", productCompleteList);

        return "productDetails";
    }

}
