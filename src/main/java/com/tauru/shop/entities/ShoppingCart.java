package com.tauru.shop.entities;

import java.util.List;

public class ShoppingCart {


    private List<Product> productList;

    private Double totalPrice = 0.0;

    public ShoppingCart(List<Product> allProducts) {

        this.productList = allProducts;
    }

    public ShoppingCart(){

    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
