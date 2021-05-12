package adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.LoginActivity;
import com.sqube.desantosdirectory.ProductActivity;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.ServiceRequestActivity;

import models.Category;
import utils.FirebaseUtil;

import static models.Commons.MODEL;
import static models.Commons.PRODUCT;
import static models.Commons.SERVICE;

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
        holder.itemView.setOnClickListener(view -> {
         Intent intent = model.getType().equals(PRODUCT)? new Intent(view.getContext(), ProductActivity.class):
                        new Intent(view.getContext(), ServiceRequestActivity.class);
                intent.putExtra(MODEL, model);
                if(model.getType().equals(SERVICE)&& FirebaseUtil.getAuth().getCurrentUser()==null){
                    view.getContext().startActivity(new Intent(view.getContext(), LoginActivity.class));
                    Toast.makeText(view.getContext(), "Sign in first", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.getContext().startActivity(intent);
        });

        if(!model.getIcon().isEmpty() && !model.getIcon().equals("non"))
            Glide.with(holder.imgIcon.getContext()).load(model.getIcon()).into(holder.imgIcon);

        int backgroundInt = position%8;
        switch (backgroundInt){
            case 0:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_1); break;
            case 1:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_2); break;
            case 2:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_3); break;
            case 3:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_4); break;
            case 4:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_5); break;
            case 5:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_6); break;
            case 6:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_7); break;
            default:
                holder.lnrCategory.setBackgroundResource(R.drawable.bg_category_8); break;
        }
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
