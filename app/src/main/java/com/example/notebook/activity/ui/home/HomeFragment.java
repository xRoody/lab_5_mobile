package com.example.notebook.activity.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.R;
import com.example.notebook.activity.ResetPasswordActivity;
import com.example.notebook.databinding.FragmentHomeBinding;
import com.example.notebook.models.UserDetails;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference mDatabase;

    private FirebaseStorage mStorage;

    private FirebaseAuth mAuth;

    private Bundle args;

    private View view1;

    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if(getArguments()!=null){
            userId=getArguments().getString("userId");
        }
        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        view1=getView();
        System.out.println(view);
        init();
    }
    private void init() {
        if(userId==null)return;

        System.out.println(getView());
        EditText eText = view1.findViewById(R.id.birthdayEditNew);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        args = getChildFragmentManager().findFragmentById(R.id.camera_edit_fragment_new).getArguments();
        //userId = getIntent().getStringExtra("userId");
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) ->
                            eText.setText(year1 + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (day < 10 ? "0" + day : day)), year, month, day);
            picker.show();
        });
        mDatabase.child("details/" + userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails details = task.getResult().getValue(UserDetails.class);
                        ((TextView) view1.findViewById(R.id.nameEditNew)).setText(details.firstName);
                        ((TextView) view1.findViewById(R.id.lastNameEditNew)).setText(details.lastName);
                        ((TextView) view1.findViewById(R.id.loginEditNew)).setText(details.login);
                        ((TextView) view1.findViewById(R.id.birthdayEditNew)).setText(details.birthday);
                        ImageView avatar = getChildFragmentManager().findFragmentById(R.id.camera_edit_fragment_new)
                                .getView()
                                .findViewById(R.id.avatar);
                        StorageReference ref = mStorage.getReference("images/" + userId + "/avatar.png");
                        ref.getBytes(Long.MAX_VALUE).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                avatar.setImageDrawable(
                                        new BitmapDrawable(BitmapFactory.decodeByteArray(task1.getResult(), 0, task1.getResult().length))
                                );
                            }
                        });
                    }
                });
        Button submit = getView().findViewById(R.id.editSubmitNew);
        submit.setOnClickListener((v) -> submitEdit());
        getView().findViewById(R.id.resetPassNew).setOnClickListener((v) -> {
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            startActivity(intent);
        });

        /*Button musicPlayer=findViewById(R.id.musicPlayer);
        musicPlayer.setOnClickListener(v -> musicPlayer());

        Button weather=findViewById(R.id.weather);
        weather.setOnClickListener(v -> weather());*/
    }
    private void writeDetails(UserDetails details) {

        ((TextView) getView().findViewById(R.id.nameEditNew)).setText(details.firstName);
        ((TextView) getView().findViewById(R.id.lastNameEditNew)).setText(details.lastName);
        ((TextView) getView().findViewById(R.id.loginEditNew)).setText(details.login);
        ((TextView) getView().findViewById(R.id.birthdayEditNew)).setText(details.birthday);
        ImageView avatar = getChildFragmentManager().findFragmentById(R.id.camera_edit_fragment_new)
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
        System.out.println("HERE1111");
        BitmapDrawable avatar = (BitmapDrawable)((ImageView)getChildFragmentManager().findFragmentById(R.id.camera_edit_fragment_new)
                .getView()
                .findViewById(R.id.avatar))
                .getDrawable();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        avatar.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);

        writeUserData(userId,
                ((TextView) getView().findViewById(R.id.nameEditNew)).getText().toString(),
                ((TextView) getView().findViewById(R.id.lastNameEditNew)).getText().toString(),
                ((TextView) getView().findViewById(R.id.loginEditNew)).getText().toString(),
                ((TextView) getView().findViewById(R.id.birthdayEditNew)).getText().toString(),
                baos.toByteArray()
        );
    }
    private void writeUserData(String userId, String firstName, String lastName, String login, String birthday,  byte[] img) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*private DatabaseReference mDatabase;

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
    private void musicPlayer() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    private void weather() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
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
        Button resetPassword = findViewById(R.id.resetPass);
        mDatabase.child("details/" + userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails details = task.getResult().getValue(UserDetails.class);
                        writeDetails(details);
                    }
                });
        submit.setOnClickListener((v) -> submitEdit());
        resetPassword.setOnClickListener((v) -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        Button musicPlayer=findViewById(R.id.musicPlayer);
        musicPlayer.setOnClickListener(v -> musicPlayer());

        Button weather=findViewById(R.id.weather);
        weather.setOnClickListener(v -> weather());
    }

    private void writeDetails(UserDetails details) {

        ((TextView) findViewById(R.id.nameEdit)).setText(details.firstName);
        ((TextView) findViewById(R.id.lastNameEdit)).setText(details.lastName);
        ((TextView) findViewById(R.id.loginEdit)).setText(details.login);
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

    private void writeUserData(String userId, String firstName, String lastName, String login, String birthday,  byte[] img) {
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
    }*/
}