package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;

/**
 * Activity allowing the user to create an account.
 */
public class UserCreate extends AppCompatActivity {
    private static final String TAG = "UsernameEmailPassword";

    private FirebaseAuth mAuth;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        mAuth = FirebaseAuth.getInstance();

        email = this.findViewById(R.id.create_email);
        password = this.findViewById(R.id.create_password);
        username = this.findViewById(R.id.create_username);

        Button btn = findViewById(R.id.create_login);
        btn.setOnClickListener(v -> onCreateButtonPressed());
    }

    /**
     * Handles pressing the create button by checking the provided fields then creating an account.
     */
    private void onCreateButtonPressed() {
        email.setError(null);
        password.setError(null);
        if (TextUtils.isEmpty(username.getText())) {
            email.setError("Please Enter an Username");
            return;
        } else if (TextUtils.isEmpty(email.getText())){
            email.setError("Please Enter an Email");
            return;
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter a Password");
            return;
        } else {
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();
            String usernameString = username.getText().toString();

            createAccount(emailString, passwordString, usernameString);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            this.reload();
        }
    }

    /**
     * Creates a new account for a user.
     *
     * @param email The account email.
     * @param password The account password.
     * @param username The account username.
     */
    private void createAccount(String email, String password, String username) {
        // https://firebase.google.com/docs/auth/android/password-auth
        // Accessed on October 16.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        createToast("Account Created.");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            } else {
                                                Log.d(TAG,"Display Name Error");
                                            }
                                        }
                                    });
                        }

                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        if (task.getException() != null) {
                            createToast(task.getException().getMessage());
                        } else {
                            createToast("Authentication failed.");
                        }
                    }
                });
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {
        finish();
    }

    /**
     * Shows the given message in a toast pop-up.
     *
     * @param msg The message to show.
     */
    public void createToast(String msg) {
        Toast.makeText(getApplicationContext(), (msg),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }
}


