package api.login.java.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import login.api.LoginApi;
import login.api.LoginCallback;
import login.api.RegisterCallback;
import login.api.client.LoginResponse;
import login.api.client.RegisterResponse;
import login.api.fido2.FidoCapability;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //check fido capability
        handleCheckDeviceCompatibility();


        //check if signedIn
        if (LoginApi.client().hasAccount() && LoginApi.client().isLoggedIn()) {
            // redirect user to home page directly
            goToHome();
            return;
        }

        // handle register button
        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterClick();
            }
        });
        // handle login button
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });


    }

    private void handleCheckDeviceCompatibility() {

        FidoCapability capability = LoginApi.client().checkFidoCapability(this);
        switch (capability) {
            case FIDO_SUPPORTED:
                // proceed to fido register or login operation
                Log.i(TAG, "Fido is supported");
                break;
            case FIDO_SUPPORTED_MISSING_ENROLMENT:
                // user is missing biometric enrolment
                // get the list of supported Fido biometrics
                List<String> featureList = LoginApi.client().getFidoSupportedBiometricsTypes(this);

                // pop message inform user to enroll to the list of supported hardware Fingerprint, Iris, or FaceID
                Log.i(TAG, "Fido is missing enrolment");

                StringBuilder featureSet = new StringBuilder();
                int i = 0;
                for (String feature : featureList) {
                    if (i != 0) {
                        featureSet.append("|");
                    }
                    featureSet.append(feature);
                    i++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Missing Biometric Enrolment");
                builder.setMessage("Please enrol with " + featureSet.toString());
                builder.setCancelable(true);
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
            case FIDO_NOT_SUPPORTED:
                // fido is not supported prompt user to fallback authentication flow
                Log.i(TAG, "Fido is not supported");
                AlertDialog.Builder unsupportedDialog = new AlertDialog.Builder(this);
                unsupportedDialog.setTitle("Device Unsupported");
                unsupportedDialog.setMessage("Your device does not support secure biometric authentication");
                unsupportedDialog.setCancelable(true);
                unsupportedDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                unsupportedDialog.create().show();
                break;
            case FIDO_NOT_AVAILABLE:
                // fido is not available prompt user to retry later
                Log.i(TAG, "Fido is not available retry later");
                AlertDialog.Builder notavailableDialog = new AlertDialog.Builder(this);
                notavailableDialog.setTitle("Device Not Available");
                notavailableDialog.setMessage("Your device biometric may be busy please retry again later");
                notavailableDialog.setCancelable(true);
                notavailableDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                notavailableDialog.create().show();
                break;
        }
    }


    /**
     * function to handle register button event
     */
    private void handleRegisterClick() {

        final RegisterCallback registerCallback = new RegisterCallback() {

            @Override
            public void onComplete(RegisterResponse response) {
                if (response.success) {
                    // go to home activity
                    goToHome();
                } else {
                    // display error message as toast
                    Log.e(TAG, "Register error: " + response.errorMessage);
                    Toast toast = Toast.makeText(LoginActivity.this, response.errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 48);
                    toast.show();
                }
            }
        };

        final TextInputEditText usernameInputText = findViewById(R.id.usernameInputText);
        if (usernameInputText.getText() == null || TextUtils.isEmpty(usernameInputText.getText().toString())) {
            Toast toast = Toast.makeText(LoginActivity.this, "Username is missing!!! ", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 48);
            toast.show();
        } else {
            String username = usernameInputText.getText().toString();
            LoginApi.client().registerWithUsername(LoginActivity.this, username, registerCallback);
        }
    }

    /**
     * function to handle login button event
     */
    private void handleLoginClick() {
        final LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onComplete(LoginResponse response) {
                if (response.success) {
                    // goToHome
                    goToHome();
                } else {
                    // display error message as toast
                    Log.e(TAG, "Login error: " + response.errorMessage);

                    Toast toast = Toast.makeText(LoginActivity.this, "Error: " + response.errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 48);
                    toast.show();
                }
            }

        };

        final TextInputEditText usernameInputText = findViewById(R.id.usernameInputText);
        if (usernameInputText.getText() == null || TextUtils.isEmpty(usernameInputText.getText().toString())) {
            Toast toast = Toast.makeText(LoginActivity.this, "Username is missing!!! ", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 48);
            toast.show();
        } else {
            String username = usernameInputText.getText().toString();
            LoginApi.client().login(LoginActivity.this, username, loginCallback);
        }

    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}