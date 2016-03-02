package com.abhinav.personalto_dolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Set;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "LoginActivity";
    private final static int RC_SIGN_IN = 2000;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);
        initialize();
    }

    private void initialize(){
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        signInButton.setOnClickListener(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"Facebook token "+loginResult.getAccessToken());
                Set<String> set = loginResult.getRecentlyGrantedPermissions();
                Log.d(TAG,""+set.toString());
                Profile fbProfile = Profile.getCurrentProfile();
                Toast.makeText(LoginActivity.this, "Welcome, "+fbProfile.getFirstName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("profile",fbProfile);
                intent.putExtra("photoUrl",fbProfile.getProfilePictureUri(800,600));
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"Facebook user cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"Facebook login error:"+error.getMessage());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG,connectionResult.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                progressDialog = ProgressDialog.show(this,"Please wait","While we log you in");
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN && resultCode==RESULT_OK){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            if(progressDialog!=null){
                progressDialog.hide();
                progressDialog.dismiss();
            }
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            Toast.makeText(LoginActivity.this, "Welcome, "+googleSignInAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("photoUrl",googleSignInAccount.getPhotoUrl());
            startActivity(intent);
            finish();
        }
        else Toast.makeText(LoginActivity.this, "Error in Sign in", Toast.LENGTH_SHORT).show();
    }
}
