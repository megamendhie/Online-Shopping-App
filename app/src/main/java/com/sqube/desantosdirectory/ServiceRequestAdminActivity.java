package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.firestore.Query;

import adapters.ServiceRequestAdapter;
import utils.FirebaseUtil;

import static models.Commons.CREATED_AT;
import static models.Commons.REQUESTS;
import static models.Commons.SERVICE;
import static models.Commons.TYPE;

public class ServiceRequestAdminActivity extends AppCompatActivity {
    private RecyclerView lstRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Service Request");
        lstRequest = findViewById(R.id.lstRequest);
        lstRequest.setLayoutManager(new LinearLayoutManager(this));
        loadRequests();
    }


    private void loadRequests() {
        Query query = FirebaseUtil.getDatabase().collection(REQUESTS).orderBy(CREATED_AT, Query.Direction.DESCENDING)
                .whereEqualTo(TYPE, SERVICE);
        ServiceRequestAdapter adapter = new ServiceRequestAdapter(query, this);
        lstRequest.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}