/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.example.yuliya.maps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class SaveStateActivity extends AppCompatActivity {

    /**
     * Default marker position when the activity is first created.
     */
    private static final LatLng DEFAULT_MARKER_POSITION = new LatLng(48.858179, 2.294576);

    /**
     * List of hues to use for the marker
     */
    private static final float[] MARKER_HUES = new float[]{
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE,
    };

    // Bundle keys.
    private static final String OTHER_OPTIONS = "options";

    private static final String MARKER_POSITION = "markerPosition";

    private static final String MARKER_INFO = "markerInfo";

    private LatLng latLng;

    /**
     * Extra info about a marker.
     */
    static class MarkerInfo implements Parcelable {

        public static final Parcelable.Creator<MarkerInfo> CREATOR =
                new Parcelable.Creator<MarkerInfo>() {
                    @Override
                    public MarkerInfo createFromParcel(Parcel in) {
                        return new MarkerInfo(in);
                    }

                    @Override
                    public MarkerInfo[] newArray(int size) {
                        return new MarkerInfo[size];
                    }
                };

        float mHue;

        public MarkerInfo(float color) {
            mHue = color;
        }

        private MarkerInfo(Parcel in) {
            mHue = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(mHue);
        }
    }

    public static class SaveStateMapFragment extends SupportMapFragment
            implements OnMarkerClickListener, OnMarkerDragListener, OnMapReadyCallback {

        private LatLng mMarkerPosition;

        private MarkerInfo mMarkerInfo;

        private boolean mMoveCameraToMarker;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                // Activity created for the first time.
                mMarkerPosition = DEFAULT_MARKER_POSITION;
                mMarkerInfo = new MarkerInfo(BitmapDescriptorFactory.HUE_RED);
                mMoveCameraToMarker = true;
            } else {
                mMarkerPosition = savedInstanceState.getParcelable(MARKER_POSITION);
                Bundle bundle = savedInstanceState.getBundle(OTHER_OPTIONS);
                mMarkerInfo = bundle.getParcelable(MARKER_INFO);

                mMoveCameraToMarker = false;
            }

            getMapAsync(this);
        }


        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putParcelable(MARKER_POSITION, mMarkerPosition);

            Bundle bundle = new Bundle();
            bundle.putParcelable(MARKER_INFO, mMarkerInfo);
            outState.putBundle(OTHER_OPTIONS, bundle);
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            float newHue = MARKER_HUES[new Random().nextInt(MARKER_HUES.length)];
            mMarkerInfo.mHue = newHue;
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(newHue));
            return true;
        }

        @Override
        public void onMapReady(GoogleMap map) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mMarkerPosition)
                    .icon(BitmapDescriptorFactory.defaultMarker(mMarkerInfo.mHue))
                    .draggable(true);
            map.addMarker(markerOptions);
            map.setOnMarkerDragListener(this);
            map.setOnMarkerClickListener(this);

            if (mMoveCameraToMarker) {
                map.animateCamera(CameraUpdateFactory.newLatLng(mMarkerPosition));
            }
        }

        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            mMarkerPosition = marker.getPosition();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_state);
    }

    public void onSave(View v) {
        Intent intent = new Intent(this, AddMarkerActivity.class);
        startActivity(intent);
    }

    public void onCansel(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }
}
