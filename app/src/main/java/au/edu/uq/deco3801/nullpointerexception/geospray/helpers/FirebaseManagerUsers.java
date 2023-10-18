package au.edu.uq.deco3801.nullpointerexception.geospray.helpers;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManagerUsers {

    private static final String KEY_ROOT_DIR = "users";
    private final DatabaseReference rootRef;

    public FirebaseManagerUsers(Context context) {
        FirebaseApp firebaseApp= FirebaseApp.initializeApp(context);
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
