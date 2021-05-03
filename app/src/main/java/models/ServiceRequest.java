package models;

import java.util.Date;

import utils.Reusable;

public class ServiceRequest {
    private String serviceName;
    private String serviceId;
    private String icon;
    private String docId;
    private String fName;
    private String lName;
    private String userId;
    private String phone;
    private String address;
    private String description;
    private boolean seen;
    private long deliveryTime;
    private long createdAt;

    public ServiceRequest(){}

    public ServiceRequest(String serviceName, String serviceId, String icon, String docId, String fName, String lName, String userId,
                          String phone, String address, String description, long deliveryTime){
        this.serviceName = serviceName;
        this.icon = icon;
        this.docId = docId;
        this.serviceId = serviceId;
        this.fName = fName;
        this.lName = lName;
        this.userId = userId;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.deliveryTime = deliveryTime;
        this.createdAt = new Date().getTime();
    }

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
}
