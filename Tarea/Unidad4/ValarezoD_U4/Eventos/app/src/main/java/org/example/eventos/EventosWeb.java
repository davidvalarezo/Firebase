package org.example.eventos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EventosWeb extends AppCompatActivity {
    private  String evento;
    final InterfazComunicacion miInterfazJava = new InterfazComunicacion(this);
    WebView navegador;
    //private ProgressBar barraProgreso;
    ProgressDialog dialogo;
    //Button btnDetener, btnAnterior, btnSiguiente;
    //private EditText txtdireccion;
    @Override
    @SuppressLint("JavascriptInterface")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventos_web);

        Bundle extras = getIntent().getExtras();
        evento = extras.getString("evento");

        //btnDetener = (Button) findViewById(R.id.btnDetener);
        //btnAnterior = (Button) findViewById(R.id.btnAnterior);
        //btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        //txtdireccion = findViewById(R.id.txtDireccion);
        navegador = (WebView) findViewById(R.id.webkit);
        navegador.getSettings().setJavaScriptEnabled(true);
        navegador.getSettings().setBuiltInZoomControls(false);
        //navegador.loadUrl("https://eventos-67df9.firebaseapp.com/index.html");
        navegador.loadUrl("file:///android_asset/index.html");
        navegador.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*String url_filtro = "http://www.androidcurso.com/";
                if (!url.toString().equals(url_filtro)){
                    view.loadUrl(url_filtro);
                }*/
                return false;
            }
            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialogo = new ProgressDialog(EventosWeb.this);
                dialogo.setMessage("Cargando...");
                dialogo.setCancelable(true);
                dialogo.show();
                //btnDetener.setEnabled(true);
                /*if (comprobarConectividad()) {
                    btnDetener.setEnabled(true);
                } else {
                    btnDetener.setEnabled(false);
                }*/
            }
            @Override public void onPageFinished(WebView view, String url) {
                dialogo.dismiss();
                navegador.loadUrl("javascript:muestraEvento(\""+evento+"\");");
                //btnDetener.setEnabled(false);
                /*if (view.canGoBack()) {
                    btnAnterior.setEnabled(true);
                } else {
                    btnAnterior.setEnabled(false);
                }
                if (view.canGoForward()) {
                    btnSiguiente.setEnabled(true);
                } else {
                    btnSiguiente.setEnabled(false);
                }*/
            }
            @Override public void onReceivedError(WebView view, int errorCode,
                                                  String description, String failingUrl) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventosWeb.this);
                builder.setMessage(description).setPositiveButton("Aceptar", null)
                        .setTitle("onReceivedError"); builder.show();
            }
        });
        //barraProgreso = (ProgressBar) findViewById(R.id.barraProgreso);

        navegador.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int progreso) {
                //barraProgreso.setProgress(0);
                //barraProgreso.setVisibility(View.VISIBLE);
                //EventosWeb.this.setProgress(progreso * 1000);
                //barraProgreso.incrementProgressBy(progreso);
                /*if (progreso == 100) {
                    barraProgreso.setVisibility(View.GONE);
                }*/
            }
            // Control de las Alerta de JavaScript
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(EventosWeb.this).setTitle("Mensaje")
                        .setMessage(message).setPositiveButton
                        (android.R.string.ok,new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        }).setCancelable(false).create().show();
                return true;
            }
        });
        ActivityCompat.requestPermissions(EventosWeb.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        navegador.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(final String url, String userAgent, String contentDisposition,
                                        String mimetype,long contentLength) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventosWeb.this);
                builder.setTitle("Descarga");
                builder.setMessage("¿Deseas guardar el archivo?");
                builder.setCancelable(false).setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                URL urlDescarga;
                                try {
                                    urlDescarga = new URL(url);
                                    new DescargarFichero().execute(urlDescarga);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
        ActivityCompat.requestPermissions(EventosWeb.this,
                new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, 2);

        navegador.addJavascriptInterface(miInterfazJava, "jsInterfazNativa");
    }

    public void detenerCarga(View v) {
        navegador.stopLoading();
    }
    public void irPaginaAnterior(View v) {
        if (comprobarConectividad()) {
            navegador.goBack();
        }
    }
    public void irPaginaSiguiente(View v) {
        if (comprobarConectividad()) {
            navegador.goForward();
        }
    }

    @Override
    public void onBackPressed() {
        if (navegador.canGoBack()) {
            navegador.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /*public void irPagina(View v) {
        //String url = txtdireccion.getText().toString();
        if(url.isEmpty() || url == null){
            //Toast.makeText(getBaseContext(), "Inserta una URL válida", Toast.LENGTH_LONG).show();
            mensaje("Escribe una URL válida");
            return;
        }else if (!url.matches("http.+[.].+")){
            //Toast.makeText(getBaseContext(), "Inserta una URL válida", Toast.LENGTH_LONG).show();
            mensaje("Inserta una URL válida");
            return;
        }
        navegador.loadUrl(url);
    }*/

    public void mensaje(String message) {
        new AlertDialog.Builder(EventosWeb.this).setTitle("Mensaje")
                .setMessage(message).setPositiveButton
                (android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setCancelable(true).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(EventosWeb.this,
                            "Permiso denegado para escribir en el almacenamiento.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(EventosWeb.this,
                            "Permiso denegado para conocer el estado de la red.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private boolean comprobarConectividad() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            Toast.makeText(EventosWeb.this,
                    "Oops! No tienes conexión a internet",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private class DescargarFichero extends AsyncTask<URL, Integer, Long> {
        private String mensaje;
        @Override
        protected Long doInBackground(URL... url) {
            String urlDescarga = url[0].toString(); mensaje = "";
            InputStream inputStream =null;
            try {
                URL direccion = new URL(urlDescarga);
                HttpURLConnection conexion =
                        (HttpURLConnection)direccion.openConnection();
                if (conexion.getResponseCode()== HttpURLConnection.HTTP_OK) {
                    inputStream = conexion.getInputStream();
                    String fileName = android.os.Environment
                            .getExternalStorageDirectory().getAbsolutePath() +
                            "/descargas";
                    File directorio = new File(fileName);
                    directorio.mkdirs();
                    File file = new File(directorio, urlDescarga.substring( urlDescarga.lastIndexOf("/"),
                            (urlDescarga.indexOf("?")==-1?
                                    urlDescarga.length():urlDescarga.indexOf("?"))));
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0; int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    mensaje = "Guardado en: " + file.getAbsolutePath();
                }else {
                    throw new Exception(conexion.getResponseMessage());
                }
            }catch (Exception ex) {
                mensaje = ex.getClass().getSimpleName() + " " + ex.getMessage();
            }finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {

                    }
                }
            }
            return (long) 0;
        }

        protected void onPostExecute(Long result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    EventosWeb.this);
            builder.setTitle("Descarga"); builder.setMessage(mensaje);
            builder.setCancelable(true); builder.create().show();
        }
    }

    public class InterfazComunicacion {
        Context mContext;
        InterfazComunicacion(Context c) { mContext = c; }
        @JavascriptInterface
        public void volver(){
            finish();
        }
    }

}
