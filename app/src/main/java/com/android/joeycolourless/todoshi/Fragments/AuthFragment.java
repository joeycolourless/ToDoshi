package com.android.joeycolourless.todoshi.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

/**
 * Created by lenovo on 30.09.2017.
 */

public class AuthFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static AuthFragment newInstance(){


        AuthFragment fragment = new AuthFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_fragment, container, false);

        final EditText email = view.findViewById(R.id.editText_email_auth_fragment);

        final EditText password = view.findViewById(R.id.editText_password_auth_fragment);

        Button signInButton = view.findViewById(R.id.button_signIn_auth_fragment);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = ToDoLab.get(getContext()).signInUser(email.getText().toString(), password.getText().toString(), getActivity());
            }
        });

        Button signUpButton = view.findViewById(R.id.button_signUp_auth_fragment);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = ToDoLab.get(getContext()).signUpUser(email.getText().toString(), password.getText().toString(), getActivity());
            }
        });



        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
