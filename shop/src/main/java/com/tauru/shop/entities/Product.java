package com.tauru.shop.entities;


import com.tauru.shop.enums.ProductCategoryEnum;
import javax.persistence.*;

@Table(name = "products")
@Entity
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name =  "price")
    private Double price;

    @Column(name = "details")
    private String details;

    @Column(name = "category")
    private ProductCategoryEnum productCategoryEnum;

    @Column(name = "stock")
    private Integer stockNumber;

    public Integer getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(Integer stockNumber) {
        this.stockNumber = stockNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ProductCategoryEnum getProductCategoryEnum() {
        return productCategoryEnum;
    }

    public void setProductCategoryEnum(ProductCategoryEnum productCategoryEnum) {
        this.productCategoryEnum = productCategoryEnum;
    }

    public Product(String name, Double price, Integer stockNumber, ProductCategoryEnum category){

        this.name = name;
        this.price = price;
        this.stockNumber = stockNumber;
        this.productCategoryEnum = category;
    }

    public Product() {

    }
}
