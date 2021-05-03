package modals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.SetOptions;
import com.sqube.desantosdirectory.R;
import com.theartofdev.edmodo.cropper.CropImage;

import interfaces.OnAddListener;
import models.Category;
import utils.FirebaseUtil;

import static android.app.Activity.RESULT_OK;
import static models.Commons.CATEGORIES;
import static models.Commons.CATEGORY;
import static models.Commons.PRODUCTS;
import static models.Commons.TYPE;

public class AddCategoryFragment extends BottomSheetDialogFragment {
    private ImageView imgIcon;
    private EditText edtName;
    private Button btnAdd;
    private ProgressBar prgAdd;
    private Uri filePath;
    private OnAddListener onAddListener;
    private Category category;
    private boolean editable;
    private String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        onAddListener = (OnAddListener) getContext();
        editable = getArguments().getBoolean("EDITABLE", false);
        if(editable)
            category = getArguments().getParcelable(CATEGORY);
        else
            type = getArguments().getString(TYPE);
        imgIcon = view.findViewById(R.id.imgIcon);
        edtName = view.findViewById(R.id.edtName);
        prgAdd = view.findViewById(R.id.prgAdd);
        ImageView imgAddIcon = view.findViewById(R.id.imgAddIcon);
        imgAddIcon.setOnClickListener(v ->
                CropImage.activity().setAspectRatio(1,1).start(getContext(), this));
        FrameLayout frmImage = view.findViewById(R.id.frmImage);
        frmImage.setOnClickListener(v -> openCropper());
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editable)
                    addCategory();
                else
                    addCategory(type);
            }
        });

        if(editable){
            edtName.setText(category.getName());
            btnAdd.setText("Update Category");
        }
        return view;
    }

    private void addCategory() {
        String name = edtName.getText().toString();
        if (filePath == null&&(category.getIcon().isEmpty()||category.getIcon().equals("non"))) {
            Toast.makeText(getContext(), "Choose image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.isEmpty()){
            edtName.setError("Enter name");
            return;
        }
        category.setName(name);

        btnAdd.setEnabled(false);
        prgAdd.setVisibility(View.VISIBLE);
        if(filePath!=null)
            FirebaseUtil.getStorage().getReference().child(PRODUCTS).child(category.getDocId()).putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                category.setIcon(url);
                                FirebaseUtil.getDatabase().collection(CATEGORIES).document(category.getDocId()).set(category, SetOptions.merge());
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
            FirebaseUtil.getDatabase().collection(CATEGORIES).document(category.getDocId()).set(category, SetOptions.merge());
            prgAdd.setVisibility(View.GONE);
            btnAdd.setEnabled(true);
            onAddListener.onComplete(true);
        }
    }

    private void addCategory(String type) {
        String name = edtName.getText().toString();

        if(filePath==null){
            Toast.makeText(getContext(), "Choose image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.isEmpty()){
            edtName.setError("Enter name");
            return;
        }

        btnAdd.setEnabled(false);
        prgAdd.setVisibility(View.VISIBLE);
        String id = FirebaseUtil.getDatabase().collection(CATEGORIES).document().getId();
        FirebaseUtil.getStorage().getReference().child(PRODUCTS).child(id).putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            category = new Category(name, url, id, type);
                            FirebaseUtil.getDatabase().collection(CATEGORIES).document(id).set(category);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    private void openCropper() {
        if(filePath==null)
            CropImage.activity().setAspectRatio(1,1).start(getContext(), this);
        else
            CropImage.activity(filePath).setAspectRatio(1,1).start(getContext(), this);
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
    }
}