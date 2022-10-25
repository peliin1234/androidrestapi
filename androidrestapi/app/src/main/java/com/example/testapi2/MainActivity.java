package com.example.testapi2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private EditText textField_drug1;
    private EditText textField_drug2;
    private Button button_send_post;
    private Button button_send_get;
    private TextView textView_response;
    private String url="http://192.168.1.34:5000/rest";//****Put your  URL here******
    private String POST="POST";
    private String GET="GET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textField_drug1=findViewById(R.id.txtField_drug1);
        textField_drug2=findViewById(R.id.txtField_drug2);
        button_send_post=findViewById(R.id.button_send_post);
        button_send_get=findViewById(R.id.button_send_get);
        textView_response=findViewById(R.id.textView_response);
        sendRequest(GET,"/name",null,null);
        /*making a post request.*/
        button_send_post.setOnClickListener(view -> {

            //get the test in the text field.In this example you should type your name here
            String d1=textField_drug1.getText().toString();
            String d2=textField_drug2.getText().toString();
            if(d1.isEmpty()){
                textField_drug1.setError("This cannot be empty for post request");

            }
            else if(d2.isEmpty()){
                textField_drug2.setError("This cannot be empty for post request");
            }
            else {
                /*if name text is not empty,then call the function to make the post request*/
                sendRequest(POST, "", "drugs", d1+"+"+d2);
            }
        });

        /*making the get request*/
        button_send_get.setOnClickListener(view -> {
            /*in ourr server.py file we implemented a get method  named "get_fact()".
            We specified its URL invocation as '/getfact' there.
            Here we pass it to the sendRequest() function*/
            sendRequest(GET,"/",null,null);
        });

    }
    void sendRequest(String type,String method,String paramname,String param){

        /* if url is of our get request, it should not have parameters according to our implementation.
         * But our post request should have 'name' parameter. */
        String fullURL=url;
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        /* If it is a post request, then we have to pass the parameters inside the request body*/
        if(type.equals(POST)){
            RequestBody formBody = new FormBody.Builder()
                    .add(paramname, param)
                    .build();

            request=new Request.Builder()
                    .url(fullURL)
                    .post(formBody)
                    .build();
        }else{
            /*If it's our get request, it doesn't require parameters, hence just sending with the url*/
            request = new Request.Builder()
                    .url(fullURL)
                    .build();
        }
        /* this is how the callback get handled */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                // Read data on the worker thread
                final String responseData = response.body().string();

                // Run view-related code back on the main thread.
                // He re we display the response message in our text view
                MainActivity.this.runOnUiThread(() -> textView_response.setText(responseData));
                System.out.println("my result"+responseData);
            }
        });
    }
}