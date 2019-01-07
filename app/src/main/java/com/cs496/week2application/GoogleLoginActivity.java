package com.cs496.week2application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleLoginActivity extends Activity {
    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadContactPermissioncheck();
        WriteContactPermissioncheck();
        InternetPermissioncheck();
        WriteStoragePermissioncheck();

//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            String[] tempArr;
//            tempArr = account.toString().split("@");
//            Intent intent = new Intent(this, Main2Activity.class);
//            intent.putExtra("userAccount", tempArr[1]);
//            Log.d("GOOGLE_LOGIN>>>>>", tempArr[1]);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button signInButton = findViewById(R.id.signInBtn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ReadContactPermissioncheck() || !WriteContactPermissioncheck() || !WriteStoragePermissioncheck()) {
                    Toast.makeText(getApplicationContext(), "앱 실행을 위해 필요한 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                    ReadContactPermissioncheck();
                    WriteContactPermissioncheck();
                    WriteStoragePermissioncheck();
                } else {
                    signIn();
                }
            }
        });

        Button continueButton = findViewById(R.id.continueBtn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ReadContactPermissioncheck() || !WriteContactPermissioncheck() || !WriteStoragePermissioncheck()) {
                    Toast.makeText(getApplicationContext(), "앱 실행을 위해 필요한 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                    ReadContactPermissioncheck();
                    WriteContactPermissioncheck();
                    WriteStoragePermissioncheck();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.putExtra("userAccount", "");
                    Log.d("GOOGLE_LOGIN>>>>>", "No LogIn");
                    startActivity(intent);
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, pass account
            String[] tempArr;
            tempArr = account.toString().split("@");
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra("userAccount", tempArr[1]);
            Log.d("GOOGLE_LOGIN>>>>>", tempArr[1]);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE LOGIN>>>>>", "signInResult:failed code=" + e.getStatusCode());
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra("userAccount", "");
            startActivity(intent);
        }
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getApplicationContext(), permission);
    }

    public boolean ReadContactPermissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean InternetPermissioncheck() {
        if (checkselfpermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 100);
            if (checkselfpermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean WriteContactPermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean WriteStoragePermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }
}
