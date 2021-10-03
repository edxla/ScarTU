package com.mobiletracker.scarTU.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobiletracker.scarTU.R;
import com.mobiletracker.scarTU.includes.MyToolbar;
import com.mobiletracker.scarTU.models.Driver;
import com.mobiletracker.scarTU.providers.AuthProvider;
import com.mobiletracker.scarTU.providers.DriverProvider;

public class DriverGenerateQRActivity extends AppCompatActivity {

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;

    ImageView img_salida;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_generate_q_r);
        mDriverProvider = new DriverProvider();
        mAuthProvider = new AuthProvider();
        Driver drivers = new Driver();
        MyToolbar.show(this,"Código QR",true);
        img_salida = findViewById(R.id.img_salida);



        //Generar QR

        String Stext = mAuthProvider.getId().toString().trim();

        //Stext = "{\"qr_code_text\": \"https://www.qr-code-generator.com/\"}";

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(Stext, BarcodeFormat.QR_CODE, 350,350);
            BarcodeEncoder encoder = new BarcodeEncoder();


            Bitmap bitmap = encoder.createBitmap(matrix);
            img_salida.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            //Esconder
        }catch (WriterException e)
        {
            e.printStackTrace();
        }
    }
}