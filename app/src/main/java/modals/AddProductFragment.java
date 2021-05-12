package modals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.SetOptions;
import com.sqube.desantosdirectory.R;
import com.theartofdev.edmodo.cropper.CropImage;

import interfaces.OnAddProductListener;
import models.Product;
import utils.FirebaseUtil;

import static android.app.Activity.RESULT_OK;
import static models.Commons.CATEGORY_ID;
import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;

public class AddProductFragment extends BottomSheetDialogFragment {
    private ImageView imgIcon;
    private EditText edtName;
    private EditText edtSize;
    private EditText edtAmount;
    private Button btnAdd;
    private ProgressBar prgAdd;
    private Uri filePath;
    private OnAddProductListener onAddListener;
    private Product product;
    private boolean editable;
    private String categoryId;
    private static final int PICK_IMAGE = 12;
    private boolean visible = true;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        onAddListener = (OnAddProductListener) getContext();
        editable = getArguments().getBoolean("EDITABLE", false);
        if(editable)
            product = getArguments().getParcelable(PRODUCT);
        else
            categoryId = getArguments().getString(CATEGORY_ID);
        imgIcon = view.findViewById(R.id.imgIcon);
        edtName = view.findViewById(R.id.edtName);
        edtSize = view.findViewById(R.id.edtSize);
        edtAmount = view.findViewById(R.id.edtAmount);
        prgAdd = view.findViewById(R.id.prgAdd);
        ImageView imgAddIcon = view.findViewById(R.id.imgAddIcon);
        imgAddIcon.setOnClickListener(v -> accessTheGallery());
        FrameLayout frmImage = view.findViewById(R.id.frmImage);
        frmImage.setOnClickListener(v -> accessTheGallery());
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            if(editable)
                addProduct();
            else
                addProduct(categoryId);
        });

        SwitchCompat swtVisible = view.findViewById(R.id.swtVisible);
        swtVisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(editable)
                product.setVisible(isChecked);
            else
                visible = isChecked;
        });

        if(editable){
            edtName.setText(product.getName());
            edtSize.setText(product.getSize());
            edtAmount.setText(String.valueOf(product.getPrice()));
            btnAdd.setText("Update Product");
            if(!product.getIcon().isEmpty() && !product.getIcon().equals("non"))
                Glide.with(getContext()).load(product.getIcon()).into(imgIcon);
            swtVisible.setChecked(product.isVisible());
        }
        return view;
    }

    private void addProduct() {
        String name = edtName.getText().toString();
        String size = edtSize.getText().toString();
        String amountInString = edtAmount.getText().toString().trim();
        if (filePath == null&&(product.getIcon().isEmpty()||product.getIcon().equals("non"))) {
            Toast.makeText(getContext(), "Choose image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.isEmpty()){
            edtName.setError("Enter name");
            return;
        }
        if(size.isEmpty()){
            edtSize.setError("Enter size");
            return;
        }
        if(amountInString.isEmpty()){
            edtAmount.setError("Enter amount");
            return;
        }

        product.setName(name);
        product.setSize(size);
        long amount = amountInString.isEmpty()? 0: Integer.parseInt(amountInString);
        product.setPrice(amount);

        btnAdd.setEnabled(false);
        prgAdd.setVisibility(View.VISIBLE);
        if(filePath!=null)
            FirebaseUtil.getStorage().getReference().child(PRODUCTS).child(product.getDocId()).putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                product.setIcon(url);
                                FirebaseUtil.getDatabase().collection(PRODUCTS).document(product.getDocId()).set(product, SetOptions.merge());
                                prgAdd.setVisibility(View.GONE);
                                btnAdd.setEnabled(true);
                                onAddListener.onComplete(true);
                            }))
                    .addOnFailureListener(e -> {
                        prgAdd.setVisibility(View.GONE);
                        btnAdd.setEnabled(true);
                        Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                    });
        else{
            FirebaseUtil.getDatabase().collection(PRODUCTS).document(product.getDocId()).set(product, SetOptions.merge());
            prgAdd.setVisibility(View.GONE);
            btnAdd.setEnabled(true);
            onAddListener.onComplete(true);
        }
    }

    private void addProduct(String categoryId) {
        String name = edtName.getText().toString();
        String size = edtSize.getText().toString();
        String amountInString = edtAmount.getText().toString().trim();

        if(filePath==null){
            Toast.makeText(getContext(), "Choose image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.isEmpty()){
            edtName.setError("Enter name");
            return;
        }
        if(size.isEmpty()){
            edtSize.setError("Enter size");
            return;
        }
        if(amountInString.isEmpty()){
            edtAmount.setError("Enter amount");
            return;
        }

        long amount = amountInString.isEmpty()? 0: Integer.parseInt(amountInString);

        btnAdd.setEnabled(false);
        prgAdd.setVisibility(View.VISIBLE);
        String id = FirebaseUtil.getDatabase().collection(PRODUCTS).document().getId();
        FirebaseUtil.getStorage().getReference().child(PRODUCTS).child(id).putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            product = new Product(name, url, id, categoryId, size, visible, amount);
                            FirebaseUtil.getDatabase().collection(PRODUCTS).document(id).set(product);
                            prgAdd.setVisibility(View.GONE);
                            btnAdd.setEnabled(true);
                            onAddListener.onComplete(false);
                        }))
                .addOnFailureListener(e -> {
                    prgAdd.setVisibility(View.GONE);
                    btnAdd.setEnabled(true);
                    Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                });
    }

    public void accessTheGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                imgIcon.setImageURI(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "An error occured", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==PICK_IMAGE&&resultCode == RESULT_OK) {
            filePath = data.getData();
            imgIcon.setImageURI(filePath);
        }
    }
}