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

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email,password;
    Button btn_login;
    Button forgot_pass;
    public String emailID;
    FirebaseAuth auth;
    DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        forgot_pass = findViewById(R.id.btn_forgot_passwoord);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login =findViewById(R.id.btn_login);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textemail , textpassword;
                textemail = email.getText().toString();
                textpassword = password.getText().toString();

                if(textemail.isEmpty() || textpassword.isEmpty()){
                    Toast.makeText(getApplicationContext() , "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    Glogin(textemail, textpassword);
                }
            }
        });
    }



    private void Glogin(String email,String password){
        emailID = email;
        auth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            checkEmailVerification();
                        }else{
                            Toast.makeText(getApplicationContext() , "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = auth.getInstance().getCurrentUser();
        if(firebaseUser.isEmailVerified()){

            String currentUserId = auth.getCurrentUser().getUid();
            UserRef.child(currentUserId).child("emailID")
                    .setValue(emailID);

            Intent intent = new Intent(getApplicationContext() , MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Please Verify your Email first",Toast.LENGTH_SHORT).show();
            auth.signOut();
        }
    }

}
