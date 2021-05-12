package com.sqube.desantosdirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.Date;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import models.ProductRequest;
import models.User;
import roomdb.DeSantosViewModel;
import utils.FirebaseUtil;
import utils.Reusable;

import static models.Commons.PRODUCT;
import static models.Commons.PRODUCT_REQUESTS;

public class CheckoutActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DeSantosViewModel viewModel;

    enum Status {SUCCESSFUL, PENDING;}

    private EditText mEditCardNum;
    private EditText mEditCVV;
    private EditText mEditExpiryMonth;
    private EditText mEditExpiryYear;
    private Button btnPay;

    private TextView mTextResponse;

    private ProgressBar prgPayment;
    private ProgressDialog dialog;
    private Charge charge;
    private Transaction transaction;

    private long amount = 0;


    private final Gson gson = new Gson();
    private ProductRequest productRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView mAmount = findViewById(R.id.txt_amount);
        mEditCardNum = findViewById(R.id.card_number);
        mEditCVV = findViewById(R.id.cvv);
        mEditExpiryMonth = findViewById(R.id.month);
        mEditExpiryYear = findViewById(R.id.year);
        btnPay = findViewById(R.id.btnPay);

        if(FirebaseUtil.getAuth().getCurrentUser()==null){
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        user = FirebaseUtil.getAuth().getCurrentUser();
        prgPayment = findViewById(R.id.prgPayment);
        mTextResponse = findViewById(R.id.textview_response); //mTextResponse.setOnClickListener(view -> contact());
        dialog = new ProgressDialog(this);

        String jsonProduct = getIntent().getStringExtra(PRODUCT);
        productRequest = gson.fromJson(jsonProduct, ProductRequest.class);
        amount = productRequest.getTotalAmount();
        mAmount.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(amount)));

        viewModel = new ViewModelProvider(this).get(DeSantosViewModel.class);
        PaystackSdk.initialize(getApplicationContext()); //initializing paystack sdk
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY);

        mEditCardNum.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }
            }
        });

        btnPay.setOnClickListener(view -> {
            try {
                transact();
            } catch (Exception e) {
                btnPay.setEnabled(true);
                this.mTextResponse.setText(String.format("An error occurred while charging card: %s", e.getMessage()));
            }
        });
    }

    private void transact(){
        int expiryMonth = 0;
        int expiryYear = 0;

        String cardNumber, mm, yy, cvv;
        cardNumber = mEditCardNum.getText().toString().trim();
        mm = mEditExpiryMonth.getText().toString().trim();
        yy = mEditExpiryYear.getText().toString().trim();
        cvv = mEditCVV.getText().toString().trim();

        if(cardNumber.isEmpty())
            mEditCardNum.setError("Card number");
        if(mm.isEmpty())
            mEditExpiryMonth.setError("month");
        if (yy.isEmpty())
            mEditExpiryYear.setError("year");
        if(cvv.isEmpty())
            mEditCVV.setError("CVV");

        if(cardNumber.isEmpty() || mm.isEmpty() || yy.isEmpty() || cvv.isEmpty())
            return;

        cardNumber = cardNumber.replace(" ", "");
        expiryMonth = Integer.parseInt(mm);
        expiryYear = Integer.parseInt(yy);

        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
        if (card.isValid()) {
            btnPay.setEnabled(false);
            mTextResponse.setText(getResources().getString(R.string.txt_processing));
            prgPayment.setVisibility(View.VISIBLE);

            charge = new Charge();
            charge.setCard(card);

            dialog = new ProgressDialog(this);
            dialog.setMessage(getResources().getString(R.string.txt_processing));
            dialog.show();

            charge.setAmount((int) amount*100);
            charge.setEmail(user.getEmail());
            charge.setReference(user.getUid() +"_"+ new Date().getTime());
            try {
                charge.putCustomField("Charged From", "Dpp app");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard(); //Function to Charge user here
        }
        else {
            Toast.makeText(this, "Invalid card details", Toast.LENGTH_LONG).show();
            mTextResponse.setText("Invalid card details");
        }
    }

    private void updateTextViews(Status status) {
        if (transaction.getReference() != null) {
            if (status==Status.SUCCESSFUL) {
                prgPayment.setVisibility(View.GONE);
                mTextResponse.setText("Successful!!");
            }
            if(status==Status.PENDING){
                prgPayment.setVisibility(View.VISIBLE);
                mTextResponse.setText(getResources().getString(R.string.txt_processing));
            }
        } else {
            mTextResponse.setText("No transaction");
            prgPayment.setVisibility(View.GONE);
            btnPay.setEnabled(true);
        }
    }

    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
                if ((dialog != null) && dialog.isShowing())
                    dialog.dismiss();

                FirebaseUtil.getDatabase().collection(PRODUCT_REQUESTS).document(productRequest.getDocId()).set(productRequest)
                        .addOnCompleteListener(CheckoutActivity.this, task -> {
                            viewModel.deleteAll();
                            showPrompt();
                        });
                setResult(RESULT_OK);
                CheckoutActivity.this.transaction = transaction;
                showPrompt();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                CheckoutActivity.this.transaction = transaction;
                updateTextViews(Status.PENDING);
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.i("paystack", "onError: "+ error.getMessage());
                if ((dialog != null) && dialog.isShowing())
                    dialog.dismiss();
                prgPayment.setVisibility(View.GONE);
                btnPay.setEnabled(true);
                mTextResponse.setText(String.format("Error: %s\n\n%s", error.getMessage(),
                        getResources().getString(R.string.txt_try_again)));
            }
        });
    }

    private void showPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CheckoutActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_request_successful, null);
        builder.setView(dialogView);
        final AlertDialog dialog= builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        Button btnOkay = dialog.findViewById(R.id.btnOkay);
        btnOkay.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }
}