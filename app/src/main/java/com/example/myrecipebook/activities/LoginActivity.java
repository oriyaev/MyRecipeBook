package com.example.myrecipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.db.UserDao;
import com.example.myrecipebook.models.User;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "myrecipebook.db")
                .allowMainThreadQueries()
                .build();

        userDao = db.userDao();

        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            User user = userDao.getUserByEmailAndPassword(email, password);

            if (user != null) {
                Intent intent = new Intent(this, RecipeListActivity.class);
                intent.putExtra("userId", user.id);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegister.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}