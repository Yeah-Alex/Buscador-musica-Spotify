package com.example.alejandro.buscadormusica;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Albums extends AppCompatActivity implements Handler.Callback{

    GridView gridView;
    String tokenAcceso;
    ArrayList<String[]> listaAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);

        String idArtista = getIntent().getStringExtra("idArtista");

        gridView = findViewById(R.id.gridview);
        tokenAcceso = getIntent().getStringExtra("tokenAcceso");
        listaAlbums = new ArrayList<>();

        DescargarJSON descargarJSON = new DescargarJSON(Albums.this, tokenAcceso, new Handler(Albums.this));
        descargarJSON.execute("https://api.spotify.com/v1/artists/"+idArtista+"/albums");
    }

    public void track(View view){
        startActivity(new Intent(this,Tracks.class));
    }

    @Override
    public boolean handleMessage(Message msg) {
        ponerAlbums(msg.getData().getString("json"));
        return false;
    }

    public void ponerAlbums(String json){
        try {
            JSONObject albums = new JSONObject(json);
            int total = albums.getJSONArray("items").length();

            for(int i = 0;i<total;i++){
                String imagen = albums.getJSONArray("items").getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                String nombre = albums.getJSONArray("items").getJSONObject(i).getString("name");
                String id = albums.getJSONArray("items").getJSONObject(i).getString("id");
                listaAlbums.add(new String[]{imagen, nombre, id});
            }

            gridView.setAdapter(new GridAdaptador());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class GridAdaptador extends BaseAdapter {

        @Override
        public int getCount() {
            return listaAlbums.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String imagen = listaAlbums.get(position)[0];
            final String nombre = listaAlbums.get(position)[1];
            final String id = listaAlbums.get(position)[2];

            View view = getLayoutInflater().inflate(R.layout.elemento_album,parent,false);
            ImageView imagenAlbum = view.findViewById(R.id.imagenAlbum);
            TextView nombreAlbum = view.findViewById(R.id.nombreAlbum);

            Picasso.with(getBaseContext()).load(imagen).into(imagenAlbum);
            nombreAlbum.setText(nombre);

            imagenAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(),Tracks.class);
                    intent.putExtra("tokenAcceso",tokenAcceso);
                    intent.putExtra("idAlbum",id);
                    intent.putExtra("nombreAlbum",nombre);
                    intent.putExtra("imagenAlbum",imagen);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
