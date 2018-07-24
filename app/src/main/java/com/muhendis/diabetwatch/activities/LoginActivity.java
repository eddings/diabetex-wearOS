package com.muhendis.diabetwatch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.helpers.FirebaseDBHelper;
import com.muhendis.diabetwatch.helpers.UIHelper;

public class LoginActivity extends WearableActivity {

    EditText mEmail,mPassword;
    ImageButton mLoginButton;
    FirebaseDBHelper mFirebaseDBHelper;
    private UIHelper mUIHelper;
    private String fcmBody,fcmTitle,fcmMessageId;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        fcmTitle = getIntent().getStringExtra("title");
        fcmBody = getIntent().getStringExtra("body");
        fcmMessageId = getIntent().getStringExtra("messageId");
        if(isLoggingOut())
        {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(this.getString(R.string.saved_user_isloggedin_key), false);
            editor.apply();
        }
        else if(checkIsLoggedIn())
        {
            Intent intent  = new Intent(this,ProgramActivity.class);
            intent.putExtra(Keys.NOTIFICATION_MESSAGE_TITLE,fcmTitle);
            intent.putExtra(Keys.NOTIFICATION_MESSAGE_BODY,fcmBody);
            intent.putExtra(Keys.NOTIFICATION_MESSAGE_ID,fcmMessageId);
            Log.d(TAG,"Values to put fcmmessage="+fcmBody+" title=+"+fcmTitle+" messageid="+fcmMessageId);
            startActivity(intent);
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Enables Always-on
        setAmbientEnabled();

        mFirebaseDBHelper = new FirebaseDBHelper(getApplicationContext(),this);
        mUIHelper = new UIHelper(this);
        mEmail = findViewById(R.id.loginEmailEditText);
        mPassword = findViewById(R.id.loginPasswordEditText);
        mLoginButton = findViewById(R.id.loginButton);
    }

    public boolean checkIsLoggedIn(){
        SharedPreferences sharedPref = this.getSharedPreferences(getResources().getString(R.string.saved_user_file_key),Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean(getString(R.string.saved_user_isloggedin_key), false);
        return isLoggedIn;
    }

    public void attemptLogin(View btn){
        if(!mUIHelper.isNetworkAvailable())
        {
            mUIHelper.showAlertNoInternet();
        }
        else{
            // Get text from email and passord field
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            mFirebaseDBHelper.attemptLogin(email,password,this);
        }    }

    public boolean isLoggingOut(){
        Intent intent = getIntent();
        return intent.getBooleanExtra(getResources().getString(R.string.logging_out_key),false);
    }


}
