package com.andyliang.pawwzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.IOException;

import is.arontibo.library.ElasticDownloadView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class LoadingScreen extends RoboActivity {

    boolean didDownloadImage = false;
    String filePath;

    @InjectView(R.id.elastic_download_view) ElasticDownloadView mElasticDownloadView;

    void showToast(String text){
        Toast.makeText(LoadingScreen.this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean isOnline() {

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);

            } catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }

            return false;
        } else {
            return false;
        }
    }

    void getRedditAwwPic(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String subreddit = prefs.getString(this.getString(R.string.pref_subreddit_key), "aww");
        String sorting = prefs.getString(getString(R.string.pref_sorting_key), "hot");
        String sortingTime = prefs.getString(getString(R.string.pref_sorting_time_key), "day");
        Ion.with(LoadingScreen.this)
                .load("http://reddit.com/r/" + subreddit + "/" + sorting + "/.json?limit=10&t=" + sortingTime)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try{
                            RedditResponse response = (new Gson()).fromJson(result, RedditResponse.class);
                            String url = response.data.children[3].data.preview.images[0].source.url;
                            downloadPicFromUrl(url);
                        } catch (Exception exception){ moveOn(); }
                    }
                });
    }

    void downloadPicFromUrl(String url){
        Ion.with(LoadingScreen.this)
                .load(url)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(final long downloaded, final long total) {
                        LoadingScreen.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mElasticDownloadView.setProgress(downloaded / total);
                            }
                        });
                    }
                })
                .write(new File("/sdcard/aww.jpg"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if(e != null){
                            showToast(e.getMessage());
                            didDownloadImage = false;
                            LoadingScreen.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mElasticDownloadView.fail();
                                }
                            });
                        } else {
                            didDownloadImage = true;
                            filePath = result.getAbsolutePath();
                            LoadingScreen.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mElasticDownloadView.success();
                                }
                            });
                        }
                        moveOn();
                    }
                });
    }

    void moveOn(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("didDownloadImage", didDownloadImage);
        intent.putExtra("filePath", filePath);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        mElasticDownloadView.startIntro();

        if(isOnline()){
            getRedditAwwPic();
        } else {
            moveOn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
