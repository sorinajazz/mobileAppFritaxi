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

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText customer_email, customer_password;
    private Button customer_login, customer_register;
    private TextView noAccount;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        customer_email = (EditText) findViewById(R.id.Email_btn1);
        customer_password = (EditText) findViewById(R.id.Password_btn1);
        customer_login = (Button) findViewById(R.id.Login_btn1);
        customer_register = (Button) findViewById(R.id.Register_btn1);
        noAccount = (TextView) findViewById(R.id.create_btn1);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null) {
                    Intent intent = new Intent(CustomerLoginActivity.this, CustomerMap2.class );
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        customer_login.setVisibility(View.VISIBLE);
        customer_register.setVisibility(View.INVISIBLE);
        customer_register.setEnabled(false);

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_login.setVisibility(View.INVISIBLE);
                noAccount.setVisibility(View.INVISIBLE);

                customer_register.setVisibility(View.VISIBLE);
                customer_register.setEnabled(true);
            }
        });



        customer_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = customer_email.getText().toString();
                final String password = customer_password.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(CustomerLoginActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(CustomerLoginActivity.this, "Please write password...", Toast.LENGTH_SHORT).show();
                }

                else {

                    loadingBar.setTitle("customer Registration");
                    loadingBar.setMessage("Please wait while we create your account");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(CustomerLoginActivity.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                            } else {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                current_user_db.setValue(true);

                            }

                        }
                    });
                }
            }
        });

        customer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = customer_email.getText().toString();
                final String password = customer_password.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(CustomerLoginActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(CustomerLoginActivity.this, "Please write password...", Toast.LENGTH_SHORT).show();
                }

                else {

                    loadingBar.setTitle("customer Login");
                    loadingBar.setMessage("Please wait..");
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(CustomerLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CustomerLoginActivity.this, "driver Login Successful", Toast.LENGTH_SHORT).show();
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
