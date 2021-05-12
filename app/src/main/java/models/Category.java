package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Category implements Parcelable {
    private String name;
    private String icon;
    private String docId;
    private String type;
    private boolean visible;
    private long createdAt;
    private long clicks;

    public Category(){}

    public Category(String name, String icon, String docId, String type, boolean visible){
        this.name = name;
        this.icon = icon;
        this.docId = docId;
        this.type = type;
        this.visible = visible;
        this.createdAt = new Date().getTime();
    }

    private Category(Parcel in) {
        name = in.readString();
        icon = in.readString();
        docId = in.readString();
        type = in.readString();
        visible = in.readByte() != 0;
        createdAt = in.readLong();
        clicks = in.readLong();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(icon);
        parcel.writeString(docId);
        parcel.writeString(type);
        parcel.writeByte((byte) (visible? 1:0));
        parcel.writeLong(createdAt);
        parcel.writeLong(clicks);
    }
}
