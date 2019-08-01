package com.enemai.model;

public class infoPermissao {

    private int isAllowed;
    private int payed;
    private int clickInLink;
    private String tokenPurchase;

    public String getTokenPurchase() {
        return tokenPurchase;
    }

    public void setTokenPurchase(String tokenPurchase) {
        this.tokenPurchase = tokenPurchase;
    }

    public int getIsAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(int isAllowed) {
        this.isAllowed = isAllowed;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public int getClickInLink() {
        return clickInLink;
    }

    public void setClickInLink(int clickInLink) {
        this.clickInLink = clickInLink;
    }
}
