package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class AddressPage extends AppCompatActivity implements OnMapReadyCallback {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;
    EditText e2,e3;
    Spinner e1;
    Button b1,b2;
    private GoogleMap mMap;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    Button btn;
    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int LOCATION_REQUEST_CODE = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_page);
        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        b2 = findViewById(R.id.submitAddress);

        e1 = findViewById(R.id.streetName);
        e2 = findViewById(R.id.FlatNo);
        e3  = findViewById(R.id.Locality);

        String[] local = {"Shashtri Nagar","Vidhyadhar Nagar", "Bani Park", "MurliPura"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddressPage.this,R.layout.support_simple_spinner_dropdown_item,local);
        e1.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(AddressPage.this);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddressPage.this, "select location in map", Toast.LENGTH_SHORT).show();
            }
        });

        if (ActivityCompat.checkSelfPermission(AddressPage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddressPage.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);

        }else{
            askLocationPermission();
        }
    }

    private void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(AddressPage.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddressPage.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(AddressPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }else{
                ActivityCompat.requestPermissions(AddressPage.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                ltlng, 16f);
                        mMap.animateCamera(cameraUpdate);

                    }

                });
                Location location = mMap.getMyLocation();

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);

                        markerOptions.title(getAddress(latLng));
                        mMap.clear();
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                latLng, 15);
                        mMap.animateCamera(location);
                        mMap.addMarker(markerOptions);
                    }
                });

            }else{

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


    private String getAddress(LatLng latLng){

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(AddressPage.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new ConfirmAddress();
            Bundle args = new Bundle();
            args.putDouble("lat", latLng.latitude);
            args.putDouble("long", latLng.longitude);
            String latitude = String.valueOf(latLng.latitude);
            String longitude = String.valueOf(latLng.longitude);
            savedataInFirebase(e1,e2,e3,latitude,longitude);
            dialogFragment.setArguments(args);
            dialogFragment.show(ft, "dialog");
            return address;

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }


    }

    private void savedataInFirebase(Spinner e1, EditText e2, EditText e3, String latitude, String longitude) {
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(e2.getText().toString().isEmpty() || e3.getText().toString().isEmpty())){
                    final String Random = UUID.randomUUID().toString();
                    String link = "https://www.google.com/maps/search/?api=1&query=+"+latitude+","+longitude;
                    DocumentReference doc = fstore.collection("saved_address").document(Random);
                    Map<String,String> info = new HashMap<>();
                    info.put("user_mail",fauth.getCurrentUser().getEmail());
                    info.put("user_location",link);
                    info.put("user_home",e2.getText().toString());
                    info.put("user_street",e3.getText().toString());
                    info.put("user_area",e1.getSelectedItem().toString());
                    info.put("address_id",Random);
                    doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            doc.update("created_at", FieldValue.serverTimestamp());
                            Toast.makeText(AddressPage.this, "Successfully added to saved address", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),SavedAddress.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddressPage.this, "Failed To Add", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),SavedAddress.class));
                        }
                    });
                }else{
                    Toast.makeText(AddressPage.this, "fill all entries", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
