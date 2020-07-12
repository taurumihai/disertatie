package com.tauru.shop.entities;

import java.util.List;

public class ShoppingCart {


    private String name;

    private String details;

    private String description;

    private Double price;

    private List<Product> productList;

    public ShoppingCart(String name, String details, String description, Double price) {

        this.name = name;
        this.details = details;
        this.description = description;
        this.price = price;
    }

    public ShoppingCart(){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
