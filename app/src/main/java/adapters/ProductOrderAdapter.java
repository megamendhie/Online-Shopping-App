package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.ServiceRequestDetailsActivity;

import java.util.ArrayList;

import models.CartProduct;
import models.ProductRequest;

import static models.Commons.SERVICE;

public class ProductOrderAdapter extends FirestoreRecyclerAdapter<ProductRequest, ProductOrderAdapter.ProductOrderHolder> {
    private final Context context;

    public ProductOrderAdapter(@NonNull Query query, Context context) {
        super(new FirestoreRecyclerOptions.Builder<ProductRequest>()
                .setQuery(query, ProductRequest.class)
                .build());
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductOrderHolder holder, int position, @NonNull ProductRequest model) {
        String date = DateFormat.format("d MMM", model.getCreatedAt()).toString();
        holder.txtDate.setText(date);
        holder.txtName.setText(String.format("%s %s", model.getfName(), model.getlName()));
        ArrayList<CartProduct> products = model.getProducts();
        if(products!=null && !products.isEmpty())
            showProductNames(holder.txtProductName, products, holder.imgIcon);

        switch (model.getStatus()){
            case 0:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorStatusAmber)));
                break;
            case 1:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorPrimary)));
                break;
            case 2:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorStatusRed)));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ServiceRequestDetailsActivity.class);
            //intent.putExtra(SERVICE, model);
            v.getContext().startActivity(intent);
        });

    }

    private void showProductNames(TextView txtProductName, ArrayList<CartProduct> products, ImageView imgIcon) {
        Glide.with(context).load(products.get(0).getIcon()).into(imgIcon);
        String names;

        switch (products.size()){
            case 1:
                txtProductName.setText(products.get(0).getName()); break;
            case 2:
                names = products.get(0).getName() + " and " +products.get(1).getName();
                txtProductName.setText(names);
                break;
            case 3:
                names = products.get(0).getName() + ", " +products.get(1).getName() + " + 1 item";
                txtProductName.setText(names);
                break;
            default:
                names = products.get(0).getName() + ", " +products.get(1).getName() + " + "+ (products.size()-2)+ " items";
                txtProductName.setText(names);
                break;
        }
    }

    @NonNull
    @Override
    public ProductOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_service, parent, false);
        return new ProductOrderHolder(view);
    }

    class ProductOrderHolder extends RecyclerView.ViewHolder {
        ImageView imgStatus, imgIcon;
        TextView txtProductName, txtName, txtDate;
        public ProductOrderHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtProductName = itemView.findViewById(R.id.txtServiceName);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
