package com.example.notebook.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.notebook.R;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        mAuth = FirebaseAuth.getInstance();
        Button submit = findViewById(R.id.resetPasswordSubmit);
        submit.setOnClickListener((v)->{
            TextView old = findViewById(R.id.oldPass);
            TextView newP = findViewById(R.id.newPass);
            if (Strings.isEmptyOrWhitespace(old.getText().toString())){
                old.setError("Required");
                return;
            }

            if (Strings.isEmptyOrWhitespace(newP.getText().toString())){
                newP.setError("Required");
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();

            AuthCredential authCredential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), old.getText().toString());

            user.reauthenticate(authCredential)
                    .addOnFailureListener((e)-> old.setError(e.getMessage()))
                    .onSuccessTask(t -> user.updatePassword(newP.getText().toString()))
                    .addOnSuccessListener(t ->{
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                    });
        });
    }
}