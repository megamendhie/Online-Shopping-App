package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

import adapters.PurchasedProductsAdapter;
import models.CartProduct;
import models.ProductRequest;
import models.User;
import utils.FirebaseUtil;
import utils.Reusable;

import static models.Commons.PRODUCT;
import static models.Commons.PRODUCT_REQUESTS;
import static models.Commons.REQUESTS;

public class ProductRequestDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private final Gson gson = new Gson();
    private User myProfile;
    private TextView txtPurchaseDate, txtName, txtPhone, txtDeliveryDate, txtAddress, txtEmail, txtTotalPrice, txtStatus;
    private ImageView imgStatus;
    private RecyclerView lstProducts;

    ProductRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_request_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Purchase Details");

        Button btnPending = findViewById(R.id.btnPending);
        btnPending.setOnClickListener(this);
        Button btnDelivered = findViewById(R.id.btnDelivered);
        btnDelivered.setOnClickListener(this);
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        ConstraintLayout cnsAction = findViewById(R.id.cnsAction);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("profile", "");
        myProfile = (json.equals("")) ? null : gson.fromJson(json, User.class);

        txtName = findViewById(R.id.txtName);
        txtPurchaseDate = findViewById(R.id.txtPurchaseDate);
        txtDeliveryDate = findViewById(R.id.txtDeliveryDate);
        txtStatus = findViewById(R.id.txtStatus);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddress = findViewById(R.id.txtAddress);
        txtTotalPrice = findViewById(R.id.txtPrice);
        cnsAction.setVisibility(myProfile.isAdmin()? View.VISIBLE:View.GONE);

        lstProducts = findViewById(R.id.lstProduct);
        lstProducts.setLayoutManager(new LinearLayoutManager(this));

        imgStatus = findViewById(R.id.imgStatus);
        ImageView imgPhone = findViewById(R.id.imgPhone);
        imgPhone.setOnClickListener(this);
        ImageView imgEmail = findViewById(R.id.imgEmail);
        imgEmail.setOnClickListener(this);
        if(getIntent()==null){
            Toast.makeText(this, "Cannot retrieve purchase details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String jsonProduct = getIntent().getStringExtra(PRODUCT);
        request = gson.fromJson(jsonProduct, ProductRequest.class);

        loadRequestDetails();
        FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document(request.getDocId()).addSnapshotListener((value, error) -> {
            if(value==null||!value.exists())
                return;

            request = value.toObject(ProductRequest.class);
            setStatus();
        });
    }

    private void setStatus() {
        switch (request.getStatus()){
            case 0:
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getResources()
                        .getColor(R.color.colorStatusAmber)));
                txtStatus.setText("pending");
                break;
            case 1:
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getResources()
                        .getColor(R.color.colorPrimary)));
                txtStatus.setText("delivered");
                break;
            case 2:
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getResources()
                        .getColor(R.color.colorStatusRed)));
                txtStatus.setText("cancelled");
                break;
        }
    }

    private void loadRequestDetails() {
        String timeDelivery = DateFormat.format("d MMM, yyyy h:mma", request.getDeliveryTime()).toString();
        String timeRequested = DateFormat.format("d MMM, yyyy h:mma", request.getCreatedAt()).toString();
        txtName.setText(String.format("%s %s", request.getfName(), request.getlName()));
        txtDeliveryDate.setText(timeDelivery);
        txtPurchaseDate.setText(timeRequested);
        txtEmail.setText(request.getEmail());
        txtPhone.setText(request.getPhone());
        txtAddress.setText(request.getAddress());
        ArrayList<CartProduct> products = request.getProducts();
        txtTotalPrice.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(request.getTotalAmount())));
        setStatus();
        lstProducts.setAdapter(new PurchasedProductsAdapter(products));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPending:
                if (request.getStatus() == 0) {
                    Toast.makeText(this, "Already pending", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document(request.getDocId()).update("status", 0);
                Snackbar.make(txtStatus, "SERVICE PENDING", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btnDelivered:
                if (request.getStatus() == 1) {
                    Toast.makeText(this, "Already delivered", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document(request.getDocId()).update("status", 1);
                Snackbar.make(txtStatus, "SERVICE DELIVERED", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btnCancel:
                if (request.getStatus() == 2) {
                    Toast.makeText(this, "Already cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document(request.getDocId()).update("status", 2);
                Snackbar.make(txtStatus, "SERVICE CANCELLED", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.imgPhone:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", request.getPhone(), null));
                startActivity(intent);
                break;
            case R.id.imgEmail:
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied code", request.getEmail());
                manager.setPrimaryClip(clip);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}