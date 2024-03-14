package com.msa.testingmsa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
EditText gmail;
Button reset;
FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        gmail = findViewById(R.id.gmail);
        reset = findViewById(R.id.reset);
        firebaseAuth = FirebaseAuth.getInstance();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = gmail.getText().toString().trim();
                if(userEmail.isEmpty()){
                    Toast.makeText(ResetPassActivity.this, "Enter your E-mail",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassActivity.this, "Password Reset link sent to U'r E-Mail",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ResetPassActivity.this, StartActivity.class));
                            }else{
                                Toast.makeText(ResetPassActivity.this, "Error in Sending Email Make Shure that U've Registered your E-mail id",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}