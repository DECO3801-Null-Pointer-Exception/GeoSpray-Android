package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.FirebaseUser;


import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;

/**
 * Activity allowing a user to sign into an account.
 */
public class UserLogin extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextInputEditText email;
    private TextInputEditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        email = this.findViewById(R.id.log_email);
        password = this.findViewById(R.id.log_password);

        password.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onLoginButtonPressed();
                return true;
            }

            return false;
        });

        Button btnLogin = findViewById(R.id.log_login);
        btnLogin.setOnClickListener(v -> onLoginButtonPressed());

        Button btnCreateAccount = findViewById(R.id.log_create_user);
        btnCreateAccount.setOnClickListener(v -> onCreateUserButtonPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null && !user.isAnonymous()) {
            //user has registered an account and should brought back to the main page
            finish();
        }
    }

    /**
     * Handles tapping on the create user button by opening the user creation activity.
     */
    private void onCreateUserButtonPressed() {
        Intent createUser = new Intent(getApplicationContext(), UserCreate.class);
        startActivity(createUser);
    }

    /**
     * Handles tapping on the login button by checking the provided fields then logging in.
     */
    private void onLoginButtonPressed() {
        email.setError(null);
        password.setError(null);

        if (TextUtils.isEmpty(email.getText())){
            email.setError("Please Enter an Email");
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter a Password");
        } else {
            // TODO allow multiple errors to be set at once

            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            signIn(emailString, passwordString);
        }
    }

    /**
     * Handles signing into the account associated with the given email and password combination.
     *
     * @param email The account email.
     * @param password The account password.
     */
    private void signIn(String email, String password) {
        //https://firebase.google.com/docs/auth/android/password-auth
        // Accessed on October 16.
        FirebaseUser userdata;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() != null) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                this.email.setError("Invalid Email Id");
                                this.email.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "email :" + email);
                                this.password.setError("Invalid Password");
                                this.password.requestFocus();
                            } catch (FirebaseNetworkException e) {
                                createToast("error: no network");
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage() + "");
                            }
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            updateUI(null);
                        } else {
                            createToast("Authentication failed.");
                        }
                    }
                });
    }

    /**
     * Shows the given message in a toast pop-up.
     *
     * @param msg The message to display.
     */
    public void createToast(String msg) {
        Toast.makeText(getApplicationContext(), (msg),
                Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user) {
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
        // TODO if you want bar, remove full screen helper
    }
}
