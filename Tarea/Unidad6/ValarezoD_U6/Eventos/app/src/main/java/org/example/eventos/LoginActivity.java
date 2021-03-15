package org.example.eventos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.MediaType;
import retrofit2.Call;

import static org.example.eventos.Comun.acercaDe;
import static org.example.eventos.Comun.colorFondo;
import static org.example.eventos.Comun.mFirebaseRemoteConfig;
import static org.example.eventos.Comun.mostrarDialogo;
import static org.example.eventos.Comun.pais;
import static org.example.eventos.Comun.performMonitoring;

public class LoginActivity extends Activity {

    // ------------------------------------------------------------
    // elementos gráficos
    // ------------------------------------------------------------
    private TextView elTextoDeBienvenida;
    private ImageView imgInicio;
    private Button botonStratEventos;
    // boton oficial de Facebook para login/logout
    LoginButton loginButtonOficial;
    // gestiona los callbacks al FacebookSdk desde el método onActivityResult() de una actividad
    private CallbackManager elCallbackManagerDeFacebook;
    //--------------------------------------------------------------
    // puntero a this para los callback
    // --------------------------------------------------------------
    private final Activity THIS = this;
    // --------------------------------------------------------------
    private TwitterLoginButton botonLoginTwitter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tenemos una plantilla llamada activity_login.xmlogin.xml donde mostraremos la información que queramos (logotipo, etc.)
        setContentView(R.layout.activity_login);
        // --------------------------------------------------------------
        // Unidad5 Configuración remota
        configuracionRemota();
        // --------------------------------------------------------------
        TextView msBienvenida = (TextView) findViewById(R.id.txtWellcome);
        try{
            //if(pais == null) pais = false;
            if(pais){
                msBienvenida.setText("Esta aplicación muestra información sobre eventos");
            }else{
                msBienvenida.setText("Esta aplicación muestra eventos de España");
            }
        }catch (Exception ex){
            System.out.println("pais null: "+ex.getMessage());
        }

        /**************************************************************************************/
        imgInicio = findViewById(R.id.imgInicio);
        botonStratEventos = (Button) findViewById(R.id.boton_StartEventos);
        // botón oficial de "login en Facebook"
        // obtengo referencia
        loginButtonOficial = (LoginButton) findViewById(R.id.login_button);
        // declaro los permisos que debe pedir al ser pulsado
        // ver lista en: https://developers.facebook.com/docs/facebook-login/per-missions
        loginButtonOficial.setPermissions("email", "public_profile");
        // crear callback manager de Facebook //
        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();
        // registro un callback para saber cómo ha ido el login
        LoginManager.getInstance().registerCallback(this.elCallbackManagerDeFacebook,
                new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(THIS, "Login onSuccess()", Toast.LENGTH_LONG).show();
               actualizarVentanita();
                startMain();
            }
            @Override public void onCancel() {
                Toast.makeText(THIS, "Login onCancel()", Toast.LENGTH_LONG).show();
                actualizarVentanita();
            }
            @Override public void onError(FacebookException exception) {
                // App code
                Toast.makeText(THIS, "Login onError(): " + exception.getMessage(),
                        Toast.LENGTH_LONG).show();
                actualizarVentanita();
            }
        });

        // obtengo referencias a mis otros widgets en el layout //
        elTextoDeBienvenida = (TextView) findViewById(R.id.elTextoDeBienvenida);
       this.actualizarVentanita();
        Log.d("cuandrav.onCreate", "final .onCreate() ");


        /**************************************************************************************/
        Twitter.initialize(this);
        botonLoginTwitter = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        botonLoginTwitter.setEnabled(true);
        this.botonLoginTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(THIS, "Autenticado en twitter: " +
                        result.data.getUserName(), Toast.LENGTH_LONG).show();
                elTextoDeBienvenida.setText("Bienvenido a Eventos 2020: " + result.data.getUserName());
                startMain();

            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(THIS, "Fallo en autentificación: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void startMain(){
        // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    };

    private void actualizarVentanita() {
        Log.d("cuandrav.actualizarVent", "empiezo");
        // obtengo el access token para ver si hay sesión
        AccessToken accessToken = this.obtenerAccessToken();
        if (accessToken == null) {
            Log.d("cuandrav.actualizarVent", "no hay sesion, deshabilito");
            // // sesion con facebook cerrada //
            this.elTextoDeBienvenida.setText("Haz login");
            return;
        }

        // averiguo los datos básicos del usuario acreditado //
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
/***/           //this.elTextoParaDatos.setText(profile.getName());
        }

        // otra forma de averiguar los datos básicos:
        // hago una petición con "graph api" para obtener datos del usuario acreditado //
        this.obtenerPublicProfileConRequest_async(
                // como es asíncrono he de dar un callback
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override public void onCompleted(JSONObject datosJSON, GraphResponse response) {
                        // // muestro los datos //
                        String nombre= "nombre desconocido";
                        try {
                            nombre = datosJSON.getString("name");
                        } catch (JSONException ex) {
                            Log.d("cuandrav.actualizarVent",
                                    "callback de obte-nerPublicProfileConRequest_async: excepcion: "
                                            + ex.getMessage());
                        } catch (NullPointerException ex) {
                            Log.d("cuandrav.actualizarVent",
                                    "callback de obte-nerPublicProfileConRequest_async: excepcion: "
                                            + ex.getMessage());
                        }
                    elTextoDeBienvenida.setText("Bienvenido a Eventos 2020: " + nombre);
                }
            });
    }// ()

    private void obtenerPublicProfileConRequest_async (GraphRequest.GraphJSONObjectCallback callback) {
        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red?", Toast.LENGTH_LONG).show();
        }
        // obtengo access token y compruebo que hay sesión //
        AccessToken accessToken = obtenerAccessToken();
        if (accessToken == null) {
            Toast.makeText(THIS, "no hay sesión con Facebook", Toast.LENGTH_LONG).show();
            return;
        }
        // monto la petición: /me //
        GraphRequest request = GraphRequest.newMeRequest(accessToken, callback);
        Bundle params = new Bundle ();
        params.putString("fields", "id, name, email");
        request.setParameters(params);

        // la ejecuto (asíncronamente) //
        request.executeAsync();
    }

    private boolean hayRed() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        // http://stackoverflow.com/questions/15091591/post-on-facebook-wall-without-showing-dialog-on-android
        // comprobar que estamos conetactos a internet, antes de hacer el login con
        // facebook. Si no: da problemas.
    } // ()

    private AccessToken obtenerAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode, data);
        this.botonLoginTwitter.onActivityResult(requestCode, resultCode, data);
    }

    // ------------------------------------------------------------
    private TwitterSession obtenerSesionDeTwitter() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    public void startEventos(View view){
        startMain();
    }

    public void configuracionRemota(){
        //Inicializa el objeto Remote Config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(3000)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        //Obtiene los valores predeterminados de la aplicación desde el fichero xml
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            getColorFondo();
                            getAcercaDe();
                            getPais();
                        } else {
                            Toast.makeText(LoginActivity.this, "Remote config error.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void getColorFondo() {
        colorFondo = mFirebaseRemoteConfig.getString("color_fondo");
    }
    private void getAcercaDe() {
        acercaDe = mFirebaseRemoteConfig.getBoolean("acerca_de");
    }
    private void getPais() {
        pais = mFirebaseRemoteConfig.getBoolean("pais");
    }
    private void getPerformanceMonitoring() {
        performMonitoring = mFirebaseRemoteConfig.getBoolean("PerformanceMonitoring");
    }
}
