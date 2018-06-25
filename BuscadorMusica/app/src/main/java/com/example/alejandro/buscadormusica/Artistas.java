package com.example.alejandro.buscadormusica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class Artistas extends AppCompatActivity implements Handler.Callback{

    LinearLayout contenedor;
    private String tokenAcceso;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistas);

        contenedor = findViewById(R.id.contenedor);
        tokenAcceso = getIntent().getStringExtra("tokenAcceso");

        sharedPreferences = getSharedPreferences("ArchivoSP",MODE_PRIVATE);
        ponerArtistas(sharedPreferences.getString("json",""));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.buscador, menu);
        MenuItem searchItem = menu.findItem(R.id.buscador);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    String palabra = URLEncoder.encode(query, "UTF-8");
                    DescargarJSON descargarJSON = new DescargarJSON(Artistas.this, tokenAcceso, new Handler(Artistas.this));
                    descargarJSON.execute("https://api.spotify.com/v1/search?query=" +  palabra + "&type=artist");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean handleMessage(Message msg) {
        String mensaje = msg.getData().getString("json");
        if (!Objects.equals(mensaje, "{}")) {
            ponerArtistas(mensaje);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("json", msg.getData().getString("json"));
            editor.apply();
        }
        return false;
    }

    private void ponerArtistas(String json) {
        try {
            JSONObject artistas = new JSONObject(json);

            int total = artistas.getJSONObject("artists").getJSONArray("items").length();

            contenedor.removeAllViews();
            for (int i = 0; i < total; i++) {
                String imagen = artistas.getJSONObject("artists").getJSONArray("items").getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                String nombre = artistas.getJSONObject("artists").getJSONArray("items").getJSONObject(i).getString("name");
                String genero = artistas.getJSONObject("artists").getJSONArray("items").getJSONObject(i).getJSONArray("genres").toString()
                        .replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", " ").replaceFirst(" ", "");
                String popularidad = String.valueOf(artistas.getJSONObject("artists").getJSONArray("items").getJSONObject(i).getInt("popularity"));
                final String id = artistas.getJSONObject("artists").getJSONArray("items").getJSONObject(i).getString("id");

                View view = getLayoutInflater().inflate(R.layout.elemento_artista, new LinearLayout(this));
                CardView cardView = view.findViewById(R.id.cardview);
                ImageView imagenArtista = view.findViewById(R.id.imagenArtista);
                TextView nombreArtista = view.findViewById(R.id.nombreArtista);
                TextView generoArtista = view.findViewById(R.id.generoArtista);
                TextView popularidadArtista = view.findViewById(R.id.popularidadArtista);

                Picasso.with(this).load(imagen).into(imagenArtista);

                nombreArtista.setText(nombre);

                if (genero.equals(""))
                    generoArtista.setText("-");
                else
                    generoArtista.setText(genero);

                popularidadArtista.setText(popularidad);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Albums.class);
                        intent.putExtra("tokenAcceso", tokenAcceso);
                        intent.putExtra("idArtista", id);
                        startActivity(intent);
                    }
                });


                contenedor.addView(view);
            }

        } catch (JSONException e) {
            //No hay json
        }
    }

}
