package au.edu.uq.deco3801.nullpointerexception.geospray.helpers;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class to manage Firebase users.
 */
public class FirebaseManagerUsers {
    private static final String KEY_ROOT_DIR = "users";
    private final DatabaseReference rootRef;

    public FirebaseManagerUsers(Context context) {
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        rootRef = FirebaseDatabase.getInstance(firebaseApp).getReference().child(KEY_ROOT_DIR);
        DatabaseReference.goOnline();
    }

    public void storeUser (String user) {
        rootRef.child(user).setValue("test");
    }

    public DatabaseReference getRootRef() {
        return rootRef;
    }
}
