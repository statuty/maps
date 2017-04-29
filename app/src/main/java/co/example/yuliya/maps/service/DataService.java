package co.example.yuliya.maps.service;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import co.example.yuliya.maps.domain.Location;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataService {
    private final String serverAddress;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    public DataService(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public List<Location> getLocations(double lan, double lon, String distance, Integer page, Integer size) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .host(serverAddress)
                .addPathSegment("locations")
                .addQueryParameter("longitude", String.valueOf(lon))
                .addQueryParameter("latitude", String.valueOf(lan));
        urlBuilder = distance != null ? urlBuilder.addQueryParameter("distance", distance) : urlBuilder;
        urlBuilder = page != null ? urlBuilder.addQueryParameter("page", page.toString()) : urlBuilder;
        urlBuilder = size != null ? urlBuilder.addQueryParameter("size", size.toString()) : urlBuilder;
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
        if (response.code() != 200) {
            throw new RuntimeException("Couldn't receive locations");
        }
        try {
            return mapper.readValue(response.body().string(), List.class);
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
    }

    public Location getLocation(String id) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .host(serverAddress)
                .addPathSegment("locations")
                .addPathSegment(id);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
        if (response.code() != 200) {
            throw new RuntimeException("Couldn't receive locations");
        }
        try {
            return mapper.readValue(response.body().string(), Location.class);
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
    }

    public void saveLocation(Location location) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .host(serverAddress)
                .addPathSegment("locations");
        RequestBody body;
        try {
            body = RequestBody.create(JSON, mapper.writeValueAsString(location));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write location");
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
        if (response.code() != 201) {
            throw new RuntimeException("Couldn't receive locations");
        }
    }
}
