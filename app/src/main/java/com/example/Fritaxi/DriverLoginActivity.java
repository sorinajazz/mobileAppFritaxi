package com.example.Fritaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity {

    private EditText driver_email, driver_password;
    private Button driver_login, driver_register;
    private TextView noAccount;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        driver_email = (EditText) findViewById(R.id.Email_btn);
        driver_password = (EditText) findViewById(R.id.Password_btn);
        driver_login = (Button) findViewById(R.id.Login_btn);
        driver_register = (Button) findViewById(R.id.Register_btn);
        noAccount = (TextView) findViewById(R.id.create_btn);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null) {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class );
                   startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        driver_login.setVisibility(View.VISIBLE);
        driver_register.setVisibility(View.INVISIBLE);
        driver_register.setEnabled(false);

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_login.setVisibility(View.INVISIBLE);
                noAccount.setVisibility(View.INVISIBLE);

                driver_register.setVisibility(View.VISIBLE);
                driver_register.setEnabled(true);
            }
        });



        driver_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = driver_email.getText().toString();
                final String password = driver_password.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(DriverLoginActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(DriverLoginActivity.this, "Please write password...", Toast.LENGTH_SHORT).show();
                }

                else {

                    loadingBar.setTitle("Driver Registration");
                    loadingBar.setMessage("Please wait while we create your account");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(DriverLoginActivity.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                            } else {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                                current_user_db.setValue(email);

                            }

                        }
                    });
                }
            }
        });

        driver_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = driver_email.getText().toString();
                final String password = driver_password.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(DriverLoginActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(DriverLoginActivity.this, "Please write password...", Toast.LENGTH_SHORT).show();
                }

                else {

                    loadingBar.setTitle("Driver Login");
                    loadingBar.setMessage("Please wait..");
                    loadingBar.show();


                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(DriverLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DriverLoginActivity.this, "driver Login Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });


                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
