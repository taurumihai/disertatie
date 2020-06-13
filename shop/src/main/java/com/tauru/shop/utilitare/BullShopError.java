package com.tauru.shop.utilitare;

public class BullShopError extends Exception {


    public BullShopError(String error) {

        super(error);
    }

    public BullShopError(String error, Throwable throwable) {

        super(error, throwable);
    }
}
