package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    private TextInputEditText email;
    private TextInputEditText password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        email = this.findViewById(R.id.log_email);
        password = this.findViewById(R.id.log_password);

        Button btn = findViewById(R.id.log_login);
        btn.setOnClickListener(v -> onLoginButtonPressed());

    }

    private void onLoginButtonPressed() {

        email.setError(null);
        password.setError(null);

        if (email.getText() == null){
            email.setError("Please Enter an Email");
            return;
        } else if (password.getText() == null) {
            password.setError("Please Enter a Password");
            return;
        }

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        signIn(emailString, passwordString);
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
                            createToast(task.getException().toString());
                        } else {
                            createToast("Authentication failed.");
                        }
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
}
