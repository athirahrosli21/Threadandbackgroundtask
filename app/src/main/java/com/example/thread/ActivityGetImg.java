package com.example.thread;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityGetImg extends AppCompatActivity {

    private Executor executor;
    private Handler handler;
    private Bitmap bitmap = null;
    private ImageView imgVwSelfie;
    private Button btnAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_img);

        // Initialize UI components
        imgVwSelfie = findViewById(R.id.imgVWselfie);
        btnAsyncTask = findViewById(R.id.btnAsyncTask);

        // Initialize Executor and Handler
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Set Button Click Listener
        btnAsyncTask.setOnClickListener(v -> fetchImageFromInternet());
    }

    private void fetchImageFromInternet() {
        // Check for connectivity
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            executor.execute(() -> {
                try {
                    // Define the URL of the image
                    URL imageURL = new URL("https://ftmk.utem.edu.my/web/wp-content/uploads/2020/02/cropped-Logo-FTMK.png");
                    HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    // Fetch the image as InputStream
                    InputStream inputStream = connection.getInputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Update the UI on the main thread
                handler.post(() -> {
                    if (bitmap != null) {
                        imgVwSelfie.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(ActivityGetImg.this, "Failed to fetch the image!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            Toast.makeText(this, "No Network!! Please add data plan or connect to Wi-Fi network!", Toast.LENGTH_SHORT).show();
        }
    }
}