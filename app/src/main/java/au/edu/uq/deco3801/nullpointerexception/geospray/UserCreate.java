package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserCreate extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.create_account);
    }
}
