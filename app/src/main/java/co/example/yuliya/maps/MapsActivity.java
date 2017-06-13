package co.example.yuliya.maps;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.example.yuliya.maps.domain.Category;
import co.example.yuliya.maps.service.DataService;


public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, OnMarkerDragListener, LocationListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    public static Map<String, co.example.yuliya.maps.domain.Location> marks = new HashMap<>();
    public static Map<String, co.example.yuliya.maps.domain.Location> marklink = new HashMap<>();

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private boolean isEditable;
    private Button save;
    private Button add;
    private Button cansel;
    private LatLng latLng;
    private LatLng markerLatLong;
    private boolean hasMarker;
    private boolean isZoomed = false;

    private GoogleApiClient client;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null && !isZoomed) {
                if (marks.isEmpty()) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                }
                isZoomed = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        save = (Button) findViewById(R.id.save_marker);
        cansel = (Button) findViewById(R.id.cansel_marker);
        add = (Button) findViewById(R.id.add);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        enableMyLocation();
        mMap.clear();
        for (String id : marks.keySet()) {
            MarkerOptions marker = new MarkerOptions().position(
                    marks.get(id).getLatLng())
                    .title(marks.get(id).getName());
            Category category = marks.get(id).getCategory();
            if (category != null) {
                marker.snippet(category.getName());
            }
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            Marker result = mMap.addMarker(marker);
            marklink.put(result.getId(), marks.get(id));
        }
        mMap.setOnMarkerDragListener(this);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), AddMarkerActivity.class);
                intent.putExtra("location", marklink.get(marker.getId()));
                intent.putExtra("view", true);
                startActivity(intent);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLngM) {
                if (isEditable) {
                    if (!hasMarker) {
                        markerLatLong = latLngM;
                        MarkerOptions marker = new MarkerOptions().position(
                                latLngM)
                                .title("").draggable(true);
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        mMap.addMarker(marker);
                        hasMarker = true;
                        save.setVisibility(View.VISIBLE);
                        cansel.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    public void onClickAdd(View v) {
        isEditable = true;
        add.setVisibility(View.INVISIBLE);
        cansel.setVisibility(View.VISIBLE);
    }

    public void onSave(View v) {
        Intent intent = new Intent(getApplicationContext(), AddMarkerActivity.class);
        intent.putExtra("latlng", markerLatLong);
        intent.putExtra("view", false);
        startActivity(intent);
        isEditable = false;
        save.setVisibility(View.INVISIBLE);
        cansel.setVisibility(View.INVISIBLE);
        add.setVisibility(View.VISIBLE);
    }

    public void onCansel(View v) {
        isEditable = false;
        save.setVisibility(View.INVISIBLE);
        cansel.setVisibility(View.INVISIBLE);
        add.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        markerLatLong = marker.getPosition();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void onSearchActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra("latlng", latLng);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchViewAndroidActionBar.clearFocus();
                new MyAsyncTask<Void, Void, Void>(new MyListener() {
                    @Override
                    public void onComplete() {
                        onMapReady(mMap);
                    }
                }) {
                    @Override
                    protected Void doInBackground(Void... params) {
                        final DataService ds = new DataService("107.170.25.215", 8080);
                        List<co.example.yuliya.maps.domain.Location> locations = ds.getLocations(latLng.latitude, latLng.longitude, "6km", null, query, 0, 100);
                        locations.addAll(ds.getLocations(latLng.latitude, latLng.longitude, "6km", query, null, 0, 100));
                        MapsActivity.marks.clear();
                        for (int i = 0; i < locations.size(); i++) {
                            co.example.yuliya.maps.domain.Location loc = locations.get(i);
                            MapsActivity.marks.put(loc.getId(), loc);
                        }
                        return null;
                    }
                }.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
