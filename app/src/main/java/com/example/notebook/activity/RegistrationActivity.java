package com.example.notebook.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notebook.R;
import com.example.notebook.models.UserDetails;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private FirebaseStorage mStorage;

    private FirebaseAuth mAuth;
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        args = getSupportFragmentManager().findFragmentById(R.id.camera_reg_fragment).getArguments();
        EditText eText = findViewById(R.id.birthday);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(RegistrationActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) ->
                            eText.setText(year1 + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (day < 10 ? "0" + day : day)), year, month, day);
            picker.show();
        });
        Button submit = findViewById(R.id.submitReg);
        submit.setOnClickListener((v) -> submitRegistration());
    }

    private void submitRegistration() {
        final String password = ((TextView) findViewById(R.id.passwordRegistration)).getText().toString();
        final String passwordRep = ((TextView) findViewById(R.id.passwordRegRepeat)).getText().toString();
        final String firstName = ((TextView) findViewById(R.id.firstName)).getText().toString();
        final String lastName = ((TextView) findViewById(R.id.lastName)).getText().toString();
        final String email = ((TextView) findViewById(R.id.email)).getText().toString();
        final String birthday = ((TextView) findViewById(R.id.birthday)).getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            ((TextView) findViewById(R.id.firstName)).setError("Required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            ((TextView) findViewById(R.id.lastName)).setError("Required");
            return;
        }

        if (TextUtils.isEmpty(passwordRep)) {
            ((TextView) findViewById(R.id.passwordRegRepeat)).setError("Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ((TextView) findViewById(R.id.passwordRegistration)).setError("Required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            ((TextView) findViewById(R.id.email)).setError("Required");
            return;
        }

        if (!Objects.equals(password, passwordRep)) {
            ((TextView) findViewById(R.id.passwordRegRepeat)).setError("Not the same");
            ((TextView) findViewById(R.id.passwordRegistration)).setError("Not the same");
            return;
        }

        final String login = email.substring(0, email.indexOf("@"));

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                writeUserData(user.getUid(), firstName, lastName, LocalDate.parse(birthday), login);
                            }
                        }
                );

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void writeUserData(String userId, String firstName, String lastName, LocalDate birthday, String login) {
        byte[] img;
        if (!args.containsKey("img")) {
            Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.png_transparent_default_avatar_thumbnail_photoroom_png_photoroom)).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            img = baos.toByteArray();
        } else {
            img = args.getByteArray("img");
        }
        UserDetails details = new UserDetails();
        details.setUser(userId);
        details.setLastName(lastName);
        details.setFirstName(firstName);
        details.setBirthday(birthday.toString());
        details.setId(userId);
        details.setLogin(login);
        saveImage(img, userId).addOnCompleteListener(task -> {
            details.setAvatar(task.getResult().toString());
            Map<String, Object> detailsMap = details.toMap();
            Map<String, Object> updates = new HashMap<>();
            updates.put("/details/" + userId, detailsMap);
            mDatabase.updateChildren(updates).isSuccessful();
        });
    }

    private Task<Uri> saveImage(byte[] img, String userId) {
        StorageReference ref = mStorage.getReference("images/" + userId + "/avatar.png");
        Task<Uri> uploadTask = ref.putBytes(img).continueWithTask(task -> ref.getDownloadUrl())
                .addOnCompleteListener(Task::getResult);
        return uploadTask;
    }
}