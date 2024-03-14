package com.msa.testingmsa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    MaterialEditText username , password , email ;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference  reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        btn_register = findViewById(R.id.btn_register);
        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_register.setClickable(false);
                String textUsername , textPassword , textemail ;
                textUsername = username.getText().toString();
                textemail = email.getText().toString();
                textPassword = password.getText().toString();

                if(textemail.isEmpty() || textPassword.isEmpty() || textUsername.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                    btn_register.setClickable(true);
                }else if(textPassword.length() < 6){
                    Toast.makeText(getApplicationContext(),"password must contain atleast 6 characters",Toast.LENGTH_LONG).show();
                    btn_register.setClickable(true);
                }else {
                    register(textUsername,textPassword,textemail);
                }
            }
        });
    }

    private void register(final String username , String password ,String email ){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String , String>  hashmap = new HashMap<>();
                            hashmap.put("id",userid);
                            hashmap.put("Username",username);
                            hashmap.put("imageURL","default");
                            hashmap.put("status","offline");
                            hashmap.put("search",username.toLowerCase());

                            reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        sendEmailVerification();
                                    }
                                }
                            });
                        }else{
                            btn_register.setClickable(true);
                            Toast.makeText(getApplicationContext(),"Registration failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),"Can't register with this G-mail or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Registration Successful Vreification link has been sent to U'r Mail", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        auth.signOut();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Verification link sending Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
