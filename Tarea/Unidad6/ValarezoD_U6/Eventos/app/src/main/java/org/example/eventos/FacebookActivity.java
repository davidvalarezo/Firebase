package org.example.eventos;

import androidx.appcompat.app.AppCompatActivity;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import static org.example.eventos.Comun.mostrarDialogo;
import static org.example.eventos.EventoDetalles.imageSN;

public class FacebookActivity extends AppCompatActivity {
    private TextView elTextoDeBienvenida;
    private Button botonHacerLogin;
    private Button botonLogOut;
    private ImageView imgInicio;
    //private TextView elTextoParaDatos;
    private Button botonObtenerDatos;
    // boton oficial de Facebook para login/logout
    LoginButton loginButtonOficial;
    // gestiona los callbacks al FacebookSdk desde el método onActivityResult() de una actividad
    //--------------------------------------------------------------
    // puntero a this para los callback
    // --------------------------------------------------------------
    private final Activity THIS = this;
    // --------------------------------------------------------------
    // elementos graficos usando Share Dialog
    private Button boton2;
    private Button boton3;
    //private TextView textoEntrada1;
    //private TextView textoSalida1;
    // gestiona los callbacks al FacebookSdk desde el método onActivityResult() de una actividad //
    private CallbackManager elCallbackManagerDeFacebook;
    // shareDialog //
    private ShareDialog elShareDialog;
    String evento;
    final int SOLICITUD_SELECCION_PUTFILE_FACEBOOK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        /**************************************************************************************/
        // botón oficial de "login en Facebook"
        // obtengo referencia
        loginButtonOficial = (LoginButton) findViewById(R.id.login_button_facebook);
        // declaro los permisos que debe pedir al ser pulsado
        // ver lista en: https://developers.facebook.com/docs/facebook-login/per-missions
        loginButtonOficial.setPermissions("email", "public_profile");

        this.elCallbackManagerDeFacebook = CallbackManager.Factory.create();

        // registro un callback para saber cómo ha ido el login
        LoginManager.getInstance().registerCallback(this.elCallbackManagerDeFacebook,
                new FacebookCallback<LoginResult>() {
                    @Override public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(THIS, "Login onSuccess()", Toast.LENGTH_LONG).show();
                        actualizarVentanita();
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
        imgInicio = findViewById(R.id.imageView);
        // obtengo referencias a mis otros widgets en el layout //
        elTextoDeBienvenida = (TextView) findViewById(R.id.textSaludo);
        botonHacerLogin = (Button) findViewById(R.id.boton_Login);
        botonLogOut = (Button) findViewById(R.id.boton_Logout);
        botonObtenerDatos = (Button) findViewById(R.id.boton_obtenerDatosUser);
        //this.elTextoParaDatos = (TextView) findViewById(R.id.textoParaDatos);

        this.actualizarVentanita();
        Log.d("cuandrav.onCreate", "final .onCreate() ");
        /**************************************************************************************/
        //usando Share Dialog
        this.elShareDialog = new ShareDialog(this);
        // inicializar FacebookSDK //
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        conseguirReferenciasAElementosGraficos();
        // crear objeto share dialog //
        this.elShareDialog = new ShareDialog(this);
        this.elShareDialog.registerCallback(this.elCallbackManagerDeFacebook,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(THIS, "Sharer onSuccess()", Toast.LENGTH_LONG).show();
                    }
                    @Override public void onCancel() { }
                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(THIS, "Sharer onError(): " + error.toString(),
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
        }
    }

    private void actualizarVentanita() {
        Log.d("cuandrav.actualizarVent", "empiezo");
        // obtengo el access token para ver si hay sesión
        AccessToken accessToken = this.obtenerAccessToken();
        if (accessToken == null) {
            Log.d("cuandrav.actualizarVent", "no hay sesion, deshabilito");
            // // sesion con facebook cerrada //
            this.botonHacerLogin.setEnabled(true);
            this.botonLogOut.setEnabled(false);
            this.botonObtenerDatos.setEnabled(false);
            this.elTextoDeBienvenida.setText("Haz login");
            return;
        }
        // sí hay sesión //
        Log.d("cuandrav.actualizarVent", "hay sesion habilito");
        this.botonHacerLogin.setEnabled(false);
        this.botonLogOut.setEnabled(true);
        this.botonObtenerDatos.setEnabled(true);

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

    private AccessToken obtenerAccessToken() {
        return AccessToken.getCurrentAccessToken();
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

    public void boton_obtenerDatosPulsado(View quien) {
        Log.d("cuandrav.btDatosPuls()", " empiezo");
        this.obtenerPublicProfileConRequest_async(
                // como es asíncrono he de dar un callback
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override public void onCompleted(JSONObject datosJSON, GraphResponse response) {
                        // muestro los datos //
/***/                   //elTextoParaDatos.setText(datosJSON.toString());
                        String user = "";
                        try {
                            user = "ID: "+datosJSON.getString("id") +"\n" +
                                    "Nombre: "+datosJSON.getString("name") +"\n" +
                                    "Correo: "+datosJSON.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mostrarDialogo(getApplicationContext(), user);
                    }
                });
    } // ()

    public void boton_Login_pulsado(View quien) {
        Log.d("cuandrav.btLoginPuls()", " empiezo");
        // // compruebo la red //
        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red? No puedo abrir sesión",
                    Toast.LENGTH_LONG).show();
        }
        // // login // // YA NO ESTA PERMITIDO hacer login para publicar (desde AGOSTO 2018):
        // Lo-ginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("pu-blish_actions"));

        try {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("email"));
        } catch (Exception ex) {
            Log.d("cuandrav.btLoginPuls()", " falla logInWith(): " + ex.getMessage());
        }
        // actualizar //
        this.actualizarVentanita();
    }

