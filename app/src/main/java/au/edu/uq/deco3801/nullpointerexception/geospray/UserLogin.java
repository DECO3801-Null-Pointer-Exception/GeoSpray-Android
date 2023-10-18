package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
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
        FirebaseUser userdata;

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
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                //do somethig
                            } catch(FirebaseAuthUserCollisionException e) {
                                //do somethig
                            } catch(Exception e) {
                                Log.e("TAG", e.getMessage());
                                // todo implement textbox errors using catch
                            }

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

//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName("Jane Q. User")
//                .build();
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("test", "User profile updated.");
//                            Log.i("test", user.getDisplayName()+"");
//                        }
//                    }
//                });
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName("Jane Q. User")
//                    .build();
//
//            userdata.updateProfile(profileUpdates)
////                    .addOnCompleteListener(new OnCompleteListener<Void>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Void> task) {
////                            if (task.isSuccessful()) {
////                                Log.i("userinfo", "User profile updated.");
////                            } else {
////                                Log.i("userinfo", "User profile not update");
////                            }
////                        }
////                    });
//
//            // The user object has basic properties such as display name, email, etc.
//            String displayName = userdata.getDisplayName();
//            String temail = userdata.getEmail();
//            String photoURL = String.valueOf(userdata.getPhotoUrl());
//            String uid = userdata.getUid();
//
//
//
//                        // The user's ID, unique to the Firebase project. Do NOT use
//                        // this value to authenticate with your backend server, if
//                        // you have one. Use User.getToken() instead.
//            Log.i("userinfo", displayName+" ");
//            Log.i("userinfo", temail+" ");
//            Log.i("userinfo", photoURL+" ");
//            Log.i("userinfo", uid);
//            Log.i("metadata", userdata.getMetadata().toString()+" ");
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
        // TODO if you want bar, remove full screen helper
    }
}
