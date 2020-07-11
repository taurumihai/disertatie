package com.tauru.shop.services;

import com.tauru.shop.entities.Product;
import com.tauru.shop.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {


    @Autowired
    private ProductRepository productRepository;


    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    public Product findProductById(Long productId) {

        return productRepository.findProductById(productId);
    }

    public void saveProduct(Product product) {

        if (product != null) {

            productRepository.save(product);
        }
    }

    public List<Product> getAllProductsWithoutStock(){

        List<Product> productList = new ArrayList<>();

        for (Product actualProduct : productRepository.findAll()) {
            if (actualProduct.getStockNumber() == 0) {
                productList.add(actualProduct);
            }
        }

        return productList;
    }
}
