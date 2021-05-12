package models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static models.Commons.PRODUCT;

public class ProductRequest{
    private ArrayList<CartProduct> products = new ArrayList<>();
    private String docId;
    private String fName;
    private String lName;
    private String userId;
    private String phone;
    private String email;
    private String address;
    private String type;
    private boolean seen;
    private int status;
    private long deliveryTime;
    private long createdAt;
    private long totalAmount;

    public ProductRequest(){}

    public ProductRequest(ArrayList<CartProduct> products, String docId, String fName, String lName, String userId,
                          String phone, String email, String address, long deliveryTime){
        this.products = products;
        this.docId = docId;
        this.fName = fName;
        this.lName = lName;
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.createdAt = new Date().getTime();
        this.type = PRODUCT;

        if(products==null||products.isEmpty())
            return;
        for (CartProduct product:products){
            totalAmount+=(product.getPrice()*product.getQuantity());
        }
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ArrayList<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<CartProduct> products) {
        this.products = products;
    }
}
