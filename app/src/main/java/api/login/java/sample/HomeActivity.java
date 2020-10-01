package api.login.java.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import login.api.LoginApi;
import login.api.LoginCallback;
import login.api.TransactionConfirmationCallback;
import login.api.client.LoginResponse;
import login.api.client.TransactionConfirmationResponse;
import login.api.client.TransactionPayload;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (LoginApi.client().hasAccount() && LoginApi.client().isLoggedIn()) {
            String usernameText = LoginApi.client().getCurrentAccountName();
            TextView welcomeTextView = findViewById(R.id.verifyWelcomeText);
            if (usernameText != null && usernameText.length() > 0) {
                welcomeTextView.setText(String.format("Welcome %s!", usernameText));
            } else {
                welcomeTextView.setText("Welcome!");
            }
        } else {
            // redirect user to login page
            goToLogin();
            return;
        }

        // handle logout event
        findViewById(R.id.buttonAuthentication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });
        // handle logout event
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogoutClick();
            }
        });


    }

    /**
     * function to handle re-authenticate button event.
     */
    private void handleLoginClick(){
        final LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onComplete(LoginResponse response) {
                if (response.success) {
                    Toast toast = Toast.makeText(HomeActivity.this, "Login Success!!! ", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 48);
                    toast.show();
                    // goToHome
                } else {
                    // display error message as toast
                    Log.e(TAG, "Login error: " + response.errorMessage);
                    Toast toast = Toast.makeText(HomeActivity.this, "Error: " + response.errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 48);
                    toast.show();
                }
            }

        };
        LoginApi.client().login(this,  loginCallback);

    }

    private void handleLogoutClick(){
        LoginApi.client().logout();
        goToLogin();
    }


    private void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}