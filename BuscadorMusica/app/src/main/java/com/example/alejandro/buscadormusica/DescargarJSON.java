package com.example.alejandro.buscadormusica;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;


public class DescargarJSON extends AsyncTask<String, String, String> {

    private Context contexto;
    private String tokenAcceso;
    private Handler handler;

    DescargarJSON(Context contexto, String tokenAcceso, Handler handler) {
        this.contexto = contexto;
        this.tokenAcceso = tokenAcceso;
        this.handler = handler;
    }


    @Override
    protected String doInBackground(String... strings) {

        String jsonString = null;

        try {
            Log.v("Form",strings[0]);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet peticion = new HttpGet(strings[0]);
            peticion.setHeader("Authorization", "Bearer " + tokenAcceso);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            jsonString = httpclient.execute(peticion, responseHandler);



        } catch (IOException e) {
            //No hay datos
        }

        return jsonString != null? jsonString:"{}";

    }

    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("json",strings);
        msg.setData(bundle);
        handler.dispatchMessage(msg);

    }
}
