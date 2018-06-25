package com.example.alejandro.buscadormusica;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Tracks extends AppCompatActivity implements Handler.Callback{

    RecyclerView recyclerView;
    ArrayList<String[]> listaTracks;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracks);

        String idAlbum = getIntent().getStringExtra("idAlbum");
        String nombreAlbum = getIntent().getStringExtra("nombreAlbum");
        String imagenAlbum = getIntent().getStringExtra("imagenAlbum");
        String tokenAcceso = getIntent().getStringExtra("tokenAcceso");
        listaTracks = new ArrayList<>();
        mediaPlayer = new MediaPlayer();

        ImageView imagenToolbar = findViewById(R.id.imagenToolbar);
        Picasso.with(getBaseContext()).load(imagenAlbum).into(imagenToolbar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(nombreAlbum);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        DescargarJSON descargarJSON = new DescargarJSON(Tracks.this, tokenAcceso, new Handler(Tracks.this));
        descargarJSON.execute("https://api.spotify.com/v1/albums/" + idAlbum + "/tracks");
    }

    @Override
    public boolean handleMessage(Message msg) {
        ponerTracks(msg.getData().getString("json"));
        return false;
    }

    private void ponerTracks(String json) {
        try {
            JSONObject albums = new JSONObject(json);
            int total = albums.getJSONArray("items").length();

            for (int i = 0; i < total; i++) {
                String id = albums.getJSONArray("items").getJSONObject(i).getString("id");
                String numero = albums.getJSONArray("items").getJSONObject(i).getString("track_number");
                String nombre = albums.getJSONArray("items").getJSONObject(i).getString("name");
                String preview_url = albums.getJSONArray("items").getJSONObject(i).getString("preview_url");

                listaTracks.add(new String[]{id, numero, nombre, preview_url});
            }

            recyclerView.setAdapter(new RecycleViewAdaptador());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class RecycleViewAdaptador extends RecyclerView.Adapter<RecycleViewAdaptador.ViewHolder> {



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_track, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.numeroTrack.setText(listaTracks.get(position)[1]);
            holder.nombreTrack.setText(listaTracks.get(position)[2]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!listaTracks.get(holder.getAdapterPosition())[3].equals("null")){
                        try {

                            mediaPlayer.reset();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(listaTracks.get(holder.getAdapterPosition())[3]);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            mediaPlayer.prepareAsync();

                            Snackbar snackbar = Snackbar.make(v, "Escuchando: \t"+ listaTracks.get(holder.getAdapterPosition())[2], Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                            snackbar.show();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getBaseContext(),"No hay preview",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return listaTracks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView numeroTrack, nombreTrack;
            ImageView icono;

            ViewHolder(View v) {
                super(v);
                numeroTrack = v.findViewById(R.id.numeroTrack);
                nombreTrack = v.findViewById(R.id.nombreTrack);
                icono = v.findViewById(R.id.icono);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.reset();
        finish();
    }

}
