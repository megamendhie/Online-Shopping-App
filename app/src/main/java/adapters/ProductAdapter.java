package adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.R;

import interfaces.CartOperationListener;
import models.Product;
import utils.Reusable;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {
    private final CartOperationListener listener;

    public ProductAdapter(@NonNull Query query, Context context) {
        super(new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build());
        listener = (CartOperationListener)context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        holder.txtName.setText(model.getName());
        holder.txtSize.setText(model.getSize());
        if(model.getPrice()==0)
            holder.txtPrice.setText("negotiable");
        else
            holder.txtPrice.setText(Html.fromHtml("&#8358;"+Reusable.getFormattedAmount(model.getPrice())));
        if(!model.getIcon().isEmpty() && !model.getIcon().equals("non"))
            Glide.with(holder.imgIcon.getContext()).load(model.getIcon()).into(holder.imgIcon);
        holder.imgAdd.setImageResource(model.isLand()? R.drawable.ic_chat_24 : R.drawable.ic_add_cart_24);
        holder.imgAdd.setOnClickListener(v -> {
            if(model.isLand()){
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.imgAdd.getContext(),
                        R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Make Enquiry")
                        .setMessage("Do you want to make enquiry from the admin?")
                        .setPositiveButton("Proceed", (dialog, which) -> {
                            startWhatsapp(holder.imgAdd);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .show();
            }
            else
            listener.onAdd(model);
        });
    }

    private void startWhatsapp(View view) {
        String mssg = "Hi! I'm interested in a listing on St. Michael De Santos app and I'd like us to discuss about it.";
        String toNumber = "2347033731744";
        Uri uri = Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+mssg);
        try {
            Intent whatsApp = new Intent(Intent.ACTION_VIEW);
            whatsApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            whatsApp.setData(uri);
            PackageManager pkMgt = view.getContext().getPackageManager();
            pkMgt.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            view.getContext().startActivity(whatsApp);
        }
        catch (PackageManager.NameNotFoundException e){
            Toast.makeText(view.getContext(), "No WhatApp installed", Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        RelativeLayout lnrProduct;
        ImageView imgIcon, imgAdd;
        TextView txtName, txtSize, txtPrice;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            lnrProduct = itemView.findViewById(R.id.lnrProduct);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            txtName = itemView.findViewById(R.id.txtName);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
