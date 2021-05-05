package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import models.ServiceRequest;
import utils.FirebaseUtil;

import static models.Commons.REQUESTS;
import static models.Commons.SERVICE;

public class ServiceRequestDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnPending, btnDelivered, btnCancel;
    TextView txtServiceName, txtName, txtPhone, txtDeliveryDate, txtDate, txtAddress, txtEmail, txtDescription, txtStatus;
    ImageView imgStatus, imgPhone, imgEmail, imgIcon;

    ServiceRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Service Details");

        btnPending = findViewById(R.id.btnPending); btnPending.setOnClickListener(this);
        btnDelivered = findViewById(R.id.btnDelivered); btnDelivered.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel); btnCancel.setOnClickListener(this);

        txtServiceName = findViewById(R.id.txtServiceName);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtDeliveryDate = findViewById(R.id.txtDeliveryDate);
        txtDate = findViewById(R.id.txtDate);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtDescription = findViewById(R.id.txtDescription);
        txtStatus = findViewById(R.id.txtStatus);

        imgStatus = findViewById(R.id.imgStatus);
        imgPhone = findViewById(R.id.imgPhone); imgPhone.setOnClickListener(this);
        imgEmail = findViewById(R.id.imgEmail); imgEmail.setOnClickListener(this);
        imgIcon = findViewById(R.id.imgIcon);

        request = getIntent().getParcelableExtra(SERVICE);
        loadRequestDetails();
        FirebaseUtil.getDatabase().collection(REQUESTS).document(request.getDocId()).addSnapshotListener((value, error) -> {
            if(value==null||!value.exists())
                return;

            request = value.toObject(ServiceRequest.class);
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
        txtServiceName.setText(request.getServiceName());
        txtName.setText(String.format("%s %s", request.getfName(), request.getlName()));
        txtDeliveryDate.setText(timeDelivery);
        txtDate.setText(timeRequested);
        txtEmail.setText(request.getEmail());
        txtPhone.setText(request.getPhone());
        txtAddress.setText(request.getAddress());
        txtDescription.setText(request.getDescription());
        Glide.with(this).load(request.getIcon()).into(imgIcon);
        setStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPending:
                if (request.getStatus() == 0) {
                    Toast.makeText(this, "Already pending", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(REQUESTS).document(request.getDocId()).update("status", 0);
                Snackbar.make(txtStatus, "SERVICE PENDING", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btnDelivered:
                if (request.getStatus() == 1) {
                    Toast.makeText(this, "Already delivered", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(REQUESTS).document(request.getDocId()).update("status", 1);
                Snackbar.make(txtStatus, "SERVICE DELIVERED", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btnCancel:
                if (request.getStatus() == 2) {
                    Toast.makeText(this, "Already cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtil.getDatabase().collection(REQUESTS).document(request.getDocId()).update("status", 2);
                Snackbar.make(txtStatus, "SERVICE CANCELLED", Snackbar.LENGTH_SHORT).show();
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