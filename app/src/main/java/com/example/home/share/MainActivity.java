package com.example.home.share;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends Activity {
    DatabaseHandler db = new DatabaseHandler(this);
    // google integration
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;

    // facebook integration
    CallbackManager mCallBackManager;
    ProfileTracker profileTracker;
    AccessToken accessToken;

    FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mCallBackManager = CallbackManager.Factory.create();
        LoginButton fb_login = (LoginButton)findViewById(R.id.fb_login_button);
        fb_login.setReadPermissions(Arrays.asList("email, user_friends"));
        fb_login.registerCallback(mCallBackManager, mCallback);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if(newProfile != null) {
                    Log.d("facebook", "started");
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    Log.d("Response", response.getJSONObject().toString());
                                    if (response.getError() != null) {
                                        // handle error
                                    } else {
                                        String email = object.optString("email");
                                        Log.d("Email", email);
                                        //     Log.d("Response", response.getInnerJsobject.toString());

                                    }
                                }
                            });
                    request.executeAsync();
                    Log.d("facebook", "ended");





                    String email = newProfile.getId();
                    if(db.checkUser(email) > 0) {
                        // already registered user.
                        db.loggedUser(email, "IN");
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        Bundle b = new Bundle();
                        b.putString("email", email);
                        b.putString("acc_type", "facebook");
                        intent.putExtras(b);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        // new user
                        db.addUser("", email);
                    }
                }
            }
        };
        profileTracker.startTracking();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        String user[] = db.getLoggedUser();
                        if(user != null && user[1].equalsIgnoreCase("OUT")) {
                            Log.d("google", "logout");
                            googleLogout();
                            db.removeLoggedUser();
                            return;
                        }
                        if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
                            mShouldResolve = true;
                            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                            if(db.checkUser(email) > 0) {
                                // already registered user.
                                db.loggedUser(email, "google_in");
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                Bundle b = new Bundle();
                                b.putString("email", email);
                                b.putString("acc_type", "google");
                                intent.putExtras(b);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                // new user
                                db.addUser("", email);
                            }
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        if (!mIsResolving && mShouldResolve) {
                            if (connectionResult.hasResolution()) {
                                try {
                                    connectionResult.startResolutionForResult(MainActivity.this, RC_SIGN_IN);
                                    mIsResolving = true;
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e ("", "Could not resolve ConnectionResult.", e);
                                    mIsResolving = false;
                                    mGoogleApiClient.connect();
                                }
                            } else {
                                // Could not resolve the connection result, show the user an
                                // error dialog.
                                Log.d("Error", "" + connectionResult);
                            }
                        } else {
                            // Show the signed-out UI
                            Log.d("Logout","");
                        }
                    }
                })
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        findViewById(R.id.google_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShouldResolve = true;
                mGoogleApiClient.connect();
            }
        });
    }

    public void googleLogout() {
        mShouldResolve = true;
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }
            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        profileTracker.stopTracking();
        mGoogleApiClient.disconnect();
    }
}
