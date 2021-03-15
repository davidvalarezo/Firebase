package org.example.eventos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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
    static String ID_PROYECTO="eventos-67df9";
    String idRegistro ="";
    static final String SERVIDOR_URL = "https://dvalarez.000webhostapp.com/eventos/";
    static final String API_KEY = "AAAA5PkKxrw:APA91bFCFU2qd-Cde78CFLGPyGx4lptsPruGlzbvOY-wToJqckHAV0b9nf5inQ0rNlmznJ6uRZeLN-mZBbZJOW_5rLuDVMUt7__hq0KuPNy0StZFBSOjZPTG1nF2rR5a-2_Mo306Bois";
    public static FirebaseStorage storage;
    public static StorageReference storageRef;
    public static FirebaseAnalytics mFirebaseAnalytics;

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

        registrarDispositivoEnServidorPropioWebTask tareaSP =
                new registrarDispositivoEnServidorPropioWebTask();
        tareaSP.contexto=context;
        tareaSP.idRegistroTarea=idRegistro;
        tareaSP.execute();
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

                            desregistrarDispositivoEnServidorPropioWebTask tareaSP =
                                    new desregistrarDispositivoEnServidorPropioWebTask();
                            tareaSP.contexto = context;
                            tareaSP.idRegistroTarea = task.getResult().getToken();
                            tareaSP.execute();
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

    public static class registrarDispositivoEnServidorPropioWebTask extends AsyncTask<Void, Void, String> {
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
                String url = SERVIDOR_URL + "registrar.php";
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

    public static class desregistrarDispositivoEnServidorPropioWebTask
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
                String url = SERVIDOR_URL + "desregistrar.php";
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

    public static class enviarEventoDispositivos extends AsyncTask<Void, Void, String> {
        String response="error";
        Context contexto;
        String notificacion ="";
        String title ="";
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                Uri.Builder constructorParametros = new Uri.Builder()
                        .appendQueryParameter("apiKey", API_KEY)
                        .appendQueryParameter("idapp", ID_PROYECTO)
                        .appendQueryParameter("title", title)
                        .appendQueryParameter("mensaje", notificacion);
                String parametros = constructorParametros.build().getEncodedQuery();
                String url = URL_SERVIDOR + "notificar.php";
                //String url = SERVIDOR_URL + "notificar.php";

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

    public static void enviarNotificacion(Context context, String mensaje, String titulo){
        enviarEventoDispositivos tarea =
                new enviarEventoDispositivos();
        tarea.contexto=context;
        tarea.notificacion=mensaje;
        tarea.title=titulo;
        tarea.execute();

    }
}
