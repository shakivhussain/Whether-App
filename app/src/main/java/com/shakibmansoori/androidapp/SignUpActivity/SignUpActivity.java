package com.shakibmansoori.androidapp.SignUpActivity;

import static java.util.Objects.requireNonNull;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shakibmansoori.androidapp.R;
import com.shakibmansoori.androidapp.WhetherActivity.WhetherActivity;

import org.json.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String LOG_TAG = "shash";

    String name, district, state;
    TextView mNameTv, mDistTv, mStateTv;

    TextInputLayout etName, etDob, etMoNumber, etAddrs1, etAddrs2, etPinCode, etGender;
    private String URL_POSTAL_PIN = "http://www.postalpincode.in/api/pincode/110002";
    boolean isGenderSelect = false;
    String selectedGender, pintCode;

    TextInputEditText dobEditText;

    AutoCompleteTextView autoCompleteTextView;
    String[] listItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etMoNumber = findViewById(R.id.etNumber);
        etDob = findViewById(R.id.etDob);
        etAddrs1 = findViewById(R.id.etAddrss1);
        etAddrs2 = findViewById(R.id.etAddrss2);
        etPinCode = findViewById(R.id.etPinCode);
        etGender = findViewById(R.id.etGender);
        autoCompleteTextView = findViewById(R.id.autoCompleteTv);
        etPinCode = findViewById(R.id.etPinCode);


        mNameTv = findViewById(R.id.pinName);
        mDistTv = findViewById(R.id.pinDist);
        mStateTv = findViewById(R.id.pinState);

        Button checkPin = findViewById(R.id.checkPinCode);


        checkPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name != null & checkPinCode()) {

                    setText();
                    customLayout();
                } else {
                    Toast.makeText(getApplicationContext(), "Something Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


        executeAync();
        setText();

        dobEditText = (TextInputEditText) etDob.getEditText();
        pintCode = etPinCode.getEditText().getText().toString();

        etDob.getEditText().setFocusable(false);
        listItem = getResources().getStringArray(R.array.array_gender);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, listItem);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(this);

        etDob.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Calender", Toast.LENGTH_SHORT).show();
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });

        changesNumber();
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String format = new SimpleDateFormat("dd MMM yyyy").format(c.getTime());
            dobEditText.setText(format);
