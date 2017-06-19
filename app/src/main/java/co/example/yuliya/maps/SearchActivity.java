package co.example.yuliya.maps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import co.example.yuliya.maps.domain.Category;
import co.example.yuliya.maps.domain.Location;
import co.example.yuliya.maps.service.DataService;

/**
 * Created by Yuliya on 4/29/2017.
 */

public class SearchActivity extends AppCompatActivity {

    private EditText name;
    private EditText dist;
    private LatLng latLng;
    private Spinner spinner;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location);
        spinner = (Spinner) findViewById(R.id.spinner_cat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SearchActivity.this,
                R.layout.spinner_item, new ArrayList<String>());
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
        setSupportActionBar(toolbar);

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                DataService ds = new DataService("107.170.25.215", 8080);
                return ds.getCategories();
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                dataAdapter.add("Категория");
                dataAdapter.addAll(strings);
                dataAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public void onSearch(View v) {
        new Thread(new Runnable() {
            public void run() {
                name = (EditText) findViewById(R.id.name);
                dist = (EditText) findViewById(R.id.dist);
                spinner = (Spinner) findViewById(R.id.spinner_cat);
                final DataService ds = new DataService("107.170.25.215", 8080);
                Intent intent = getIntent();
                latLng = intent.getParcelableExtra("latlng");
                String dis = dist.getText().toString();
                if (!dis.isEmpty()) {
                    dis += "km";
                }
                List<Location> locations = ds.getLocations(latLng.latitude, latLng.longitude, dis, spinner.getSelectedItem().toString(), name.getText().toString(), 0, 100);

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
