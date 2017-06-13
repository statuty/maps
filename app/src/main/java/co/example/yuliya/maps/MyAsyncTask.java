package co.example.yuliya.maps;

import android.os.AsyncTask;

/**
 * Created by Yuliya on 5/2/2017.
 */

public abstract class MyAsyncTask<A, P, R> extends AsyncTask<A, P, R> {
    private MyListener listener;

    public MyAsyncTask(MyListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(R r) {
        super.onPostExecute(r);
        listener.onComplete();
    }
}
