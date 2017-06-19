package co.example.yuliya.maps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import co.example.yuliya.maps.domain.Category;
import co.example.yuliya.maps.domain.GeoPoint;
import co.example.yuliya.maps.domain.Location;
import co.example.yuliya.maps.service.DataService;

import static android.R.attr.value;

public class AddMarkerActivity extends AppCompatActivity {

    private EditText name;
    private EditText description;
    private LatLng latLng;
    private Location location;
    private boolean view;
    private Button add;
    private Button edit;
    private boolean isEdit;
    private TextView title;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        title = (TextView) findViewById(R.id.title);
        latLng = intent.getParcelableExtra("latlng");
        location = (Location) getIntent().getSerializableExtra("location");
        view = intent.getBooleanExtra("view", false);
        isEdit = intent.getBooleanExtra("edit", false);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.desc);
        add = (Button) findViewById(R.id.add);
        edit = (Button) findViewById(R.id.edit);
        spinner = (Spinner) findViewById(R.id.spinner_cat);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddMarkerActivity.this,
                R.layout.spinner_item, new ArrayList<String>());
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
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
                if (isEdit) {
                    title.setText(R.string.loc_dot_edit);
                } else {
                    title.setText(R.string.loc_dot);
                }
                if (view) {
                    title.setText(R.string.loc_dot_view);
                    name.setEnabled(false);
                    description.setEnabled(false);
                    spinner.setEnabled(false);


                    name.setText(location.getName());
                    description.setText(location.getDescription());
                    Category catg = location.getCategory();
                    if (catg != null) {
                        for(int i=0; i < spinner.getAdapter().getCount(); i++) {
                            if(catg.getName().trim().equals(spinner.getAdapter().getItem(i).toString())){
                                spinner.setSelection(i);
                                break;
                            }
                        }
                    }

                    add.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.VISIBLE);
                } else {
                    if (location != null) {
                        name.setText(location.getName());
                        description.setText(location.getDescription());
                        Category catg = location.getCategory();
                        if (catg != null) {
                            for(int i=0; i < spinner.getAdapter().getCount(); i++) {
                                if(catg.getName().trim().equals(spinner.getAdapter().getItem(i).toString())){
                                    spinner.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                    add.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.INVISIBLE);
                }
            }
        }.execute();


    }

    public void onAdd(View v) {
        new Thread(new Runnable() {
            final DataService ds = new DataService("107.170.25.215", 8080);

            public void run() {
                Location loc = null;
                if (latLng != null) {
                    loc = new Location(null, name.getText().toString(), description.getText().toString(), new GeoPoint(latLng.latitude, latLng.longitude), new Category(spinner.getSelectedItem().toString()));
                } else if (location != null) {
                    loc = location;
                    loc.setName(name.getText().toString());
                    loc.setDescription(description.getText().toString());
                    loc.setCategory(new Category(spinner.getSelectedItem().toString()));
                }
                String id = ds.saveLocation(loc);
                MapsActivity.marks.put(id, loc);
                Intent intent = new Intent(AddMarkerActivity.this, MapsActivity.class);
                startActivity(intent); //I always put 0 for someIntValue
            }
        }).start();
    }

    public void onCancel(View v) {
        Intent intent = new Intent(AddMarkerActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void onEdit(View v) {
        name.setEnabled(true);
        description.setEnabled(true);
        spinner.setEnabled(true);
        add.setVisibility(View.VISIBLE);
        add.setText(R.string.save);
        edit.setVisibility(View.INVISIBLE);
        view = false;
    }
}
