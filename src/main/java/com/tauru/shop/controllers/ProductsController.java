package com.tauru.shop.controllers;


import com.tauru.shop.entities.Product;
import com.tauru.shop.enums.ProductCategoryEnum;
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
public class ProductsController {

    @Autowired
    private ProductService productService;

    private static final String ALL_PRODUCTS_LIST = "allProductsLits";
    private static final String CURRENT_PRODUCT = "currentProduct";
    private static final String PRODUCT_LIST ="productList";
    private static final String ALL_DIGITAL_PRODUCTS = "allDigitalsProducts";

    @RequestMapping("/protection")
    public String protectionMaterialView(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();


        List<Product> allProductsLits = new ArrayList<>();
        for (Product product : productService.getAllProducts()) {
            if (product.getProductCategoryEnum().equals(ProductCategoryEnum.CONSTRUCTII)) {
                allProductsLits.add(product);
            }
        }

        model.addAttribute(ALL_PRODUCTS_LIST, allProductsLits);

        return "protection";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/productDetails/{id}")
    public String getViewProductDetailsView(@PathVariable(name = "id") String productId, HttpSession session,
                                            Model model, HttpServletResponse response, HttpServletRequest request){


        List<Product> productCompleteList = (List<Product>) session.getAttribute(PRODUCT_LIST);
        if (productCompleteList == null) {
            productCompleteList = new ArrayList<>();
        }

        session = request.getSession(true);
        Product currentProduct = productService.findProductById(Long.valueOf(productId));

        if (currentProduct != null) {

            productCompleteList.add(currentProduct);
            model.addAttribute(CURRENT_PRODUCT, currentProduct);
        }

        session.setAttribute(PRODUCT_LIST, productCompleteList);

        return "productDetails";
    }

    @RequestMapping("/digitals")
    public String digitalsProductsView(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();

        List<Product> allDigitalsProducts = new ArrayList<>();
        for (Product product : productService.getAllProducts()) {
            if (product.getProductCategoryEnum().equals(ProductCategoryEnum.DIGITALE)) {
                allDigitalsProducts.add(product);
            }
        }

        model.addAttribute("allDigitalsProducts", allDigitalsProducts);

        return "digitals";
    }

    @RequestMapping("/electronics")
    public String electronicsProductsView(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();

        List<Product> allElectronicsProducts = new ArrayList<>();
        for (Product product : productService.getAllProducts()) {
            if (product.getProductCategoryEnum().equals(ProductCategoryEnum.ELECTRONICE)) {
                allElectronicsProducts.add(product);
            }
        }

        model.addAttribute(ALL_DIGITAL_PRODUCTS, allElectronicsProducts);

        return "electronics";
    }

}
