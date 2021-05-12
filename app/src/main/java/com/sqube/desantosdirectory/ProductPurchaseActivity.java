package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import adapters.CartSummaryAdapter;
import models.CartProduct;
import models.ProductRequest;
import models.User;
import roomdb.DeSantosViewModel;
import utils.FirebaseUtil;
import utils.Reusable;

import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;
import static models.Commons.PRODUCT_REQUESTS;

public class ProductPurchaseActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtDate, txtTime;
    private EditText edtFName, edtLName, edtPhone, edtAddress;
    private String date, time, userId;
    final private Calendar c = Calendar.getInstance();
    private final Gson gson = new Gson();
    private User myProfile;
    private ArrayList<CartProduct> allCartProducts;
    private TextView txtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prouct_purchase);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Purchase Summary");
        txtPrice = findViewById(R.id.txtPrice);

        if(getIntent()==null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Button btnPurchase = findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(this);
        txtDate = findViewById(R.id.txtDate); txtDate.setOnClickListener(this);
        txtTime = findViewById(R.id.txtTime); txtTime.setOnClickListener(this);
        edtFName = findViewById(R.id.edtFname);
        edtLName = findViewById(R.id.edtLname);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        long currentDate = new Date().getTime();
        date = DateFormat.format("d/M/yyyy", currentDate).toString();
        time = DateFormat.format("H:m", currentDate).toString();

        if(FirebaseUtil.getAuth().getCurrentUser()==null){
            Toast.makeText(this, "Sign in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = FirebaseUtil.getAuth().getCurrentUser().getUid();
        String json = prefs.getString("profile", "");
        myProfile = (json.equals("")) ? null : gson.fromJson(json, User.class);
        edtFName.setText(myProfile.getFirstName());
        edtLName.setText(myProfile.getLastName());
        if(!myProfile.getPhone().isEmpty())
            edtPhone.setText(myProfile.getPhone());
        if(!myProfile.getAddress().isEmpty())
            edtAddress.setText(myProfile.getAddress());

        allCartProducts = getIntent().getParcelableArrayListExtra(PRODUCTS);
        RecyclerView lstProduct = findViewById(R.id.lstProduct);
        lstProduct.setLayoutManager(new LinearLayoutManager(this));
        lstProduct.setAdapter(new CartSummaryAdapter(allCartProducts));
        setTotalPrice();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    private void setTotalPrice() {
        if(allCartProducts==null||allCartProducts.isEmpty())
            return;
        long totalAmount = 0;
        for (CartProduct product:allCartProducts){
            totalAmount+=(product.getPrice()*product.getQuantity());
        }
        txtPrice.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(totalAmount)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtDate:
                setDate(); break;
            case R.id.txtTime:
                setTime(); break;
            case R.id.btnPurchase:
                submitRequest(); break;
        }
    }

    private void submitRequest() {
        String fName = edtFName.getText().toString();
        String lName = edtLName.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        String date = txtDate.getText().toString();
        String time = txtTime.getText().toString();
        long deliveryTime = getTimeStamp();

        if(fName.isEmpty()){
            edtFName.setError("Enter First Name");
            return;
        }
        if(lName.isEmpty()){
            edtLName.setError("Enter Last Name");
            return;
        }
        if(phone.isEmpty()){
            edtPhone.setError("Enter Phone no");
            return;
        }
        if(address.isEmpty()){
            edtAddress.setError("Enter Address");
            return;
        }
        if(date.equals("Delivery date")){
            Toast.makeText(this, "Set date", Toast.LENGTH_SHORT).show();
            return;
        }
        if(time.equals("Time")){
            Toast.makeText(this, "Set time", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document().getId();
        ProductRequest productRequest = new ProductRequest(allCartProducts, id, fName, lName, userId, phone,
                myProfile.getEmail(), address, deliveryTime);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("Do you want to proceed with this purchase?")
                .setTitle("Confirm Purchase")
                .setPositiveButton("Proceed", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(productRequest);
                    intent.putExtra(PRODUCT, json);
                    startActivityForResult(intent, 43);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void setDate(){
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            date = String.format("%s/%s/%s", day, month, year);
            txtDate.setText(getNewDate());
        },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTime(){
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (timePicker1, hour, min) -> {
            time = String.format("%s:%s", hour, min);
            txtTime.setText(getNewTime());
        },
                mHour, mMinute, false);
        timePicker.show();
    }

    private String getNewDate(){
        try {
            Date oldDate= new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).parse(date);
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getNewTime(){
        try {
            Date oldTime= new SimpleDateFormat("H:m", Locale.getDefault()).parse(time);
            return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(oldTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private long getTimeStamp(){
        try {
            Date newDate = new SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
                    .parse(String.format("%s %s",date, time));
            return newDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==43&&resultCode==RESULT_OK)
            finish();
    }
}