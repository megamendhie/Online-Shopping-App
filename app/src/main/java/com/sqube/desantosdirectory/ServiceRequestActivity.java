package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import models.Category;
import models.ServiceRequest;
import models.User;
import utils.FirebaseUtil;

import static models.Commons.MODEL;
import static models.Commons.REQUESTS;

public class ServiceRequestActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtDate, txtTime;
    private EditText edtFName, edtLName, edtPhone, edtAddress, edtDescription;
    private String date, time, userId;
    final private Calendar c = Calendar.getInstance();
    private Category service;
    private final Gson gson = new Gson();
    private User myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Service Request Form");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        txtDate = findViewById(R.id.txtDate); txtDate.setOnClickListener(this);
        txtTime = findViewById(R.id.txtTime); txtTime.setOnClickListener(this);
        Button btnServiceName = findViewById(R.id.btnServiceName);
        edtFName = findViewById(R.id.edtFname);
        edtLName = findViewById(R.id.edtLname);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtDescription = findViewById(R.id.edtDescription);
        ImageView imgHeader = findViewById(R.id.imgHeader);

        long currentDate = new Date().getTime();
        date = DateFormat.format("d/M/yyyy", currentDate).toString();
        time = DateFormat.format("H:m", currentDate).toString();

        service = getIntent().getParcelableExtra(MODEL);
        btnServiceName.setText(service.getName());
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
        Glide.with(this).load(service.getIcon()).into(imgHeader);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtDate:
                setDate(); break;
            case R.id.txtTime:
                setTime(); break;
            case R.id.btnSubmit:
                submitRequest(); break;
        }
    }

    private void submitRequest() {
        String fName = edtFName.getText().toString();
        String lName = edtLName.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        String description = edtDescription.getText().toString();
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

        String id = FirebaseUtil.getDatabase().collection(REQUESTS).document().getId();
        ServiceRequest request = new ServiceRequest(service.getName(), service.getDocId(), service.getIcon(), id,
                fName, lName, userId, phone, myProfile.getEmail(), address, description, deliveryTime);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("Do you want to proceed with this request?")
                .setTitle("Confirm Request")
                .setPositiveButton("Proceed", (dialogInterface, i) -> {
                    FirebaseUtil.getDatabase().collection(REQUESTS).document(id).set(request)
                            .addOnCompleteListener(this, task -> {
                        dialogInterface.cancel();
                        showPrompt();
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void showPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceRequestActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ServiceRequestActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_request_successful, null);
        builder.setView(dialogView);
        final AlertDialog dialog= builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        Button btnOkay = dialog.findViewById(R.id.btnOkay);
        btnOkay.setOnClickListener(v -> finish());
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
}