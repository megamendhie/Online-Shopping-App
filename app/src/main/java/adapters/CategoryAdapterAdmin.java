package adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.ProductActivity;
import com.sqube.desantosdirectory.R;

import models.Category;

import static models.Commons.MODEL;

public class CategoryAdapterAdmin extends FirestoreRecyclerAdapter<Category, CategoryAdapterAdmin.CategoryHolder> {

    public CategoryAdapterAdmin(@NonNull Query query) {
        super(new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull final Category model) {
        holder.txtName.setText(model.getName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductActivity.class);
            intent.putExtra(MODEL, model);
            view.getContext().startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category, parent, false);
        return new CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        ConstraintLayout lnrCategory;
        ImageView imgIcon;
        TextView txtName;
        ImageView imgEdit, imgRemove;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            lnrCategory = itemView.findViewById(R.id.lnrCategory);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
            imgEdit = itemView.findViewById(R.id.imgHome);
            imgRemove = itemView.findViewById(R.id.imgRemove);
        }
    }
}
