package com.mobiletracker.scarTU.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mobiletracker.scarTU.models.QRScanner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobiletracker.scarTU.R;
import com.mobiletracker.scarTU.includes.MyToolbar;
import com.mobiletracker.scarTU.models.QRScanner;
import com.mobiletracker.scarTU.providers.AuthProvider;
import com.mobiletracker.scarTU.providers.DriverProvider;

public class ScannerQRActivity extends AppCompatActivity {
    private  DatabaseReference dbref;

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;
    private DatabaseReference mDatabase;
    private TextView txtDataSet;
    private Button mButtonScannear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_q_r);
        mButtonScannear = findViewById(R.id.btnScanner);
        txtDataSet = findViewById(R.id.textLabelView);
       IntentIntegrator integrator = new IntentIntegrator(this);
       mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("LecturasQR");
       MyToolbar.show(this, "Scanner QR", true);
       integrator.setOrientationLocked(false);
       integrator.setTimeout(8000);
       integrator.initiateScan();



    }

    //llamar metodo Result


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //llamar
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        //obtener la informacion String

        if (!result.getContents().isEmpty())
        {
            String datos = result.getContents();
            saveQR(datos);
        }else
            {
                Intent intent = new Intent(ScannerQRActivity.this, ScannerQRActivity.class);
                startActivity(intent);
            }



        //txtDataSet.setText(datos);
        //dbref.push().setValue(data)


        //        mDatabase.child("Users").child("QR").push().setValue(datos);

    }


    void saveQR(String Datos)
    {

        QRScanner qrScanner = new QRScanner();
        qrScanner.setDatos(Datos);

        mDatabase.push().setValue(Datos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(ScannerQRActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ScannerQRActivity.this, MapDriverActivity.class);
                    //startActivity(intent);
                    txtDataSet.setText(Datos);
                }
                else{
                    Toast.makeText(ScannerQRActivity.this, "Fallo el registro", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


}