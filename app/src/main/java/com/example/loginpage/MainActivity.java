package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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

import at.favre.lib.crypto.bcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    EditText emailEt, passEt;
    TextView emailError, passError;
    TextView registertv;
    LinearLayout linearLayout;
    CheckBox box;
    Button loginButton;

    SharedPreferences prf;
    SharedPreferences.Editor editor;

    String emailSp;
    String passwordSp;

    String email;
    String password;
    String hashedCode;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prf = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        editor = prf.edit();

        emailSp = prf.getString("email","");
        passwordSp = prf.getString("password","");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        emailEt = findViewById(R.id.email);
        passEt = findViewById(R.id.password);

        registertv = findViewById(R.id.register);
        registertv.setPaintFlags(registertv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        emailEt.setText(emailSp);
        passEt.setText(passwordSp);

        emailError = findViewById(R.id.emailError);
        passError = findViewById(R.id.passError);

        box = findViewById(R.id.box);
        box.setChecked(true);

        linearLayout = findViewById(R.id.linlayout);
        loginButton = findViewById(R.id.loginbtn);

        mAuth = FirebaseAuth.getInstance();

    }

    public void login(View view){

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
            passError.setText("*password cannot be less than 8 characters");
            passEt.setText("");
            linearLayout.setBackgroundResource(R.drawable.seterror);
            passEt.requestFocus();
        }
        else{
            passError.setText("");
            emailError.setText("");
            loginButton.setEnabled(false);
            emailEt.setBackgroundResource(R.drawable.gradientcolor2);
            linearLayout.setBackgroundResource(R.drawable.gradientcolor2);

            hashedCode = hashIt(password);

            if(box.isChecked()){
                editor.putString("email",email);
                editor.putString("password",password);
            }
            else{
                editor.putString("email","");
                editor.putString("password","");
            }
            editor.apply();

            loginToFirebase();

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


    public void loginToFirebase(){

        mAuth.signInWithEmailAndPassword(email, hashedCode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    loginButton.setEnabled(true);
                    Toast.makeText(MainActivity.this, "oops! error occurred",Toast.LENGTH_LONG).show();
                }
                else{
                    loginButton.setEnabled(true);
                    Intent i = new Intent(MainActivity.this, oops_activity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

    }

    public void register(View view){
        Intent i = new Intent(MainActivity.this, Register_activity.class);
        startActivity(i);
    }


    @Override
    protected void onStart() {
        super.onStart();
        loginButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}