package com.example.alejandro.buscadormusica;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class Login extends AppCompatActivity {

    private final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void login(View view) {

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(getResources().getString(R.string.CLIENT_ID), AuthenticationResponse.Type.TOKEN, getResources().getString(R.string.REDIRECT_URI));
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                Intent intent = new Intent(this,Artistas.class);
                intent.putExtra("tokenAcceso",response.getAccessToken());
                startActivity(intent);

            }
        }
    }
}
