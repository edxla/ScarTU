package com.mobiletracker.scarTU.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiletracker.scarTU.R;
import com.mobiletracker.scarTU.SQLiteDB.HelperSQLite;
import com.mobiletracker.scarTU.activities.MainActivity;
import com.mobiletracker.scarTU.adapters.PopupAdapter;
import com.mobiletracker.scarTU.includes.MyToolbar;
import com.mobiletracker.scarTU.providers.AuthProvider;
import com.mobiletracker.scarTU.providers.ClockProvider;
import com.mobiletracker.scarTU.providers.CloudFireStoreProvider;
import com.mobiletracker.scarTU.providers.DriverProvider;
import com.mobiletracker.scarTU.providers.GeofireProvider;
import com.mobiletracker.scarTU.services.ForegroundService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeofireProvider mGeofireProvider;
    private ClockProvider mClockProvider;
    private DriverProvider mDriverProvider;
    private FirebaseFirestore mFirestore;

    private FirebaseAuth mAuth;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;
    private List<Marker> mDriversMarkers = new ArrayList<>();

    private Button mButtonConnect;
    private Button mButtonLocation;
    private boolean mIsConnect = false;

    private LatLng mCurrentLatLng;

    private boolean mIsFirstTime = true;

    private HashMap<String, String> mImagesMarkers = new HashMap<String, String>();
    private int mCounter = 0;


    //Reloj
    private Timer timer;
    //    private Handler mHandler;
    private Calendar mCalendar;
    private String horaGlobal;

    //Doubles latitud
    private double mlatitud;
    private double mlongutud;

    //Variables cronometro
    CountDownTimer cTimer = null;
    private boolean mTempActive = false;

    //ofline
    HelperSQLite DB;


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location: locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mMarker != null) {
                        mMarker.remove();
                    }

                    /*mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("Tu posicion")
                                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location))


                    );
                    // OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    */



                    updateLocation();
                    //refresh_date_drivers();
                    //startTimer();

                    ejecutaSubidaDatos();


                    if (mIsFirstTime) {
                        mIsFirstTime = false;

                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(14.99f)
                                        .tilt(0)
                                        .bearing(670)
                                        .build()
                                //.target(new LatLng(location.getLatitude(), location.getLongitude()))
                                //.zoom(16f)
                                //.build()
                        ));
                        getActiveDrivers();
                        ;
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);
        MyToolbar.show(this, "Conductor", false);

        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider();
        mDriverProvider = new DriverProvider();
        mAuth = FirebaseAuth.getInstance();

        //IntentoHora
        mClockProvider = new ClockProvider();
        mFirestore = FirebaseFirestore.getInstance();

        DB = new HelperSQLite(this);


        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mButtonConnect = findViewById(R.id.btnConnect);
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsConnect) {
                    stopServices();
                    disconnect();
                }
                else {
                    startServices();
                    startLocation();
                }
            }
        });

        mButtonLocation = findViewById(R.id.btnCameralocation);
        mButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerPosition();

            }
        });

        //TImer





        //Todo Sobre Reloj
        timer = new Timer();// Crear objeto de temporizador
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v("Timer","run()...");
                mCalendar = Calendar.getInstance(); //America/Mexico_City
                //
                //TimeZone myTimeZone = TimeZone.getTimeZone("America/Mexico_City");
                //
                mCalendar.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);// HOUR son 12 horas HOUR_OF_DAY son 24 horas
                int minute = mCalendar.get(Calendar.MINUTE);//minuto
                int second = mCalendar.get(Calendar.SECOND) + 1;//Segundos
                if (second == 60) {
                    minute += 1;
                    second = 0;
                }
                if (minute == 60){
                    hour += 1;
                    minute = 0;
                }
                if (hour == 12){
                    hour = 0;
                }
                String time = String.format("%d:%02d:%02d", hour, minute, second);
                mCalendar.set(Calendar.SECOND, second);
                mCalendar.set(Calendar.MINUTE, minute);
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);

                Message message=new Message();
                message.what=0;
                message.obj=time;
                mHandler.sendMessage(message);

            }
        },0,1000);

    }


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.v("Timer","handleMessage()..");
            super.handleMessage(msg);
            String str=(String)msg.obj;
            horaGlobal = str;
            //tvTime.setText(str);
        }
    };





    private void centerPosition() {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(mCurrentLatLng.latitude, mCurrentLatLng.longitude))
                        .zoom(15f)
                        .build()
        ));
    }

    private void updateLocation() {
        if (mAuthProvider.existSession() && mCurrentLatLng != null) {
            mGeofireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
            mlatitud = mCurrentLatLng.latitude;
            mlongutud = mCurrentLatLng.longitude;
            //refresh_date_drivers();
            //startTimer();
            ejecutaSubidaDatos();




        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                    else {
                        showAlertDialogNOGPS();
                    }
                }
                else {
                    checkLocationPermissions();
                }
            }
            else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived())  {
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
        else {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    private void disconnect() {

        if (mFusedLocation != null) {
            mButtonConnect.setText("Conectarse");
            mIsConnect = false;
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if (mAuthProvider.existSession()) {
                mGeofireProvider.removeLocation(mAuthProvider.getId());

            }
        }
        else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mButtonConnect.setText("Desconectarse");
                    mIsConnect = true;
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }
                else {
                    showAlertDialogNOGPS();
                }
            }
            else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
            else {
                showAlertDialogNOGPS();
            }
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        if (item.getItemId() == R.id.action_update) {
            Intent intent = new Intent(MapDriverActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.action_createQR) {
            Intent intent = new Intent(MapDriverActivity.this, DriverGenerateQRActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.action_readQR) {
            Intent intent = new Intent(MapDriverActivity.this, ScannerQRActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        disconnect(); //Elimar datos de ubicacion
        mAuthProvider.logout();
        Intent intent = new Intent(MapDriverActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Conductores activos
    private void getActiveDrivers()
    {
        mGeofireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //AÑADIREMOS LOS MARCADORES DE LOS CODUCTORES QUE SE CONECTEN
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            return;
                        }
                    }
                }

                LatLng driverLatlng = new LatLng(location.latitude, location.latitude);






                    Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatlng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)));
                    marker.setTag(key);
                    mDriversMarkers.add(marker);
                    getDriverInfo();






            }

            @Override
            public void onKeyExited(String key) {
                //AÑADIREMOS LOS MARCADORES DE LOS CODUCTORES QUE SE CONECTEN
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Actualizar la posicion de cada conductor
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


        //CLocl


    }

    private void getDriverInfo() {
        mCounter = 0;
        for (final Marker marker: mDriversMarkers) {
            mDriverProvider.getDriver(marker.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounter = mCounter + 1;
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("name")) {
                            String name = snapshot.child("name").getValue().toString();
                            marker.setTitle(name);
                        }
                        if (snapshot.hasChild("image")) {
                            String image = snapshot.child("image").getValue().toString();
                            mImagesMarkers.put(marker.getTag().toString(), image);

                        }else {
                            mImagesMarkers.put(marker.getTag().toString(), null);
                        }
                    }
                    // TERMINO DE TRAER TODA LA INFORMACION DE LOS CONDUCTORES
                    if (mCounter == mDriversMarkers.size()) {
                        mMap.setInfoWindowAdapter(new PopupAdapter(MapDriverActivity.this, getLayoutInflater(), mImagesMarkers));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void startServices()
    {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(MapDriverActivity.this, serviceIntent);
    }

    private void stopServices()
    {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    private void refresh_date_drivers()
    {
        String tfechaDispositivo = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        mClockProvider.setIdConductor(mAuthProvider.getId().toString().trim());
        mClockProvider.setLatitud(mlatitud);
        mClockProvider.setLongitud(mlongutud);
        mClockProvider.setHoraNTP(horaGlobal);
        mClockProvider.setFechaDispositivo(tfechaDispositivo);

        mClockProvider.updateClock(mClockProvider).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(MapDriverActivity.this, "Subiendo", Toast.LENGTH_SHORT).show();

            }
        });

    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    void startTimer() {
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
               Toast.makeText(MapDriverActivity.this, "Segundos Flatantes : "+ millisUntilFinished/100, Toast.LENGTH_SHORT).show();


            }

            public void onFinish() {
                //ejecutaSubidaDatos();
                mTempActive = true;

            }
        }.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();// Cerrar temporizador
        }
    }

    //CLoud
    private void cloudStoneStorage() {
        Map<String, Object> mapCloudStorage = new HashMap<>();
        mapCloudStorage.put("Hora", horaGlobal);
        mapCloudStorage.put("latitud", mlatitud);
        mapCloudStorage.put("longitud", mlongutud);
        mapCloudStorage.put("idConductor", mAuthProvider.getId().toString().trim());

        mFirestore.collection("Ubicaciones").add(mapCloudStorage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MapDriverActivity.this, "Subido Correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapDriverActivity.this, "Ubo un problema con tu conexion a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InternalStorage()
    {
        String idConductorStorage = mAuthProvider.getId().toString().trim();
        String tfecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String tTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Boolean checkinsertdata = DB.insertarDatos(idConductorStorage, mlatitud, mlongutud,tfecha,tTime);
        if(checkinsertdata==true)
            Toast.makeText(MapDriverActivity.this, "Dato Insertado", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MapDriverActivity.this, "Dato no insertado", Toast.LENGTH_SHORT).show();
    }

    private void ejecutaSubidaDatos()
    {
        refresh_date_drivers();
        InternalStorage();
        //cloudStoneStorage();
        //refresh_date_drivers();
    }

}
