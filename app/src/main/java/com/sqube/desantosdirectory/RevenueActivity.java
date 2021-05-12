package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import utils.FirebaseUtil;
import utils.Reusable;

import static models.Commons.PRODUCT_REQUESTS;
import static models.Commons.REQUESTS;
import static models.Commons.STATUS;

public class RevenueActivity extends AppCompatActivity {
    TextView txtProductCount, txtServiceCount, txtDeliveredProduct, txtDeliveredService, txtEarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Earnings Report");

        txtEarning = findViewById(R.id.txtEarning);
        txtProductCount = findViewById(R.id.txtProductCount);
        txtDeliveredProduct = findViewById(R.id.txtProductDelivered);
        txtServiceCount = findViewById(R.id.txtServiceCount);
        txtDeliveredService = findViewById(R.id.txtServiceDelivered);

        FirebaseUtil.getDatabase().collection(REQUESTS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()&&!task.getResult().isEmpty()){
                int count = task.getResult().size();
                txtServiceCount.setText(String.valueOf(count));
            }
        });
        FirebaseUtil.getDatabase().collection(REQUESTS).whereEqualTo(STATUS, 1).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()&&!task.getResult().isEmpty()){
                int count = task.getResult().size();
                txtDeliveredService.setText(String.valueOf(count));
            }
        });
        FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()&&!task.getResult().isEmpty()){
                int count = task.getResult().size();
                txtProductCount.setText(String.valueOf(count));
            }
        });
        FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).whereEqualTo(STATUS, 1).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()&&!task.getResult().isEmpty()){
                int count = task.getResult().size();
                txtDeliveredProduct.setText(String.valueOf(count));

                long revenue = 0;
                for(DocumentSnapshot snapshot: task.getResult().getDocuments()){
                    long amount = snapshot.getLong("totalAmount");
                    revenue += amount;
                }
                txtEarning.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(revenue)));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}