package com.example.notebook.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notebook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button login;
    Button register;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.regButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        login.setOnClickListener(v -> signIn());
        register.setOnClickListener(v -> signUp());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        String login = ((TextView) findViewById(R.id.name)).getText().toString();
        String password = ((TextView) findViewById(R.id.passwordReg)).getText().toString();

        mAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    }
                });
    }

    private void signUp() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void onAuthSuccess(FirebaseUser user) {
        String id=user.getUid();
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("userId", id);          //load user data by id in next activity
        startActivity(intent);
    }

    private boolean validateForm() {
        boolean result = true;
        String login = ((TextView) findViewById(R.id.name)).getText().toString();
        if (TextUtils.isEmpty(login)) {
            ((TextView) findViewById(R.id.name)).setError("Required");
            result = false;
        } else {
            ((TextView) findViewById(R.id.name)).setError(null);
        }

        String password = ((TextView) findViewById(R.id.passwordReg)).getText().toString();
        if (TextUtils.isEmpty(password)) {
            ((TextView) findViewById(R.id.passwordReg)).setError("Required");
            result = false;
        } else {
            ((TextView) findViewById(R.id.passwordReg)).setError(null);
        }

        return result;
    }
}
