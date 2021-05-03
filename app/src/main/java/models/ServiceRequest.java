package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import static models.Commons.SERVICE;

public class ServiceRequest implements Parcelable {
    private String serviceName;
    private String serviceId;
    private String icon;
    private String docId;
    private String fName;
    private String lName;
    private String userId;
    private String phone;
    private String email;
    private String address;
    private String description;
    private String type;
    private boolean seen;
    private int status;
    private long deliveryTime;
    private long createdAt;

    public ServiceRequest(){}

    public ServiceRequest(String serviceName, String serviceId, String icon, String docId, String fName, String lName, String userId,
                          String phone, String email, String address, String description, long deliveryTime){
        this.serviceName = serviceName;
        this.icon = icon;
        this.docId = docId;
        this.serviceId = serviceId;
        this.fName = fName;
        this.lName = lName;
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.description = description;
        this.deliveryTime = deliveryTime;
        this.createdAt = new Date().getTime();
        this.type = SERVICE;
    }

    protected ServiceRequest(Parcel in) {
        serviceName = in.readString();
        serviceId = in.readString();
        icon = in.readString();
        docId = in.readString();
        fName = in.readString();
        lName = in.readString();
        userId = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        description = in.readString();
        type = in.readString();
        seen = in.readByte() != 0;
        status = in.readInt();
        deliveryTime = in.readLong();
        createdAt = in.readLong();
    }

    public static final Creator<ServiceRequest> CREATOR = new Creator<ServiceRequest>() {
        @Override
        public ServiceRequest createFromParcel(Parcel in) {
            return new ServiceRequest(in);
        }

        @Override
        public ServiceRequest[] newArray(int size) {
            return new ServiceRequest[size];
        }
    };

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceName);
        dest.writeString(serviceId);
        dest.writeString(icon);
        dest.writeString(docId);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(userId);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeByte((byte) (seen ? 1 : 0));
        dest.writeInt(status);
        dest.writeLong(deliveryTime);
        dest.writeLong(createdAt);
    }
}
