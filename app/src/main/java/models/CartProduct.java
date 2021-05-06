package models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity (tableName = "cart_table")
public class CartProduct implements Parcelable {
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

    public CartProduct(){}

    public CartProduct(Product product){
        this.name = product.getName();
        this.icon = product.getIcon();
        this.docId = product.getDocId();
        this.categoryId = product.getCategoryId();
        this.size = product.getSize();
        this.price = product.getPrice();
        this.createdAt = new Date().getTime();
        this.quantity = 1;
    }

    protected CartProduct(Parcel in) {
        docId = in.readString();
        name = in.readString();
        icon = in.readString();
        categoryId = in.readString();
        size = in.readString();
        price = in.readLong();
        createdAt = in.readLong();
        quantity = in.readInt();
    }

    public static final Creator<CartProduct> CREATOR = new Creator<CartProduct>() {
        @Override
        public CartProduct createFromParcel(Parcel in) {
            return new CartProduct(in);
        }

        @Override
        public CartProduct[] newArray(int size) {
            return new CartProduct[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(docId);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(categoryId);
        dest.writeString(size);
        dest.writeLong(price);
        dest.writeLong(createdAt);
        dest.writeInt(quantity);
    }
}
