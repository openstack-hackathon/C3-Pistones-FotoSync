package com.fotosync.example;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Register extends ActionBarActivity implements View.OnClickListener {

    EditText etUser, etEmail;
    Button btnLogin;
    JSONObject jObject;
    String nombre="", email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String email=(mSharedPreference.getString("email", ""));
        if(email.equals("")){

        }else{
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
        }


        etUser = (EditText)findViewById(R.id.etUser);
        etEmail = (EditText)findViewById(R.id.etEmail);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

        etUser.setHintTextColor(Color.WHITE);
        etEmail.setHintTextColor(Color.WHITE);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if(etUser.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty()){

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                        builder1.setMessage("Por favor, llena todos los campos para Registrarte correctamente");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Entendido",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    } else
                    {
                        String nombre = etUser.getText().toString();
                        String email = etEmail.getText().toString();

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("nombre", nombre);
                        editor.putString("email", email);
                        editor.commit();

                        String url = "http://172.16.11.70/fotosync/upload_user.php?nombre="+nombre+"&email="+email;
                        //String url = "http://10.43.2.89/fotosync/upload_user.php?nombre="+nombre+"&email="+email;

                        try {
                            jObject = new JSONObject(getJSONUrl(url));
                            //Log.e("Resultado de JOBJECT ", ""+jObject);

                }catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);

                        break;
        }
    }}

    // Get JSON Code from URL
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                //Log.e("Log", "Failed to download file.."+" Codigo: "+statusCode);
                Log.e("Log", "Res: "+url);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();

    }
}
