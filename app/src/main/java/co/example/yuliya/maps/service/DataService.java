package co.example.yuliya.maps.service;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import co.example.yuliya.maps.domain.Location;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by astatut on 4/18/17.
 */

public class DataService {
    private final String serverAddress;
    private final int port;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    public DataService(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Location> getLocations(double lan, double lon, String distance, String category, String name, Integer page, Integer size) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(serverAddress)
                .port(port)
                .addPathSegment("locations")
                .addQueryParameter("longitude", String.valueOf(lon))
                .addQueryParameter("latitude", String.valueOf(lan));
        urlBuilder = (distance != null && !distance.isEmpty()) ? urlBuilder.addQueryParameter("distance", distance) : urlBuilder;
        urlBuilder = (category != null && !category.isEmpty()) ? urlBuilder.addQueryParameter("category", category) : urlBuilder;
        urlBuilder = (name != null && !name.isEmpty()) ? urlBuilder.addQueryParameter("name", name) : urlBuilder;
        urlBuilder = page != null ? urlBuilder.addQueryParameter("page", page.toString()) : urlBuilder;
        urlBuilder = size != null ? urlBuilder.addQueryParameter("size", size.toString()) : urlBuilder;
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new RuntimeException("Couldn't receive locations. Code = " + response.code());
            }
            return mapper.readValue(response.body().string(), new TypeReference<List<Location>>() {
            });
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
    }

    public List<String> getCategories() {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(serverAddress)
                .port(port)
                .addPathSegment("locations")
                .addPathSegment("categories");
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't receive categories. Code = " + response.code());
        }
        if (response.code() != 200) {
            throw new RuntimeException("Couldn't receive categories. Code = " + response.code());
        }
        try {
            return mapper.readValue(response.body().string(), new TypeReference<List<String>>() {});
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getCategories: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive categories");
        }
    }

    public Location getLocation(String id) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(serverAddress)
                .port(port)
                .addPathSegment("locations")
                .addPathSegment(id);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        Response response = null;
        try {
            response = new RequestTask().execute(request).get();
            if (response.code() != 200) {
                throw new RuntimeException("Couldn't receive locations");
            }
            return mapper.readValue(response.body().string(), Location.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Couldn't receive locations");
        } catch (IOException e) {
            Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
            throw new RuntimeException("Couldn't receive locations");
        }
    }

    public String saveLocation(Location location) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(serverAddress)
                .port(port)
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
            response = new RequestTask().execute(request).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Couldn't receive locations");
        }

        if (response.code() != 201) {
            throw new RuntimeException("Couldn't receive locations");
        }
        String[] header = response.header("Location").split("/");
        return header[header.length - 1];
    }

    class RequestTask extends AsyncTask<Request, Void, Response> {

        @Override
        protected Response doInBackground(Request... params) {
            try {
                return client.newCall(params[0]).execute();
            } catch (IOException e) {
                Log.d(DataService.class.getName(), "getLocations: " + e.getMessage(), e);
                throw new RuntimeException("Couldn't receive locations");
            }
        }
    }

}
