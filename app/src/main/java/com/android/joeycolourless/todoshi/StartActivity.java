package com.android.joeycolourless.todoshi;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class StartActivity extends FragmentActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;


    private Button signInButton;
    private SignInButton signInGooGleButton;
    private Button signUpButton;
    private Button guestButton;

    public static boolean mFirstEnter = false;
    private final int RC_SIGN_IN = 0;
    private final String TAG = "tag";

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    startActivity(ToDoListActivity.newInstance(mFirstEnter, getApplicationContext()));

                } else {
                    // User is signed out

                }

            }
        };

        final EditText email = (EditText) findViewById(R.id.editText_email_auth_fragment);

        final EditText password = (EditText) findViewById(R.id.editText_password_auth_fragment);

        signInButton = (Button) findViewById(R.id.button_signIn_auth_fragment);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if ( email.getText().toString().equals("") || password.getText().toString().equals("")){
                        errorMassage(getString(R.string.one_of_the_field_empty));
                    }else{
                        mFirstEnter = true;
                        mAuth = ToDoLab.get(getContext()).signInUser(email.getText().toString(), password.getText().toString(), StartActivity.this);
                    }


                } catch (Exception e) {
                    errorMassage(getString(R.string.something_wrong_maybe_internet));
                    updateUI(getContext());
                }
            }
        });

        signUpButton = (Button) findViewById(R.id.button_signUp_auth_fragment);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAuth = ToDoLab.get(getContext()).signUpUser(email.getText().toString(), password.getText().toString(), StartActivity.this);
                }catch (Exception e){
                    errorMassage(getString(R.string.something_wrong_maybe_internet));
                    updateUI(getContext());
                }
            }
        });

        guestButton = (Button) findViewById(R.id.button_guest_auth_fragment);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToDoListActivity.class);
                startActivity(intent);

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_token))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInGooGleButton = (SignInButton) findViewById(R.id.signInButton);
        signInGooGleButton.setSize(SignInButton.SIZE_STANDARD);
        signInGooGleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        updateUI(getContext());

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(getContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mFirstEnter = true;
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            errorMassage(getString(R.string.something_wrong_maybe_internet));
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateUI(Context context){
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

    private Context getContext() {
        return getApplicationContext();
    }
}
