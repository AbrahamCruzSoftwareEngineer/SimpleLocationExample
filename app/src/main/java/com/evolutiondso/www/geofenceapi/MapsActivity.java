package com.evolutiondso.www.geofenceapi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng Dantanas, Publix, Starbucks, CasaCerca;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    double dist;
    double[] latitud = {33.9362908, 33.9362908, 33.935961, 33.938952};
    double[] longitud = {-84.3782817, -84.3804704, -84.376694, -84.368465};
    String[] lugares ={"Dantanas","Publix","HairCut","Starbucks"};

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        //UiSettings of the map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Add a markers
        Dantanas = new LatLng(latitud[0], longitud[0]);
        mMap.addMarker(new MarkerOptions().position(Dantanas).title(lugares[0]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Dantanas));

        Publix = new LatLng(latitud[1], longitud[1]);
        mMap.addMarker(new MarkerOptions().position(Publix).title(lugares[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Publix));

        Starbucks = new LatLng(latitud[2], longitud[2]);
        mMap.addMarker(new MarkerOptions().position(Starbucks).title(lugares[2]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Starbucks));

        CasaCerca = new LatLng(latitud[3], longitud[3]);
        mMap.addMarker(new MarkerOptions().position(CasaCerca).title(lugares[3]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CasaCerca));


        miUbicacion();
    }

    private void agregaMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("ME").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizaUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregaMarcador(lat, lng);



            //Actualiza en donde poner el circulo
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(lat, lng))
                    .radius(200)
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor(Color.argb(100, 150,150,150))
                    );

            //Actualiza la distancia
            distanceUpdate();
        }

    }

    private void distanceUpdate(){
        Location me   = new Location("");
        Location dest = new Location("");

        me.setLatitude(lat);
        me.setLongitude(lng);

        // va a loopear las distancias
        for (int i = 0; i < latitud.length; i++) {
            dest.setLatitude(latitud[i]);
            dest.setLongitude(longitud[i]);
            dist = me.distanceTo(dest);
            if (dist <= 200) {
                Toast.makeText(this, "Estas cerca de " + lugares[i]+ " Aprovecha tu recompensa!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            checkLocationPermission();
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizaUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizaUbicacion(location);

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
    };




    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                     android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }
                        ,MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Toast.makeText(this, "Oh Yeah!", Toast.LENGTH_LONG).show();
//                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    actualizaUbicacion(location);
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, (android.location.LocationListener) locListener);


                    mMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
