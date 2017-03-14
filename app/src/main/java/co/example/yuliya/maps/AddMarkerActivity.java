package co.example.yuliya.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class AddMarkerActivity extends AppCompatActivity {

    private EditText name;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        latLng = intent.getParcelableExtra("latlng");
        name = (EditText) findViewById(R.id.name);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onAdd(View v) {
        MapsActivity.marks.put(name.getText().toString(), latLng);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent); //I always put 0 for someIntValue
    }
}
