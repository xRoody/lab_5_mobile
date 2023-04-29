package com.example.notebook.activity;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.notebook.R;
import com.example.notebook.models.UserDetails;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ChangeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private FirebaseStorage mStorage;

    private FirebaseAuth mAuth;

    private Bundle args;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        init();
    }

    private void init() {
        EditText eText = findViewById(R.id.birthdayEdit);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        args = getSupportFragmentManager().findFragmentById(R.id.camera_edit_fragment).getArguments();
        userId = getIntent().getStringExtra("userId");
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(ChangeActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) ->
                            eText.setText(year1 + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (day < 10 ? "0" + day : day)), year, month, day);
            picker.show();
        });
        Button submit = findViewById(R.id.editSubmit);
        mDatabase.child("details/" + userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails details = task.getResult().getValue(UserDetails.class);
                        writeDetails(details);
                    }
                });
        submit.setOnClickListener((v) -> submitEdit());
    }

    private void writeDetails(UserDetails details) {
        ((TextView) findViewById(R.id.nameEdit)).setText(details.firstName);
        ((TextView) findViewById(R.id.lastNameEdit)).setText(details.lastName);
        ((TextView) findViewById(R.id.loginEdit)).setText(details.getLogin());
        ((TextView) findViewById(R.id.birthdayEdit)).setText(details.birthday);
        ImageView avatar = getSupportFragmentManager().findFragmentById(R.id.camera_edit_fragment)
                .getView()
                .findViewById(R.id.avatar);
        StorageReference ref = mStorage.getReference("images/" + userId + "/avatar.png");
        ref.getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                avatar.setImageDrawable(
                        new BitmapDrawable(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length))
                );
            }
        });
    }

    private void submitEdit() {
        BitmapDrawable avatar = (BitmapDrawable)((ImageView)getSupportFragmentManager().findFragmentById(R.id.camera_edit_fragment)
                .getView()
                .findViewById(R.id.avatar))
                .getDrawable();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        avatar.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);

        writeUserData(userId,
                ((TextView) findViewById(R.id.nameEdit)).getText().toString(),
                ((TextView) findViewById(R.id.lastNameEdit)).getText().toString(),
                ((TextView) findViewById(R.id.loginEdit)).getText().toString(),
                ((TextView) findViewById(R.id.birthdayEdit)).getText().toString(),
                baos.toByteArray()
        );
    }

    private void writeUserData(String userId, String firstName, String lastName, String birthday, String login, byte[] img) {
        UserDetails details = new UserDetails();
        details.setUser(userId);
        details.setLastName(lastName);
        details.setFirstName(firstName);
        details.setBirthday(birthday);
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