package org.example.eventos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import retrofit2.Call;

import static org.example.eventos.EventoDetalles.imageSN;

public class TwitterActivity extends AppCompatActivity {
    private TwitterLoginButton botonLoginTwitter;
    private Button botonEnviarATwitter;
    private Button botonEnviarImagenATwitter;
    private Button botonElegirImagen;
    private EditText messageATwitter;
    private TextView elTextoDeBienvenida;
    private ImageView imgInicio;
    private final Activity THIS = this;
    final int SOLICITUD_SELECCION_PUTFILE_TWITTER = 101;
    String evento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        Twitter.initialize(this);
        imgInicio = findViewById(R.id.imageViewT);
        elTextoDeBienvenida = (TextView) findViewById(R.id.textSaludoTwitter);
        messageATwitter = (EditText)  findViewById(R.id.txt_messageTwitter);
        botonElegirImagen = (Button) findViewById(R.id.buttonI);
        botonEnviarImagenATwitter = (Button) findViewById(R.id.boton_ImagenEnviarTwitter);
        botonEnviarATwitter = (Button) findViewById(R.id.boton_EnviarTwitter);
        botonLoginTwitter = (TwitterLoginButton) findViewById(R.id.boton_twitter_login);
        botonLoginTwitter.setEnabled(true);
        this.botonLoginTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(THIS, "Autenticado en twitter: " +
                        result.data.getUserName(), Toast.LENGTH_LONG).show();
                elTextoDeBienvenida.setText("Bienvenido a Eventos 2020: " + result.data.getUserName());

            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(THIS, "Fallo en autentificación: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        Bundle extras = getIntent().getExtras();
        try {
            evento = extras.getString("evento");
        }catch (Exception ex){ }
        if (evento!=null) {
            if(imageSN != null) imgInicio.setImageBitmap(imageSN);
            elTextoDeBienvenida.setText("Bienvenidos a Eventos "+evento);
            messageATwitter.setText("#Evento"+evento);
        }

    }

    // ------------------------------------------------------------
    private TwitterSession obtenerSesionDeTwitter() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }
    // ------------------------------------------------------------
    private StatusesService obtenerStatusesService() {
        return TwitterCore.getInstance().getApiClient(obtenerSesionDeTwitter()).getStatusesService();
    }
    // ------------------------------------------------------------
    // Twitter publicar Imagen
    public void enviarImagen_async(View view) {
        // cargamos foto
        imgInicio.setDrawingCacheEnabled(true);
        imgInicio.buildDrawingCache();
        Bitmap bitmapImagen = imgInicio.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] photo = stream.toByteArray();
        bitmapImagen.recycle();

        // obtenemos referencia al media service
        MediaService ms = TwitterCore.getInstance()
                .getApiClient(obtenerSesionDeTwitter())
                .getMediaService();
        if (ms == null) {
            Toast.makeText(THIS, "No has iniciado sesión en Twitter", Toast.LENGTH_LONG);
            return;
        }
        // ponemos la foto en el request body de la petición
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                MediaType.parse("image/jpg"), photo);
        // con el media service: enviamos la foto a Twitter
        Call<Media> call1 = ms.upload( requestBody, null, null);
        call1.enqueue (new Callback<Media>() {
            @Override
            public void success(Result<Media> mediaResult) {
                // éxito de call1
                Toast.makeText(THIS, "imagen publicada: " +
                        mediaResult.response.toString(), Toast.LENGTH_LONG);
                // como he tenido éxito, la foto está en twitter, pero no en el timeline
                // he de escribir un tweet referenciando la foto
                // obtengo referencia al status service

                StatusesService ss = TwitterCore.getInstance().
                        getApiClient(obtenerSesionDeTwitter()).getStatusesService();
                // publico un tweet
                String tweet = messageATwitter.getText().toString();
                if(tweet.isEmpty()) {
                    tweet = "Imagen enviada desde Eventos App";
                }
                Call<Tweet> call2 = ss.update(tweet,
                        null, false, null,
                        null, null, true, false,
                        ""+mediaResult.data.mediaId // string con los identicadores
                        // (hasta 4, separado por coma) de las imágenes
                        // que quiero que aparezcan en este tweet.
                        // El mediaId referencia a la foto que acabo de subir previamente
                );
                // envio la petición de publicación del tweet
                call2.enqueue( new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Log.d("cuandrav", "enviarImagen_async(): tweet con foto publicado ! " );
                        Toast.makeText(THIS, "Tweet con foto publicado: "+ result.response.message().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void failure(TwitterException e) {
                        Log.d("cuandrav", "enviarImagen_async(): tweet con foto NO publicado ! " );
                        Toast.makeText(THIS, "No se pudo publicar el tweet con foto: "+ e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }// enqueue sucess()

            @Override
            public void failure(TwitterException e) {
                // failure de call1
                Toast.makeText(THIS, "No se pudo publicar el tweet con foto: "+ e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }// enviarImagen_async()

    public void enviarATwitter(View view){
        StatusesService miStatusesService = TwitterCore.getInstance()
                .getApiClient(obtenerSesionDeTwitter()).getStatusesService();
        String textoQueEnviar = messageATwitter.getText().toString();
        if(textoQueEnviar.isEmpty()) {
            Toast.makeText(THIS, "Mensaje inválido", Toast.LENGTH_LONG).show();
            return;
        }
        Call<Tweet> call = miStatusesService.update(textoQueEnviar,
                null, null, null,null,
                null, null, null, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(THIS, "Tweet publicado: " +
                        result.response.message(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(THIS, "No se pudo publicar el tweet: "+ e.getMessage(),
                        Toast.LENGTH_LONG).show();
            } });
    }

    public void seleccionarFotografiaDispositivo(View v) {
        Intent seleccionFotografiaIntent = new Intent(Intent.ACTION_PICK);
        seleccionFotografiaIntent.setType("image/*");
        startActivityForResult(seleccionFotografiaIntent, SOLICITUD_SELECCION_PUTFILE_TWITTER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        this.botonLoginTwitter.onActivityResult(requestCode, resultCode, data);
        Uri ficheroSeleccionado;
        Cursor cursor; String rutaImagen;
        if (resultCode == Activity.RESULT_OK) {
            //Twitter
            if(requestCode == SOLICITUD_SELECCION_PUTFILE_TWITTER) {
                ficheroSeleccionado = data.getData();
                String[] proyeccionFile = {MediaStore.Images.Media.DATA};
                cursor = getContentResolver().query(ficheroSeleccionado,
                        proyeccionFile, null, null, null);
                cursor.moveToFirst();
                rutaImagen = cursor.getString( cursor.getColumnIndex(proyeccionFile[0]));
                cursor.close();
                imgInicio.setImageURI(Uri.parse(rutaImagen));
                //enviarImagen_async(rutaImagen);
            }
        }
    }
}
