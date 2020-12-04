package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Register_activity extends AppCompatActivity {

    EditText emailEt, passEt;
    TextView emailError, passError;
    LinearLayout linearLayout;

    String email;
    String password;
    String hashedCode;

    Button registerButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        emailEt = findViewById(R.id.email);
        passEt = findViewById(R.id.password);

        registerButton = findViewById(R.id.regbtn);

        emailError = findViewById(R.id.emailError);
        passError = findViewById(R.id.passError);
        linearLayout = findViewById(R.id.linlayout);

        mAuth = FirebaseAuth.getInstance();

    }

    public void Register(View view){

        email = emailEt.getText().toString().trim();
        password = passEt.getText().toString().trim();

        if(email.isEmpty()){
            emailError.setText("*this field cannot be empty");
            emailEt.setText("");
            emailEt.setBackgroundResource(R.drawable.seterror);
            emailEt.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailError.setText("*provide a valid email");
            emailEt.setText("");
            emailEt.setBackgroundResource(R.drawable.seterror);
            emailEt.requestFocus();
        }

        else if(password.isEmpty()){
            passError.setText("*this field cannot be empty");
            passEt.setText("");
            linearLayout.setBackgroundResource(R.drawable.seterror);
            passEt.requestFocus();
        }

        else if(password.length() < 8){
            passError.setText("*weak password");
            passEt.setText("");
            linearLayout.setBackgroundResource(R.drawable.seterror);
            passEt.requestFocus();
        }
        else {
            registerButton.setEnabled(false);
            passError.setText("");
            emailError.setText("");
            emailEt.setBackgroundResource(R.drawable.gradientcolor2);
            linearLayout.setBackgroundResource(R.drawable.gradientcolor2);

            hashedCode = hashIt(password);

            registerToFirebase();
        }

    }

    public void show(View view){
        passEt.setTransformationMethod(null);
    }


    public String hashIt(String password){

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] resultByteArray = messageDigest.digest();

            StringBuilder sb = new StringBuilder();

            for(byte b : resultByteArray){
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "//";
    }


    public void registerToFirebase(){

        mAuth.createUserWithEmailAndPassword(email, hashedCode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Register_activity.this, "oops! error occurred",Toast.LENGTH_LONG).show();
                }
                else{
                    registerButton.setEnabled(true);
                    Intent i = new Intent(Register_activity.this, oops_activity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        registerButton.setEnabled(true);
    }

}