//            dobEditText.setText(Integer.toString(calculateAge(c.getTimeInMillis())));
        }
    };


    private void customLayout() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final View addContactLayout = getLayoutInflater().inflate(R.layout.sign_up_popup_layout, null);
        builder.setView(addContactLayout);

        mNameTv = addContactLayout.findViewById(R.id.pinName);
        mDistTv = addContactLayout.findViewById(R.id.pinDist);
        mStateTv = addContactLayout.findViewById(R.id.pinState);

        mNameTv.setText(name);
        mDistTv.setText(district);
        mStateTv.setText(state);

        // add a button
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        });


        // create and show
        // the alert dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();


    }

    int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        Toast.makeText(getApplicationContext(), "Your Age = " + age, Toast.LENGTH_SHORT).show();
        return age;
    }

    private boolean validationName() {

        String f_name = requireNonNull(etName.getEditText()).getText().toString().trim();

        if (f_name.isEmpty()) {
            etName.setError("Name is required can't be empty.");
            return false;
        } else if (f_name.length() < 6) {
            etName.setError("Name is short ,.");
            return false;
        } else {
            etName.setError(null);
            etName.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validationPhoneNumber() {

        String phoneNumbr = requireNonNull(etMoNumber.getEditText()).getText().toString().trim();

        if (phoneNumbr.isEmpty()) {
            etMoNumber.setError("Name is required can't be empty.");
            return false;
        } else if (phoneNumbr.length() < 10) {
            etMoNumber.setError("10 Digits required");
            return false;
        } else {
            etMoNumber.setError(null);
            etMoNumber.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validDob() {

        String dob = requireNonNull(etDob.getEditText()).getText().toString().trim();

        if (dob.isEmpty()) {
            etDob.setError("DOB is required");
            return false;
        } else {
            etDob.setError(null);
            etDob.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validationAddrs() {

        String etAddrs = requireNonNull(etAddrs1.getEditText()).getText().toString().trim();

        if (etAddrs.isEmpty()) {
            etAddrs1.setError("Address is required can't be empty.");
            return false;
        } else if (etAddrs.length() <= 3) {
            etAddrs1.setError("Address is short.");
            return false;
        } else {
            etAddrs1.setError(null);
            etAddrs1.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validationPinCode() {

        String _etPinCode = requireNonNull(etPinCode.getEditText()).getText().toString().trim();

        if (_etPinCode.isEmpty()) {
            etPinCode.setError("Enter Pin Code.");
            return false;
        } else if (_etPinCode.length() < 6) {
            etPinCode.setError("PinCode is short.");
            return false;
        } else {
            etPinCode.setError(null);
            etPinCode.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validationGender() {
        if (!isGenderSelect) {
            etGender.setError("Select gender.");
            return false;
        } else {
            etGender.setError(null);
            etGender.setErrorEnabled(false);
            return true;
        }
    }


    public boolean validate() {

        if (!validationName() | !validationPhoneNumber() | !validDob() | !validationGender() | !validationAddrs() | !validationPinCode()) {
            return false;
        } else {
            return true;
        }
    }

    public void regstrBtn(View view) {
        if (!validate()) {
            showError();
            Toast.makeText(this, "Sign-Up Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sign-Up SuccessFully", Toast.LENGTH_SHORT).show();
            onSignup();
        }
    }

    private void onSignup() {

        Intent intent = new Intent(getApplicationContext(), WhetherActivity.class);
        startActivity(intent);
        finish();
    }

    private void changesNumber() {

        etMoNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validationPhoneNumber();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validationName();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDob.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validDob();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public boolean checkPinCode() {

        if (validationPinCode()) {
            pintCode = etPinCode.getEditText().getText().toString();

            URL_POSTAL_PIN = "http://www.postalpincode.in/api/pincode/" + pintCode;

            executeAync();
            setText();
            return true;

        } else {
            return false;
        }

    }

    private void executeAync() {

        PostalAsyncTask task = new PostalAsyncTask();
        task.execute();
    }


    public void showError() {

        if (!validationName()) {
            etGender.setErrorEnabled(false);
            etGender.setError(null);

            etMoNumber.setErrorEnabled(false);
            etMoNumber.setError(null);

            etDob.setErrorEnabled(false);
            etDob.setError(null);

            etAddrs1.setErrorEnabled(false);
            etAddrs1.setError(null);

            etPinCode.setErrorEnabled(false);
            etPinCode.setError(null);


        } else if (!validationGender()) {

            if (!validationName()) {

            } else {
                etName.setErrorEnabled(false);
                etName.setError(null);
            }

//            etGender.setErrorEnabled(false);
//            etGender.setError(null);

            etMoNumber.setErrorEnabled(false);
            etMoNumber.setError(null);


            etDob.setErrorEnabled(false);
            etDob.setError(null);

            etAddrs1.setErrorEnabled(false);
            etAddrs1.setError(null);

            etPinCode.setErrorEnabled(false);
            etPinCode.setError(null);

        } else if (!validationPhoneNumber()) {

            if (!validationName()) {

            } else {
                etName.setErrorEnabled(false);
                etName.setError(null);
            }

            if (!validationGender()) {
            } else {
                etGender.setErrorEnabled(false);
                etGender.setError(null);
            }
            etDob.setErrorEnabled(false);
            etDob.setError(null);

            etAddrs1.setErrorEnabled(false);
            etAddrs1.setError(null);

            etPinCode.setErrorEnabled(false);
            etPinCode.setError(null);


        } else if (!validDob()) {

            if (!validationName()) {

            } else {
                etName.setErrorEnabled(false);
                etName.setError(null);
            }

            if (!validationGender()) {
            } else {
                etGender.setErrorEnabled(false);
                etGender.setError(null);
            }

            if (!validationPhoneNumber()) {
            } else {
                etMoNumber.setErrorEnabled(false);
                etMoNumber.setError(null);
            }


            etAddrs1.setErrorEnabled(false);
            etAddrs1.setError(null);

            etPinCode.setErrorEnabled(false);
            etPinCode.setError(null);

        } else if (!validationAddrs()) {
            if (!validationName()) {

            } else {
                etName.setErrorEnabled(false);
                etName.setError(null);
            }

            if (!validationGender()) {
            } else {
                etGender.setErrorEnabled(false);
                etGender.setError(null);
            }

            if (!validationPhoneNumber()) {
            } else {
                etMoNumber.setErrorEnabled(false);
                etMoNumber.setError(null);
            }

            etPinCode.setErrorEnabled(false);
            etPinCode.setError(null);

        }


    }


    private void sendData(Model earthquake) {

        name = String.valueOf(earthquake.name);
        district = String.valueOf(earthquake.district);
        state = String.valueOf(earthquake.state);

        setText();

    }

    private void setText() {

        mNameTv.setText(name);
        mDistTv.setText(district);
        mStateTv.setText(state);

    }

    public void nextActivity(View view) {

        startActivity(new Intent(getApplicationContext(), WhetherActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView genderTv = findViewById(R.id.genderTv);

        if (isGenderSelect == false) {
            isGenderSelect = true;
        }
        selectedGender = listItem[position];
        autoCompleteTextView.setText(null);
        genderTv.setText(selectedGender);
        autoCompleteTextView.showDropDown();
        Toast.makeText(SignUpActivity.this, selectedGender + " Selected !", Toast.LENGTH_SHORT).show();

    }


    private class PostalAsyncTask extends AsyncTask<URL, Void, Model> {

        @Override
        protected Model doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(URL_POSTAL_PIN);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            Model data = extractFeatureFromJson(jsonResponse);

            return data;
        }


        @Override
        protected void onPostExecute(Model data) {
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

        private Model extractFeatureFromJson(String earthquakeJSON) {
            if (TextUtils.isEmpty(earthquakeJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

                JSONArray featureArray = baseJsonResponse.getJSONArray("PostOffice");

                if (featureArray.length() > 0) {

                    JSONObject properties = featureArray.getJSONObject(0);

                    String name = properties.getString("Name");
                    String district = properties.getString("District");
                    String state = properties.getString("State");

                    return new Model(name, district, state);
                }
            } catch (JSONException e) {
                Log.e("kkk", "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }
    }

}