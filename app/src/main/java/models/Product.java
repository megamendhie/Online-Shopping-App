package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Product implements Parcelable {
    private String name;
    private String icon;
    private String docId;
    private String categoryId;
    private String size;
    private boolean visible;
    private long price;
    private long createdAt;
    private long clicks;
    private int quantity;

    public Product(){}

    public Product(String name, String icon, String docId, String categoryId, String size, boolean visible, long price){
        this.name = name;
        this.icon = icon;
        this.docId = docId;
        this.categoryId = categoryId;
        this.size = size;
        this.visible = visible;
        this.price = price;
        this.createdAt = new Date().getTime();
    }

    protected Product(Parcel in) {
        name = in.readString();
        icon = in.readString();
        docId = in.readString();
        categoryId = in.readString();
        size = in.readString();
        visible = in.readByte() != 0;
        price = in.readLong();
        createdAt = in.readLong();
        clicks = in.readLong();
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(docId);
        dest.writeString(categoryId);
        dest.writeString(size);
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeLong(price);
        dest.writeLong(createdAt);
        dest.writeLong(clicks);
        dest.writeInt(quantity);
    }
}
