package adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.ProductActivity;
import com.sqube.desantosdirectory.R;

import models.Category;

import static models.Commons.MODEL;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryHolder> {
    private final int VIEW_PRODUCT = 1;

    public CategoryAdapter(@NonNull Query query) {
        super(new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull final Category model) {
        holder.txtName.setText(model.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProductActivity.class);
                intent.putExtra(MODEL, model);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_SERVICE = 0;
        if(getItem(position).getType().equals("product"))
            return VIEW_PRODUCT;
        else
            return VIEW_SERVICE;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = viewType==VIEW_PRODUCT?
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_product, parent, false):
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_service, parent, false);
        return new CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        LinearLayout lnrCategory;
        ImageView imgIcon;
        TextView txtName;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            lnrCategory = itemView.findViewById(R.id.lnrCategory);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}
