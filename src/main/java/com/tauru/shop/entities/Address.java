package com.tauru.shop.entities;

import javax.persistence.*;

@Entity
@Table(name = "users_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "zip_code")
    private Integer zipCode;

    @Column(name = "shipping_address_same_as_billing")
    private Boolean shippingAddressSameAsBilling = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public Boolean getShippingAddressSameAsBilling() {
        return shippingAddressSameAsBilling;
    }

    public void setShippingAddressSameAsBilling(Boolean shippingAddressSameAsBilling) {
        this.shippingAddressSameAsBilling = shippingAddressSameAsBilling;
    }
}
