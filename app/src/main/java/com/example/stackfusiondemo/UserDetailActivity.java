package com.example.stackfusiondemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class UserDetailActivity extends AppCompatActivity implements LocationListener {

    String firstName, lastName, email, birthday, gender, location, imageUrl, fbLocation;
    TextView firstNameView, lastNameView, emailView, birthdayView, genderView, locationView, fbLocationView;
    ImageView userProfile;
    Button logOut;

    final int MY_PERMISSIONS_REQUEST_LOCATION = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Check if location permission is granted if not ask user to grant it
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(UserDetailActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return;
        }

        // Retrieving the data from previous activity
        firstName = getIntent().getStringExtra("first name");
        lastName = getIntent().getStringExtra("last name");
        email = getIntent().getStringExtra("email");
        gender = getIntent().getStringExtra("gender");
        birthday = getIntent().getStringExtra("birthday");
        fbLocation = getIntent().getStringExtra("fb location");
        imageUrl = getIntent().getStringExtra("image url");

        firstNameView = (TextView) findViewById(R.id.first_name);
        lastNameView = (TextView) findViewById(R.id.last_name);
        emailView = (TextView) findViewById(R.id.email_address);
        genderView = (TextView) findViewById(R.id.gender);
        birthdayView = (TextView) findViewById(R.id.birthday);
        fbLocationView = (TextView) findViewById(R.id.fb_location);
        userProfile = (ImageView) findViewById(R.id.user_profile);
        locationView = (TextView) findViewById(R.id.current_location);
        logOut = (Button) findViewById(R.id.log_out);

        // Getting latitude and longitude to get address
        if (locationManager != null) {
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0,this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);


            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);


            if (location == null) {
                Toast.makeText(getApplicationContext(), "GPS signal not found",
                        Toast.LENGTH_LONG).show();
            }
            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Using Geocoder to find address by passing latitude and longitude
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(UserDetailActivity.this);

                    if (latitude != 0 || longitude != 0) {
                        addresses = geocoder.getFromLocation(latitude ,
                                longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getAddressLine(1);
                        String country = addresses.get(0).getAddressLine(2);
                        Log.d("TAG", "address = "+address+", city ="+city+", country = "+country );
                        //Toast.makeText(this, "address = "+address+", city ="+city+", country = "+country, Toast.LENGTH_LONG).show();
                        String completeAddress = address+city+country;
                        locationView.setText(completeAddress);

                    }
                    else {
                        Toast.makeText(this, "latitude and longitude are null",
                                Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }

        // setting the user details in fields
        firstNameView.setText(firstName);
        lastNameView.setText(lastName);
        emailView.setText(email);
        genderView.setText(gender);
        birthdayView.setText(birthday);
        fbLocationView.setText(fbLocation);
        Picasso.get().load(imageUrl).into(userProfile);


        // Logout from facebook completly
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginManager.getInstance().logOut();

                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }

                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        LoginManager.getInstance().logOut();

                    }
                }).executeAsync();

                Intent i = new Intent(UserDetailActivity.this, DashboardActivity.class);
                finish();
                startActivity(i);
            }
        });


    }



    // Get the result of the location permission prompt shown to user
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Recreate activity to apply changes
                    recreate();

                    // permission was granted

                } else {

                    Intent myIntent = new Intent(this, DashboardActivity.class);
                    finish();
                    startActivity(myIntent);

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }

            }

        }
    }

// Location listner callbacks which is auto generated
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
