package org.moire.opensudoku.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.moire.opensudoku.R;
import org.moire.opensudoku.utils.Const;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Log.d(Const.TAG, "Preparing Google API SignInOptions for LoginActivity");

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        Log.d(Const.TAG, "Successfully prepared Google API SignInOptions and ApiClient for LoginActivity");

        findViewById(R.id.sign_in_google).setOnClickListener(this);
        findViewById(R.id.sign_in_guest).setOnClickListener(this);

    }

    private void transitionToMain() {
        Log.d(Const.TAG, "Authentication with firebase succeeded, transitioning to main menu.");
        Intent startGameIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(startGameIntent);
    }

    private void signInGuest() {
        Log.d(Const.TAG, "Signing in to firebase services anonymously.");
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.e(Const.TAG, "Failed to authenticate anonymously!");

                        Toast.makeText(
                                LoginActivity.this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        transitionToMain();
                    }
                }
            });
    }

    private void signInGoogle() {
        Log.d(Const.TAG, "Displaying Google API sign in client to authenticate with firebase.");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(Const.TAG, "Google authentication came through, verifying...");

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(Const.TAG, "Google authentication succeeded, forwarding to firebase.");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(Const.TAG, "Authenticating with firebase using google credentials.");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(Const.TAG, "Failed to authenticate with firebase using google credentials!");
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            transitionToMain();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();

        if (viewID == R.id.sign_in_google) {
            signInGoogle();
        } else if (viewID == R.id.sign_in_guest) {
            signInGuest();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(Const.TAG, "Could not connect to Google API authentication services!");
    }
}
