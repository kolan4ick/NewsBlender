package com.example.newsblender.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsblender.LoginActivity;
import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private ItemViewModel mViewModel;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // get the Firebase  storage reference and change image view if exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        ImageView appBarImageView = getActivity().findViewById(R.id.popUpButtonImageView);
        ImageView userAvatarImageView = mView.findViewById(R.id.userAvatarImageView);

        /* Change image view if exists */
        if (appBarImageView != null) {
            userAvatarImageView.setImageDrawable(appBarImageView.getDrawable());
        }


        FirebaseAuth fAuth = mViewModel.getFAuth();

        TextView userLoginValueTextView = mView.findViewById(R.id.userLoginValueTextView);
        TextView userInboxValueTextView = mView.findViewById(R.id.userInboxValueTextView);
        TextView registerDateValueTextView = mView.findViewById(R.id.registerDateValueTextView);
        userInboxValueTextView.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
        userLoginValueTextView.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        registerDateValueTextView.setText(dateFormat.format(new Date(Objects.requireNonNull(Objects.requireNonNull(fAuth.getCurrentUser()).getMetadata()).getCreationTimestamp())));

        mView.findViewById(R.id.okButton).setOnClickListener(item -> mViewModel.getNavigationViewValue().popBackStack());
        mView.findViewById(R.id.exitButton).setOnClickListener(item -> {
            fAuth.signOut();
            Intent login_activity = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(login_activity);
        });
        return mView;
    }
}