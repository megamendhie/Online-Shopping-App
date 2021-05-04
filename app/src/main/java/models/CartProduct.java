package models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity (tableName = "cart_table")
public class CartProduct {
    @PrimaryKey
    @NonNull
    private String docId;
    private String name;
    private String icon;
    private String categoryId;
    private String size;
    private long price;
    private long createdAt;
    private int quantity;

    public CartProduct(Product product){
        this.name = product.getName();
        this.icon = product.getIcon();
        this.docId = product.getDocId();
        this.categoryId = product.getCategoryId();
        this.size = product.getSize();
        this.price = product.getPrice();
        this.createdAt = new Date().getTime();
        this.quantity = 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof CartProduct){
            CartProduct cartProduct = (CartProduct) obj;
            return this.getDocId().equals(cartProduct.getDocId());
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