    public void boton_Logout_pulsado(View quien) {
        // compruebo la red //
        if (!this.hayRed()) {
            Toast.makeText(this, "¿No hay red? No puedo cerrar sesión",
                    Toast.LENGTH_LONG).show();
        }

        // logout //
        LoginManager.getInstance().logOut();
        // // actualizar //
        this.actualizarVentanita();
    }

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

    private void conseguirReferenciasAElementosGraficos () {
        boton2 = (Button) findViewById(R.id.boton_info);
        boton3 = (Button) findViewById(R.id.boton_imagenFacebook);
        //textoEntrada1 = (TextView) findViewById(R.id.textoEntrada1);
        //textoSalida1 = (TextView) findViewById(R.id.textoSalida1);
    } // ()
    // ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarMensaje () {
        // no he encontrado un método específico para
        // publicar sólo un mensaje
        return puedoUtilizarShareDialogParaPublicarLink();
    }//()
    // ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarLink () {
        return ShareDialog.canShow(ShareLinkContent.class);
    } // ()
    // ------------------------------------------------------------
    private boolean puedoUtilizarShareDialogParaPublicarFoto () {
        return ShareDialog.canShow(SharePhotoContent.class);
    } // ()
    // ------------------------------------------------------------

    public void boton2_pulsado(View quien) {
        //textoSalida1.setText("boton2_pulsado");
        // llamar al metodo para publicar //
        this.publicarMensajeConShareDialog();
        Toast.makeText(this, "Botón: Publicar Mensaje",
                Toast.LENGTH_LONG).show();
        // this.compartirLinkConShareDialog();
    }// ()
    // ------------------------------------------------------------
    private void publicarMensajeConShareDialog () {
        // https://developers.facebook.com/docs/android/share -> Using the Share Dialog
        if ( ! puedoUtilizarShareDialogParaPublicarMensaje() ) {
            Log.d("cuandrav.boton2_pul()", " ¡¡¡ No puedo utilizar share dialog !!!");
            // Share dialog en definitiva es llamar al aplicacin Facebook de Android.
            // Si no se tuviera (devuelve false) aún se puede llamar a un navegador que saca una
            // pgina de FB para publicar: Feed Dialog (buscar: pu-blishFeedDialog
            // en https://developers.facebook.com/docs/android/share)
            return;
        }
        Log.d("cuandrav.boton2_puls()", " sí que puedo utilizar share dia-log");
        // llamar a share dialog
        // aunque utilizamos ShareLinkContent, al no poner link
        // publica un mensaje //
        ShareLinkContent content = new ShareLinkContent.Builder().build();
        this.elShareDialog.show(content);
    }// ()
    // ------------------------------------------------------------
    public void boton3_pulsado(View quien) {
        Log.d("cuandrav.boton3_pulsado", " llamado ");
        //textoSalida1.setText("boton3_pulsado");
        // llamar al metodo para publicar foto //
        this.publicarFotoConShareDialog(quien);
        //seleccionarFotografiaDispositivo(quien );*
        Toast.makeText(this, "Botón: Publicar Foto",
                Toast.LENGTH_LONG).show();
    }// ()
    // ------------------------------------------------------------
    private void publicarFotoConShareDialog (View view) {
        // https://developers.facebook.com/docs/android/share -> Using the Share Dialog
        if ( ! puedoUtilizarShareDialogParaPublicarFoto() ) { return; }
        Log.d("cuandrav.boton2_puls()", " sí que puedo utilizar share dia-log");

        // cojo una imagen directamente de los recursos para publicarla //
        //Bitmap image = BitmapFactory.decodeResource( getResources(), R.drawable.messenger_bubble_large_blue);
        /*Bitmap image = null;
        File imgFile = new File(rutaImagen);
        if(imgFile.exists()){
            image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }*/
        imgInicio.setDrawingCacheEnabled(true);
        imgInicio.buildDrawingCache();
        Bitmap image = imgInicio.getDrawingCache();

        // monto la petición
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("caption de prueba que no llega")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        this.elShareDialog.show(content);
    }// ()

    public void seleccionarFotografiaDispositivo(View v) {
        Intent seleccionFotografiaIntent = new Intent(Intent.ACTION_PICK);
        seleccionFotografiaIntent.setType("image/*");
        startActivityForResult(seleccionFotografiaIntent, SOLICITUD_SELECCION_PUTFILE_FACEBOOK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        this.elCallbackManagerDeFacebook.onActivityResult(requestCode, resultCode, data);
        Uri ficheroSeleccionado;
        Cursor cursor; String rutaImagen;
        if (resultCode == Activity.RESULT_OK) {
            //Facebook this.publicarFotoConShareDialog();
            if(requestCode == SOLICITUD_SELECCION_PUTFILE_FACEBOOK) {
                ficheroSeleccionado = data.getData();
                String[] proyeccionFile = {MediaStore.Images.Media.DATA};
                cursor = getContentResolver().query(ficheroSeleccionado,
                        proyeccionFile, null, null, null);
                cursor.moveToFirst();
                rutaImagen = cursor.getString( cursor.getColumnIndex(proyeccionFile[0]));
                cursor.close();
                imgInicio.setImageURI(Uri.parse(rutaImagen));
                //this.publicarFotoConShareDialog();
            }
        }
    }

}
