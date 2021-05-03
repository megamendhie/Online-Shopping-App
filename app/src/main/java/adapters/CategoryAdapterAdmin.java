package adapters;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.ProductActivity;
import com.sqube.desantosdirectory.R;

import interfaces.OnAddListener;
import models.Category;
import utils.GlideApp;

import static models.Commons.MODEL;

public class CategoryAdapterAdmin extends FirestoreRecyclerAdapter<Category, CategoryAdapterAdmin.CategoryHolder> {
    private OnAddListener onAddListener;

    public CategoryAdapterAdmin(@NonNull Query query, Context context) {
        super(new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build());
        onAddListener = (OnAddListener) context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull final Category model) {
        holder.txtName.setText(model.getName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductActivity.class);
            intent.putExtra(MODEL, model);
            view.getContext().startActivity(intent);
        });

        if(!model.getIcon().isEmpty() && !model.getIcon().equals("non"))
            Glide.with(holder.imgIcon.getContext()).load(model.getIcon()).into(holder.imgIcon);
        holder.imgEdit.setOnClickListener(v -> onAddListener.onEdit(model, false));
        holder.imgRemove.setOnClickListener(v -> onAddListener.onEdit(model, true));
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
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgRemove = itemView.findViewById(R.id.imgRemove);
        }
    }
}
