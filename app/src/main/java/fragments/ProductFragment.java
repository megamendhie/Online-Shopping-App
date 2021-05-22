package fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.sqube.desantosdirectory.LoginActivity;
import com.sqube.desantosdirectory.ProfileActivity;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.SearchActivity;

import adapters.CategoryAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;
import static models.Commons.SERVICE;
import static models.Commons.TYPE;
import static models.Commons.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {
    private RecyclerView lstProducts;
    private CategoryAdapter adapter;
    private CircleImageView imgDp;
    private final Gson gson = new Gson();

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        lstProducts = view.findViewById(R.id.lstCategory);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        lstProducts.setLayoutManager(gridLayoutManager);
        ImageView imgHome = view.findViewById(R.id.imgHome);
        imgHome.setOnClickListener(v -> openProfile());
        imgDp = view.findViewById(R.id.imgDp);
        imgDp.setOnClickListener(v -> openProfile());
        CardView crdSearch = view.findViewById(R.id.search_cardView);
        crdSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra(TYPE,PRODUCTS);
            startActivity(intent);
        });
        loadProductCategories();
        return view;
    }

    private void openProfile(){
        if(FirebaseUtil.getAuth().getCurrentUser()==null)
            startActivity(new Intent(getContext(), LoginActivity.class));
        else
            startActivity(new Intent(getContext(), ProfileActivity.class));
    }

    private void loadProductCategories() {
        Query query = FirebaseUtil.getDatabase().collection(CATEGORIES).orderBy(CREATED_AT)
                .whereEqualTo(TYPE, PRODUCT).whereEqualTo(VISIBLE, true);
        adapter = new CategoryAdapter(query);
        lstProducts.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(FirebaseUtil.getAuth().getCurrentUser()==null){
            imgDp.setImageResource(R.drawable.ic_account_circle_green_64);
        }
        else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
            String json = prefs.getString("profile", "");
            User myProfile = (json.equals("")) ? null : gson.fromJson(json, User.class);
            if(myProfile ==null)
                return;

            if(myProfile.getPic()==null||myProfile.getPic().isEmpty())
                imgDp.setImageResource(R.drawable.ic_account_circle_green_64);
            else
                Glide.with(getContext()).load(myProfile.getPic()).into(imgDp);
        }
    }
}
