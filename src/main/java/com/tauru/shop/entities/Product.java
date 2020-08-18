package com.tauru.shop.entities;


import com.tauru.shop.enums.ProductCategoryEnum;
import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

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

    @ManyToMany(mappedBy = "productList",
            cascade = {
                        CascadeType.PERSIST,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
                        CascadeType.REMOVE},
            fetch = FetchType.EAGER)
    private List<Order> order;

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

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

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        Product product = (Product) obj;

        return  product.name.equals(name) &&
                product.description.equals(description) &&
                product.price.equals(price) &&
                product.details.equals(details) &&
                product.productCategoryEnum.equals(productCategoryEnum) &&
                product.stockNumber.equals(stockNumber);
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 11 * result + name.hashCode();
        result = 11 * result + description.hashCode();
        result = 11 * result + price.hashCode();
        result = 11 * result + details.hashCode();
        result = 11 * result + productCategoryEnum.hashCode();
        result = 11 * result + stockNumber.hashCode();

        return result;
    }

    public static class ProductSortingComparator implements Comparator<Product> {

        @Override
        public int compare(Product firstProduct, Product secondProduct) {

            int nameComparator = firstProduct.getName().compareTo(secondProduct.getName());
            int priceComparator = firstProduct.getPrice().compareTo(secondProduct.getPrice());

            if (nameComparator == 0) {
                return ((priceComparator == 0) ? nameComparator : priceComparator);
            } else {
                return priceComparator;
            }
        }
    }
}
