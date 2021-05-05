package adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.R;

import interfaces.CartOperationListener;
import models.Product;
import utils.Reusable;

public class ProductAdapterAdmin extends FirestoreRecyclerAdapter<Product, ProductAdapterAdmin.ProductHolder> {

    public ProductAdapterAdmin(@NonNull Query query, Context context) {
        super(new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        holder.txtName.setText(model.getName());
        holder.txtSize.setText(model.getSize());
        holder.txtPrice.setText(Html.fromHtml("&#8358;"+Reusable.getFormattedAmount(model.getPrice())));
        if(!model.getIcon().isEmpty() && !model.getIcon().equals("non"))
            Glide.with(holder.imgIcon.getContext()).load(model.getIcon()).into(holder.imgIcon);
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductHolder(view);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        RelativeLayout lnrProduct;
        ImageView imgIcon, imgEdit, imgDelete;
        TextView txtName, txtSize, txtPrice;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            lnrProduct = itemView.findViewById(R.id.lnrProduct);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgRemove);
        }
    }
}
