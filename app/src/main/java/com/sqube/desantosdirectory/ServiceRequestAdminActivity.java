package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.firestore.Query;

import adapters.ProductOrderAdapter;
import adapters.ServiceRequestAdapter;
import interfaces.OnStatusChangeListener;
import modals.AddCategoryFragment;
import modals.FilterFragment;
import utils.FirebaseUtil;

import static models.Commons.CATEGORY_ID;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT_REQUESTS;
import static models.Commons.REQUESTS;
import static models.Commons.SERVICE;
import static models.Commons.SERVICES;
import static models.Commons.STATUS;
import static models.Commons.TYPE;

public class ServiceRequestAdminActivity extends AppCompatActivity implements OnStatusChangeListener {
    private RecyclerView lstRequest;
    private FilterFragment filterFragment;
    private String type;
    private int status = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        lstRequest = findViewById(R.id.lstRequest);
        lstRequest.setLayoutManager(new LinearLayoutManager(this));
        type = getIntent().getStringExtra(TYPE);
        actionBar.setTitle(type.equals(SERVICES)?"Service Request":"Product Orders");
        if (type.equals(SERVICES))
            loadRequests(status);
        else
            loadOrders(status);
    }


    private void loadRequests(int status) {
        Query query = status==3?FirebaseUtil.getDatabase().collection(REQUESTS).orderBy(CREATED_AT, Query.Direction.DESCENDING):
                FirebaseUtil.getDatabase().collection(REQUESTS).orderBy(CREATED_AT, Query.Direction.DESCENDING)
                        .whereEqualTo(STATUS, status);
        ServiceRequestAdapter adapter = new ServiceRequestAdapter(query, this);
        lstRequest.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadOrders(int status) {
        Query query = status==3?FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).orderBy(CREATED_AT, Query.Direction.DESCENDING):
                FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).orderBy(CREATED_AT, Query.Direction.DESCENDING)
                .whereEqualTo(STATUS, status);
        ProductOrderAdapter adapter = new ProductOrderAdapter(query, this);
        lstRequest.setAdapter(adapter);
        adapter.startListening();
    }

    private void showFilterDialog() {
        filterFragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(STATUS, status);
        filterFragment.setArguments(bundle);
        filterFragment.show(getSupportFragmentManager(), "FilterBottomSheet");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.nav_filter:
                showFilterDialog();
                break;
        }
        return true;
    }

    @Override
    public void onStatusChange(int status) {
        this.status = status;
        filterFragment.dismiss();
        if (type.equals(SERVICES))
            loadRequests(status);
        else
            loadOrders(status);
    }
}