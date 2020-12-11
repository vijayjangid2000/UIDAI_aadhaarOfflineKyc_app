package com.vijayjangid.aadharkyc;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GetUserData extends AppCompatActivity {

    EditText nameTv, mobileTv, emailTv, fatherTv, genderTv, birthDateTv;
    String name, mobile, email, father, gender, birthDate;
    Button nextButton;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_data);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(GetUserData.this, "Please Allow to use storage"
                    , Toast.LENGTH_LONG).show();
            storage();
        }

        nameTv = findViewById(R.id.nameTv);
        mobileTv = findViewById(R.id.mobileTv);
        emailTv = findViewById(R.id.emailTv);
        fatherTv = findViewById(R.id.fatherTv);
        genderTv = findViewById(R.id.genderTv);
        birthDateTv = findViewById(R.id.birthdateTv);
        nextButton = findViewById(R.id.nextButton);

        // if user data already exists then we will set the data as hint
        sp = getSharedPreferences("User", MODE_PRIVATE);
        name = sp.getString("name", "");
        mobile = sp.getString("mobile", "");
        email = sp.getString("email", "");
        father = sp.getString("father", "");
        gender = sp.getString("gender", "");
        birthDate = sp.getString("birthDate", "");


        if (name.length() != 0 && mobile.length() != 0 && email.length() != 0
                && father.length() != 0 && gender.length() != 0 && birthDate.length() != 0) {
            nameTv.setText(name);
            mobileTv.setText(mobile);
            emailTv.setText(email);
            fatherTv.setText(father);
            genderTv.setText(gender);
            birthDateTv.setText(birthDate);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                gotoNextPage();
            }
        });


    }

    void saveUserData() {
        editor = sp.edit();
        name = nameTv.getText().toString().trim().toLowerCase();
        mobile = mobileTv.getText().toString().trim().toLowerCase();
        email = emailTv.getText().toString().trim().toLowerCase();
        father = fatherTv.getText().toString().trim().toLowerCase();
        gender = genderTv.getText().toString().trim().toLowerCase();
        birthDate = birthDateTv.getText().toString().trim().toLowerCase();

        editor.putString("name", name);
        editor.putString("mobile", mobile);
        editor.putString("email", email);
        editor.putString("father", father);
        editor.putString("gender", gender);
        editor.putString("birthDate", birthDate);
        editor.apply();
    }

    void gotoNextPage() {
        startActivity(new Intent(GetUserData.this, WebPage.class));
    }

    public void storage() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }
    }

}