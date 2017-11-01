package com.android.joeycolourless.todoshi.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.ToDoListActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;





public class AuthFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private Button signInButton;
    private Button signUpButton;
    private Button guestButton;

    public static boolean mFirstEnter = false;

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

                    startActivity(ToDoListActivity.newInstance(mFirstEnter, getContext()));

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

        final EditText email = (EditText) view.findViewById(R.id.editText_email_auth_fragment);

        final EditText password = (EditText) view.findViewById(R.id.editText_password_auth_fragment);

        signInButton = (Button) view.findViewById(R.id.button_signIn_auth_fragment);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if ( email.getText().toString().equals("") || password.getText().toString().equals("")){
                        errorMassage(getString(R.string.one_of_the_field_empty));
                    }else{
                        mFirstEnter = true;
                        mAuth = ToDoLab.get(getContext()).signInUser(email.getText().toString(), password.getText().toString(), getActivity());
                    }


                } catch (Exception e) {
                    errorMassage(getString(R.string.something_wrong_maybe_internet));
                    enableButtons(getContext());
                }
            }
        });

        signUpButton = (Button) view.findViewById(R.id.button_signUp_auth_fragment);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAuth = ToDoLab.get(getContext()).signUpUser(email.getText().toString(), password.getText().toString(), getActivity());
                }catch (Exception e){
                    errorMassage(getString(R.string.something_wrong_maybe_internet));
                    enableButtons(getContext());
                }
            }
        });

        guestButton = (Button) view.findViewById(R.id.button_guest_auth_fragment);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToDoListActivity.class);
                startActivity(intent);

            }
        });

        enableButtons(getContext());



        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        enableButtons(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void enableButtons(Context context){
        if (!isOnline(context)){
            signInButton.setEnabled(false);
            signUpButton.setEnabled(false);
        }else {
            signInButton.setEnabled(true);
            signUpButton.setEnabled(true);
        }
    }
    private void errorMassage(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
