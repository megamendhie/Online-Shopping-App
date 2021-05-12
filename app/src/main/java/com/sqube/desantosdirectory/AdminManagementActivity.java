package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.Query;

import adapters.UserAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import utils.FirebaseUtil;

import static models.Commons.CREATED_AT;
import static models.Commons.NAME;
import static models.Commons.USERS;

public class AdminManagementActivity extends AppCompatActivity {
    private EditText edtSearch;
    private RecyclerView lstAdmins, lstUser;
    private ProgressBar prgSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Admin Management");

        edtSearch = findViewById(R.id.edtSearch);
        lstAdmins = findViewById(R.id.lstAdmin);
        lstUser = findViewById(R.id.lstUsers);
        ImageView imgSearch = findViewById(R.id.imgSearch);
        prgSearch = findViewById(R.id.prgSearch);

        lstUser.setLayoutManager(new LinearLayoutManager(this));
        lstAdmins.setLayoutManager(new LinearLayoutManager(this));

        loadAdmins();

        imgSearch.setOnClickListener(v -> {
            String searchTerm = edtSearch.getText().toString();
            if(searchTerm.isEmpty()){
                edtSearch.setError("Enter email");
                return;
            }
            prgSearch.setVisibility(View.VISIBLE);
            Query query = FirebaseUtil.getDatabase().collection(USERS).orderBy("email")
                    .startAt(searchTerm).endAt(searchTerm + "\uf8ff");
            UserAdapter userAdapter = new UserAdapter(query);
            lstUser.setAdapter(userAdapter);
            userAdapter.startListening();

            query.get().addOnCompleteListener(task -> {
                prgSearch.setVisibility(View.GONE);
                if(task.isSuccessful() && task.getResult().isEmpty())
                    Toast.makeText(AdminManagementActivity.this, "No email match", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void loadAdmins() {
        Query query = FirebaseUtil.getDatabase().collection(USERS).orderBy(CREATED_AT).whereEqualTo("admin", true);
        UserAdapter adminAdapter = new UserAdapter(query);
        lstAdmins.setAdapter(adminAdapter);
        adminAdapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}