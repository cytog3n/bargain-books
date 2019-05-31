package com.cyto.bargainbooks.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Book {

    private String author;

    private String title;

    @SerializedName("isbn")
    private String ISBN;

    private Long originalPrice;

    private Long newPrice;

    private String url;

    private String store;

    private Long salePercent;

    private Date lastUpdateDate;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Long newPrice) {
        this.newPrice = newPrice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Long getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(Long salePercent) {
        this.salePercent = salePercent;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString() {
        return "Book [author=" + author + ", title=" + title + "]";
    }

}
