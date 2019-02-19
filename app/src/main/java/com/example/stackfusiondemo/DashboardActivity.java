package com.example.stackfusiondemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class DashboardActivity extends AppCompatActivity {

    private Button fbLogin;
    private CallbackManager mCallbackManager;
    public static final String TAG = "StackfusionDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Log.d(TAG, "USER DETAILS");
        fbLogin = (Button) findViewById(R.id.fb_login_btn);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fbLogin.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(DashboardActivity.this, Arrays.asList("email", "public_profile", "user_about_me", "user_birthday", "user_gender", "user_location"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        //handleFacebookAccessToken(loginResult.getAccessToken());

                        Toast.makeText(DashboardActivity.this, "login success", Toast.LENGTH_SHORT).show();

                        //getFbInfo();


                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        fbLogin.setEnabled(true);
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        fbLogin.setEnabled(true);
                        // ...
                    }
                });

            }
        });

    }


    //     [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // calling method to fetch user details
        getFbInfo();

    }
    // [END onactivityresult]

    // method to fetch user details from facebook graph api
    private void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Log.d(TAG, "fb json object: " + object);
                            Log.d(TAG, "fb graph response: " + response);

                            // Fetching the data from the JSON response obtained from facebook graph api
                            String id = object.getString("id");
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String gender = object.getString("gender");
                            String birthday = object.getString("birthday");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
                            JSONObject jsonObject = object.getJSONObject("location");
                            String fbLocation = jsonObject.getString("name");

                            String email = "Not Available";
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            //Toast.makeText(DashboardActivity.this, "Response"+first_name+last_name+gender+fbLocation+email+image_url,Toast.LENGTH_LONG).show();

                            // Passing the user details to the next activity using intent
                            Intent i = new Intent(DashboardActivity.this, UserDetailActivity.class);
                            i.putExtra("first name",first_name);
                            i.putExtra("last name", last_name);
                            i.putExtra("gender", gender);
                            i.putExtra("birthday", birthday);
                            i.putExtra("image url", image_url);
                            i.putExtra("fb location", fbLocation);
                            i.putExtra("email", email);

                            finish();
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,location"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();


    }

}
