package com.example.newsblender.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsblender.R;
import com.example.newsblender.classes.ItemViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.zip.Inflater;

public class ProfileSettingsFragment extends Fragment {
    private TextView mEditTextFullName;
    private TextView mEditTextInbox;
    private TextView mEditTextPassword;
    private Button mButtonOk;
    private ItemViewModel mViewModel;

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
        View mView = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        /* Initialize main variables */
        mViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        FirebaseAuth fAuth = mViewModel.getFAuth();
        mEditTextFullName = mView.findViewById(R.id.editTextFullName);
        mEditTextPassword = mView.findViewById(R.id.editTextPassword);
        mEditTextInbox = mView.findViewById(R.id.editTextInbox);
        mButtonOk = mView.findViewById(R.id.buttonOk);

        /* Set views on screen with current user values */
        mEditTextFullName.setText(fAuth.getCurrentUser().getDisplayName());
        mEditTextInbox.setText(fAuth.getCurrentUser().getEmail());

        mButtonOk.setOnClickListener(view -> {
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

        return mView;
    }
}