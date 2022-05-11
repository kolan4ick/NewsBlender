package com.example.newsblender.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsblender.MainActivity;
import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.Inflater;

public class ProfileSettingsFragment extends Fragment {
    private TextView mEditTextFullName;
    private TextView mEditTextInbox;
    private TextView mEditTextPassword;
    private Button mButtonOk;
    private ItemViewModel mViewModel;
    // view for image view
    private ImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth fAuth;
    private View mView;
    private ImageView appBarImageView;

    public static ProfileSettingsFragment newInstance() {
        ProfileSettingsFragment fragment = new ProfileSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        // get the Firebase  storage reference and change image view if exists
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView = mView.findViewById(R.id.imageViewProfileSettings);
        appBarImageView = getActivity().findViewById(R.id.popUpButtonImageView);
        imageView.setOnClickListener(view -> SelectImage());

        /* Change image view if exists */
        if (appBarImageView != null && appBarImageView.getDrawable() != null) {
            imageView.setImageDrawable(appBarImageView.getDrawable());
        }

        /* Initialize main variables */
        mViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        fAuth = mViewModel.getFAuth();
        mEditTextFullName = mView.findViewById(R.id.editTextFullName);
        mEditTextPassword = mView.findViewById(R.id.editTextPassword);
        mEditTextInbox = mView.findViewById(R.id.editTextInbox);
        mButtonOk = mView.findViewById(R.id.buttonOk);

        /* Set views on screen with current user values */
        mEditTextFullName.setText(fAuth.getCurrentUser().getDisplayName());
        mEditTextInbox.setText(fAuth.getCurrentUser().getEmail());
        mButtonOk.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setMessage(requireContext().getString(R.string.are_you_sure));

            builder.setTitle(requireContext().getString(R.string.changing));

            builder.setCancelable(false);

            builder.setPositiveButton(
                    requireContext().getString(R.string.yes),
                    (dialog, which) -> {
                        try {
                            if (!mEditTextInbox.getText().toString().equals(fAuth.getCurrentUser().getEmail())) {
                                fAuth.getCurrentUser().updateEmail(mEditTextInbox.getText().toString());
                            }
                            if (!mEditTextFullName.getText().toString().equals(fAuth.getCurrentUser().getDisplayName())) {
                                fAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mEditTextFullName.getText().toString()).build());
                            }
                            if (mEditTextPassword.getText().length() > 5) {
                                fAuth.getCurrentUser().updatePassword(mEditTextPassword.getText().toString());
                            }
                            Toast.makeText(getContext(), "Успіх!", Toast.LENGTH_SHORT).show();
                            mViewModel.getNavigationViewValue().popBackStack();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Something is wrong, please, try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

            builder.setNegativeButton(
                    requireContext().getString(R.string.no),
                    (dialog, which) -> {
                        // If user click no
                        // then dialog box is canceled.
                        dialog.cancel();
                    });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();
        });

//        Variant to load user avatar in high quality
//        storageReference.child("images/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
//            Glide.with(this).load(uri).into(imageView);
//        });

        return mView;
    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(mView.getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                appBarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        uploadImage();
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(mView.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("images/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast
                                        .makeText(mView.getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT)
                                        .show();
                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(mView.getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded " + (int) progress + "%");
                            });
        }
    }

}