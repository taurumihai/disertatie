package com.tauru.shop.services;

import com.tauru.shop.entities.Product;
import com.tauru.shop.repositories.ProductRepository;
import com.tauru.shop.utilitare.BullShopError;
import com.tauru.shop.utilitare.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

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

    public boolean checkStockForCurrentProductById(Long productId, Integer numberOfSameProductInCart) throws BullShopError {

        if (!StringUtils.isNullOrEmpty(String.valueOf(productId))) {

            Product currentProduct = productRepository.findProductById(productId);
            if (currentProduct != null && currentProduct.getStockNumber() - numberOfSameProductInCart > 0) {

                return true;
            } else {

                LOGGER.error("Not enought stock for this product !");
                return false;
            }

        } else {

            LOGGER.error("No such product !");
            throw new BullShopError("Invalid productId while checking for stock! ProductId null or empty, productId = " + productId);
        }
    }

    @Transactional
    public void checkAndActualizeStockForProductsInFinalizeOrder(Long productId) throws BullShopError {

        if (!StringUtils.isNullOrEmpty(String.valueOf(productId))) {

            Product currentProduct = productRepository.findProductById(productId);
            if (currentProduct != null && currentProduct.getStockNumber() - 1 >= 0) {
                currentProduct.setStockNumber(currentProduct.getStockNumber() - 1);
                productRepository.save(currentProduct);
            } else {

                LOGGER.error("Not enought stock for this product !");
                throw new BullShopError("Not enought stock for this product with productId = " + productId);
            }
        } else {
            LOGGER.error("No such product !");
            throw new BullShopError("Invalid productId while checking for stock! ProductId null or empty, productId = " + productId);
        }
    }
}
