package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;

public class UserLogin extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    private TextInputEditText email;
    private TextInputEditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        email = this.findViewById(R.id.log_email);
        password = this.findViewById(R.id.log_password);

        Button btnLogin = findViewById(R.id.log_login);
        btnLogin.setOnClickListener(v -> onLoginButtonPressed());

        Button btnCreateAccount = findViewById(R.id.log_create_user);
        btnCreateAccount.setOnClickListener(v -> onCreateUserButtonPressed());

    }

    private void onCreateUserButtonPressed() {
        Intent createUser = new Intent(getApplicationContext(), UserCreate.class);
        startActivity(createUser);
    }

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


    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        createToast("Signed In");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        if (task.getException() != null) {
                            createToast(task.getException().getMessage());
                            // todo implement textbox errors using catch

                        } else {
                            createToast("Authentication failed.");
                        }

//                        if(!task.isSuccessful()) {
//                            try {
//                                throw task.getException();
//                            } catch(FirebaseAuthWeakPasswordException e) {
//                                //do somethig
//                            } catch(FirebaseAuthInvalidCredentialsException e) {
//                                //do somethig
//                            } catch(FirebaseAuthUserCollisionException e) {
//                                //do somethig
//                            } catch(Exception e) {
//                                Log.e("TAG", e.getMessage());
//                            }
//                        }
                    }
                });

    }

    public void createToast(String msg) {
        Toast.makeText(getApplicationContext(), (msg),
                Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user) {
//        user.getDisplayName();
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
        // TODO if you want bar, remove full screen helper
    }
}
