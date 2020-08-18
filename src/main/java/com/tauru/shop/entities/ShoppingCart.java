package com.tauru.shop.entities;

import java.util.List;

public class ShoppingCart {


    private List<Product> productList;

    public ShoppingCart(List<Product> allProducts) {

        this.productList = allProducts;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
