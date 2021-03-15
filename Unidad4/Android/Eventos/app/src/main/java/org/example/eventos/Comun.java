package org.example.eventos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Comun {
    static final String URL_SERVIDOR = "http://curso-firebase.000webhostapp.com/";
    //http://curso-firebase.000webhostapp.com/listar.html
    static String ID_PROYECTO="eventos-67df9";
    String idRegistro ="";
    public static FirebaseStorage storage;
    public static StorageReference storageRef;

    public static void mostrarDialogo(final Context context, final String mensaje) {
        Intent intent = new Intent(context, Dialogo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mensaje", mensaje);
        context.startActivity(intent);
    }

    public static class registrarDispositivoEnServidorWebTask extends AsyncTask<Void, Void, String> {
        String response="error";
        Context contexto;
        String idRegistroTarea ="";
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                Uri.Builder constructorParametros;
                constructorParametros = new Uri.Builder()
                        .appendQueryParameter("iddevice", idRegistroTarea)
                        .appendQueryParameter("idapp", ID_PROYECTO);
                String parametros = constructorParametros.build().getEncodedQuery();
                String url = URL_SERVIDOR + "registrar.php";
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setRequestProperty("Accept-Language", "UTF-8");
                conexion.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new
                        OutputStreamWriter(conexion.getOutputStream());
                outputStreamWriter.write(parametros.toString());
                outputStreamWriter.flush();
                int respuesta = conexion.getResponseCode();
                if (respuesta==200){
                    response="ok";
                }
                else {
                    response="error";
                }
            } catch (IOException e) {
                response= "error";
            }
            return response;
        }

        public void onPostExecute(String res) { }
    }

    public static void guardarIdRegistro(Context context, String idRegistro){
        registrarDispositivoEnServidorWebTask tarea =
                new registrarDispositivoEnServidorWebTask();
        tarea.contexto=context;
        tarea.idRegistroTarea=idRegistro;
        tarea.execute();
    }


    public static void eliminarIdRegistro(final Context context){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            desregistrarDispositivoEnServidorWebTask tarea =
                                    new desregistrarDispositivoEnServidorWebTask();
                            tarea.contexto = context;
                            tarea.idRegistroTarea = task.getResult().getToken();
                            tarea.execute();
                        }
                    }
                });
    }

    public static class desregistrarDispositivoEnServidorWebTask
            extends AsyncTask<Void, Void, String> {
        String response="error";
        Context contexto;
        String idRegistroTarea;
        public void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Uri.Builder constructorParametros = new Uri.Builder()
                        .appendQueryParameter("iddevice", idRegistroTarea)
                        .appendQueryParameter("idapp", ID_PROYECTO);
                String parametros = constructorParametros.build().getEncodedQuery();
                String url = URL_SERVIDOR + "desregistrar.php";
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setRequestProperty("Accept-Language", "UTF-8");
                conexion.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conexion.getOutputStream());
                outputStreamWriter.write(parametros.toString());
                outputStreamWriter.flush();
                int respuesta = conexion.getResponseCode();
                if (respuesta == 200) {
                    response = "ok";
                }else {
                    response = "error";
                }
            } catch (IOException e) {
                response = "error";
            }
            return response;
        }
        public void onPostExecute(String res) { }

        public static StorageReference getStorageReference() {return storageRef;}

    }
}
