package com.mobiletracker.scarTU.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mobiletracker.scarTU.R;
import com.mobiletracker.scarTU.activities.driver.RegisterDriverActivity;
import com.mobiletracker.scarTU.includes.MyToolbar;

import dmax.dialog.SpotsDialog;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText mEditTextEmail;
    private Button mButtonResetPassword;
    private String email ="";

    private FirebaseAuth mAuth;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        MyToolbar.show(this, "Restablecer Contraseña", true);
        mDialog = new SpotsDialog.Builder().setContext(ResetPasswordActivity.this).setMessage("Espere un momento").build();

        mAuth = FirebaseAuth.getInstance();

        //
        mEditTextEmail = (EditText) findViewById(R.id.TextInputEmailReset);
        mButtonResetPassword = (Button) findViewById(R.id.btnResetPassword);

        mButtonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();

                if (!email.isEmpty())
                {
                    mDialog.show();
                    resetPassword();
                }else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Debe Ingresar un correo", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

    private void resetPassword()
    {
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.hide();
                if (task.isSuccessful())
                {
                    Toast.makeText(ResetPasswordActivity.this, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                    returnLogin();
                }else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

    private void returnLogin()
    {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}