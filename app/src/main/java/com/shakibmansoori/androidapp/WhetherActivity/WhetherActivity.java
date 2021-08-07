package com.shakibmansoori.androidapp.WhetherActivity;

import static com.shakibmansoori.androidapp.SignUpActivity.SignUpActivity.LOG_TAG;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.shakibmansoori.androidapp.R;
import com.shakibmansoori.androidapp.SignUpActivity.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class WhetherActivity extends AppCompatActivity {

    static String WHETHER_API_URL = "https://api.weatherapi.com/v1/current.json?key=ff870738a12242ebafb105933210508&q=India";

    String temp_c, temp_f, latitude, longitude, currentWhe, imgUrl, bedroomText, meetingroomText;

    TextView mainTempTv, currentDescTv, temp_fTv, latitudeTv, longitudeTv, bedroomTv, meetingRoomTv;

    EditText cityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whether);


        mainTempTv = findViewById(R.id.main_temTv);
        currentDescTv = findViewById(R.id.currentDesTv);
        temp_fTv = findViewById(R.id.tem_f);
        latitudeTv = findViewById(R.id.latitudeTv);
        longitudeTv = findViewById(R.id.longTv);
        bedroomTv = findViewById(R.id.bedroomTv);
        meetingRoomTv = findViewById(R.id.meetingRoomTv);


            executeAsync();
            setText();

        Button enterCityBtn = findViewById(R.id.enterCity);
        Button updateCity = findViewById(R.id.updateCity);


        updateCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (temp_c != null) {

                    executeAsync();
                    setText();

                    Snackbar.make(v, "Please Wait We Are Updating Data", Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Connect Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
        enterCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (temp_c != null) {
                    customLayout();

                    executeAsync();
                    setText();

                } else {
                    Toast.makeText(getApplicationContext(), "Connect Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void customLayout() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final View addContactLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(addContactLayout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                cityEditText = addContactLayout.findViewById(R.id.cus_Et_title);

                String url = cityEditText.getText().toString();
                if (url != null) {
                    WHETHER_API_URL = "https://api.weatherapi.com/v1/current.json?key=ff870738a12242ebafb105933210508&q=" + url;

                    executeAsync();
                    setText();
                    Toast.makeText(getApplicationContext(), "Please Wait We Are Updating Data", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Enter City Name !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog
                = builder.create();
        dialog.show();
    }

    private void setText() {
        mainTempTv.setText(temp_c);
        currentDescTv.setText(currentWhe);
        temp_fTv.setText(temp_f);
        longitudeTv.setText(longitude);
        latitudeTv.setText(latitude);
        bedroomTv.setText(bedroomText);
        meetingRoomTv.setText(meetingroomText);
    }

    private void sendData(WhetherModel earthquake) {

        String cel = "\u2103";
        double tmp = earthquake.temp_c;
        double tmpf = earthquake.temp_f;

        temp_c = String.valueOf((int) tmp) + cel;
        temp_f = String.valueOf((int) tmpf) + "\u2109";

        int bed = ((int) tmp) - 2;
        int meet = ((int) tmp) - 3;

        bedroomText = String.valueOf(bed + cel);
        meetingroomText = String.valueOf(meet + cel);

        latitude = String.valueOf(earthquake.latitude);
        longitude = String.valueOf(earthquake.longitude);
        currentWhe = String.valueOf(earthquake.currentWhe);
        imgUrl = String.valueOf(earthquake.imgUrl);

        setText();

    }

    private void executeAsync() {
        // Kick off an {@link AsyncTask} to perform the network request
        TsunamiAsyncTask task = new TsunamiAsyncTask();
        task.execute();

    }


    private class TsunamiAsyncTask extends AsyncTask<URL, Void, WhetherModel> {

        @Override
        protected WhetherModel doInBackground(URL... urls) {

            URL url = createUrl(WHETHER_API_URL);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            WhetherModel data = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return data;
        }


        @Override
        protected void onPostExecute(WhetherModel data) {
            if (data == null) {
                return;
            }

            sendData(data);

        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (jsonResponse == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET"); // request to get the info
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect(); // connect to the server

                if (urlConnection.getResponseCode() == 200) {// check the data is success rcv
                    // used to format the response.
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }


        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private WhetherModel extractFeatureFromJson(String earthquakeJSON) {
            if (TextUtils.isEmpty(earthquakeJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

                JSONObject locationObj = baseJsonResponse.getJSONObject("location");
                JSONObject currentObj = baseJsonResponse.getJSONObject("current");

                JSONObject condition = currentObj.getJSONObject("condition");

                // If there are results in the features array
                if (locationObj.length() > 0 & currentObj.length() > 0) {

                    JSONObject location = locationObj;
                    JSONObject current = currentObj;

                    // Extract out the title, time, and tsunami values
                    Double latitude = location.getDouble("lat");
                    Double longitude = location.getDouble("lon");


                    Double temp_c = current.getDouble("temp_c");
                    Double temp_f = current.getDouble("temp_f");


                    String currentWhe = condition.getString("text");
                    String imgurl = condition.getString("icon");

                    // Create a new {@link Event} object
                    return new WhetherModel(temp_c, temp_f, latitude, longitude, imgurl, currentWhe);
                }
            } catch (JSONException e) {
                Log.e("kkk", "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }
    }


}