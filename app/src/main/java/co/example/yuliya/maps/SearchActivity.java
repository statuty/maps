package co.example.yuliya.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import co.example.yuliya.maps.domain.Location;
import co.example.yuliya.maps.service.DataService;

/**
 * Created by Yuliya on 4/29/2017.
 */

public class SearchActivity extends AppCompatActivity {

    private EditText name;
    private EditText dist;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onSearch(View v) {
        new Thread(new Runnable() {
            public void run() {
                name = (EditText) findViewById(R.id.name);
                dist = (EditText) findViewById(R.id.dist);
                final DataService ds = new DataService("107.170.25.215", 8080);
                Intent intent = getIntent();
                latLng = intent.getParcelableExtra("latlng");
                String dis = dist.getText().toString();
                if (dis == null) {
                    dis += "km";
                }
                List<Location> locations = ds.getLocations(latLng.latitude, latLng.longitude, dis, null, null);

                MapsActivity.marks.clear();
                if (locations != null) {
                    for (int i = 0; i < locations.size(); i++) {
                        Location loc = locations.get(i);
                        MapsActivity.marks.put(loc.getId(), loc);
                    }
                }
                intent = new Intent(SearchActivity.this, MapsActivity.class);
                startActivity(intent); //I always put 0 for someIntValue
            }
        }).start();
    }

    public void onCansel(View v) {
        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}
