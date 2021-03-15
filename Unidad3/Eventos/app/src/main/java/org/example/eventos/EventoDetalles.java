package org.example.eventos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.remote.TokenResult;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.eventos.Comun.acercaDe;
import static org.example.eventos.Comun.desregistrarDispositivoEnServidorWebTask.getStorageReference;
import static org.example.eventos.Comun.mFirebaseAnalytics;
import static org.example.eventos.Comun.mostrarDialogo;
import static org.example.eventos.Comun.storage;
import static org.example.eventos.Comun.storageRef;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class EventoDetalles extends AppCompatActivity {
    final int SOLICITUD_SUBIR_PUTDATA = 0;
    final int SOLICITUD_SUBIR_PUTSTREAM = 1;
    final int SOLICITUD_SUBIR_PUTFILE = 2;
    final int SOLICITUD_SELECCION_STREAM = 100;
    final int SOLICITUD_SELECCION_PUTFILE = 101;
    TextView txtEvento, txtFecha, txtCiudad;
    ImageView imgImagen;
    String evento, descuento;
    CollectionReference registros;
    static UploadTask uploadTask=null;
    static FileDownloadTask downloadTask=null;
    StorageReference imagenRef;
    private ProgressDialog progresoSubida;
    Boolean subiendoDatos =false;
    Boolean bajandoDatos =false;
    final int SOLICITUD_FOTOGRAFIAS_DRIVE = 102;
    Trace mTrace;

    @Override
    @AddTrace(name = "onCreateTrace", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evento_detalles);
        txtEvento = (TextView) findViewById(R.id.txtEvento);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtCiudad = (TextView) findViewById(R.id.txtCiudad);
        imgImagen = (ImageView) findViewById(R.id.imgImagen);
        Bundle extras = getIntent().getExtras();
        evento = extras.getString("evento");
        if (evento==null) {
            android.net.Uri url = getIntent().getData();
            evento= url.getQueryParameter("evento");
            descuento = url.getQueryParameter("descuento");
            if (descuento != "" ) {
                mostrarDialogo(getApplicationContext(),
                        "Tienes un descuento del " + descuento + "%");
            }
        }
        registros = FirebaseFirestore.getInstance().collection("eventos");
        registros.document(evento).get().addOnCompleteListener(
            new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        txtEvento.setText(task.getResult()
                                .get("evento").toString());
                        txtCiudad.setText(task.getResult()
                                .get("ciudad").toString());
                        txtFecha.setText(task.getResult()
                                .get("fecha").toString());
                        new DownloadImageTask( (ImageView) imgImagen).execute(task.getResult()
                                .get("imagen").toString());
                    }
                }
            });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(evento);
        setSupportActionBar(toolbar);
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        mFirebaseAnalytics.setUserProperty("evento_detalle", evento);
        mTrace = FirebasePerformance.getInstance().newTrace("trace_EventoDetalles");
        mTrace.start();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) { this.bmImage = bmImage; }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mImagen = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mImagen = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImagen;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalles, menu);
        try{
            if (!acercaDe) { menu.removeItem(R.id.action_acercaDe); }
        }catch (Exception ex){ }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View vista = (View) findViewById(android.R.id.content);
        Bundle bundle = new Bundle();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_putData:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "subir_imagen");
                mFirebaseAnalytics.logEvent("menus", bundle);
                subirAFirebaseStorage(SOLICITUD_SUBIR_PUTDATA,null);
                break;
            case R.id.action_streamData:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "subir_stream");
                mFirebaseAnalytics.logEvent("menus", bundle);
                seleccionarFotografiaDispositivo(vista, SOLICITUD_SELECCION_STREAM);
                break;
            case R.id.action_putFile:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "subir_fichero");
                mFirebaseAnalytics.logEvent("menus", bundle);
                seleccionarFotografiaDispositivo(vista, SOLICITUD_SELECCION_PUTFILE);
                break;
            case R.id.action_getFile:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "descargar_fichero");
                mFirebaseAnalytics.logEvent("menus", bundle);
                descargarDeFirebaseStorage(evento);
                break;
            case R.id.action_deleteFile:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "borrar_fichero");
                mFirebaseAnalytics.logEvent("menus", bundle);
                confirmarBorrarImagen(this, evento);
                break;
            case R.id.action_fotografiasDrive:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "fotografias_drive");
                mFirebaseAnalytics.logEvent("menus", bundle);
                Intent intent = new Intent(getBaseContext(), FotografiasDrive.class);
                intent.putExtra("evento", evento);
                startActivity(intent);
                break;
            case R.id.action_acercaDe:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "acerca_de");
                mFirebaseAnalytics.logEvent("menus", bundle);
                Intent intentWeb = new Intent(getBaseContext(), EventosWeb.class);
                intentWeb.putExtra("evento", evento);
                startActivity(intentWeb);
                break;
            case R.id.action_descuento:
                if (id == R.id.action_descuento){
                    invitarDescuento();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void seleccionarFotografiaDispositivo(View v, Integer solicitud) {
        Intent seleccionFotografiaIntent = new Intent(Intent.ACTION_PICK);
        seleccionFotografiaIntent.setType("image/*");
        startActivityForResult(seleccionFotografiaIntent, solicitud);
    }

    @Override protected void onActivityResult(final int requestCode,
                                              final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri ficheroSeleccionado;
        Cursor cursor; String rutaImagen;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SOLICITUD_SELECCION_STREAM:
                    ficheroSeleccionado = data.getData();
                    String[] proyeccionStream = {MediaStore.Images.Media.DATA};
                    cursor = getContentResolver().query(ficheroSeleccionado,
                            proyeccionStream, null, null, null);
                    cursor.moveToFirst();
                    rutaImagen = cursor.getString( cursor.getColumnIndex(proyeccionStream[0]));
                    cursor.close();
                    subirAFirebaseStorage(SOLICITUD_SUBIR_PUTSTREAM, rutaImagen);
                    break;
                case SOLICITUD_SELECCION_PUTFILE:
                    ficheroSeleccionado = data.getData();
                    String[] proyeccionFile = {MediaStore.Images.Media.DATA};
                    cursor = getContentResolver().query(ficheroSeleccionado,
                            proyeccionFile, null, null, null);
                    cursor.moveToFirst();
                    rutaImagen = cursor.getString( cursor.getColumnIndex(proyeccionFile[0]));
                    cursor.close();
                    subirAFirebaseStorage(SOLICITUD_SUBIR_PUTFILE, rutaImagen);
                    break;
            }
        }

    }

    public void subirAFirebaseStorage(Integer opcion, String ficheroDispositivo) {
        final ProgressDialog progresoSubida = new ProgressDialog(EventoDetalles.this);
        progresoSubida.setTitle("Subiendo...");
        progresoSubida.setMessage("Espere...");
        progresoSubida.setCancelable(true);
        progresoSubida.setCanceledOnTouchOutside(false);
        progresoSubida.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadTask.cancel();
            }
        });

        String fichero = evento;
        imagenRef = getStorageReference().child(fichero);
        try {
            switch (opcion) {
                case SOLICITUD_SUBIR_PUTDATA:
                    imgImagen.setDrawingCacheEnabled(true);
                    imgImagen.buildDrawingCache();
                    Bitmap bitmap = imgImagen.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    uploadTask = imagenRef.putBytes(data);
                    break;
                case SOLICITUD_SUBIR_PUTSTREAM:
                    InputStream stream = new FileInputStream( new File(ficheroDispositivo));
                    uploadTask = imagenRef.putStream(stream);
                    break;
                case SOLICITUD_SUBIR_PUTFILE:
                    Uri file = Uri.fromFile(new File(ficheroDispositivo));
                    uploadTask = imagenRef.putFile(file);
                    break;
            }
            uploadTask .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    subiendoDatos=false;
                    mostrarDialogo(getApplicationContext(), "Ha ocurrido un error al" +
                            " subir la imagen o el usuario ha cancelado la subida.");
                }
            })
            .addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagenRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            Map<String, Object> datos = new HashMap<>();
                            datos.put("imagen",uri.toString());
                            FirebaseFirestore.getInstance().collection("eventos")
                                    .document(evento).set(datos, SetOptions.merge());
                            new DownloadImageTask( (ImageView) imgImagen).execute(uri.toString());
                            progresoSubida.dismiss();
                    subiendoDatos=false; mostrarDialogo(getApplicationContext(),
                                    "Imagen subida correctamente.");
                        }
                    });
                }
            })
            .addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    if (!subiendoDatos) {
                        progresoSubida.show();
                        subiendoDatos=true;
                    } else {
                        if (taskSnapshot.getTotalByteCount()>0)
                            progresoSubida.setMessage("Espere... " +
                                    String.valueOf(100*taskSnapshot.getBytesTransferred()
                                    /taskSnapshot.getTotalByteCount())+"%");
                    }
                }
            })
            .addOnPausedListener(
                    new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    //UploadTask pausa
                    subiendoDatos=false;
                    mostrarDialogo(getApplicationContext(), "La subida ha sido pausada.");
                }
            });
        }catch (IOException e) {
            mostrarDialogo(getApplicationContext(), e.toString());
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imagenRef != null) {
        outState.putString("EXTRA_STORAGE_REFERENCE_KEY", imagenRef.toString());
        }
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String stringRef = savedInstanceState
                .getString("EXTRA_STORAGE_REFERENCE_KEY");
        if (stringRef == null) {
            return;
        }
        imagenRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(stringRef);

        List<UploadTask> tasks = imagenRef.getActiveUploadTasks();
        for( UploadTask task : tasks ) {
            task .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    upload_error(exception);
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    upload_exito(taskSnapshot);
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    upload_progreso(taskSnapshot);
                }
            })
            .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    upload_pausa(taskSnapshot);
                }
            });
        }

        List<FileDownloadTask> downloadtasks = imagenRef.getActiveDownloadTasks();
        for( FileDownloadTask task : downloadtasks ) {
            task .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    download_error(exception);
                }
            })
            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    download_exito(taskSnapshot);
                }
            });
        }


    }

    private void upload_error(Exception exception){
        subiendoDatos=false; mostrarDialogo(getApplicationContext(),
            "Ha ocurrido un error al subir la imagen o " +
                    "el usuario ha cancelado la subida.");
    }

    private void upload_exito(UploadTask.TaskSnapshot taskSnapshot){
        imagenRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUrl = uri;
                Map<String, Object> datos = new HashMap<>();
                datos.put("imagen", uri.toString());
                FirebaseFirestore.getInstance().collection("eventos")
                        .document(evento).set(datos, SetOptions.merge());
                new DownloadImageTask( (ImageView) imgImagen).execute(uri.toString());
                if (progresoSubida != null) {
                    progresoSubida.dismiss();
                }
                subiendoDatos = false;
                mostrarDialogo(getApplicationContext(),
                        "Imagen subida correctamente.");
            }
        });
    }

    private void upload_progreso(UploadTask.TaskSnapshot taskSnapshot){
        if (!subiendoDatos) {
            progresoSubida = new ProgressDialog(EventoDetalles.this);
            progresoSubida.setTitle("Subiendo...");
            progresoSubida.setMessage("Espere...");
            progresoSubida.setCancelable(true);
            progresoSubida.setCanceledOnTouchOutside(false);
            progresoSubida.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadTask.cancel();
                }
            });
            progresoSubida.show();
            subiendoDatos=true;
        }else{
            if (taskSnapshot.getTotalByteCount()>0)
                progresoSubida.setMessage("Espere... " +
                        String.valueOf(100*taskSnapshot.getBytesTransferred()
                                /taskSnapshot.getTotalByteCount())+"%");
        }
    }

    private void upload_pausa(UploadTask.TaskSnapshot taskSnapshot){
        subiendoDatos=false;
        mostrarDialogo(getApplicationContext(), "La subida ha sido pausada.");
    }

    public void descargarDeFirebaseStorage(String fichero) {
        StorageReference referenciaFichero = getStorageReference().child(fichero);
        File rootPath = new File(Environment.getExternalStorageDirectory(), "Eventos");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,evento+".jpg");
        referenciaFichero.getFile(localFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        mostrarDialogo(getApplicationContext(),
                                "Fichero descargado con éxito: "+localFile.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mostrarDialogo(getApplicationContext(),
                                "Error al descargar el fichero.");
                    }

        });
    }

    private void download_error(Exception exception){
        bajandoDatos=false;
        mostrarDialogo(getApplicationContext(),
                "Ha ocurrido un error al descargar la imagen");
    }

    private void download_exito(FileDownloadTask.TaskSnapshot taskSnapshot){
        bajandoDatos = false;
        mostrarDialogo(getApplicationContext(),
                "Imagen descargada correctamente.");
    }

    private void borrarFileDeFirebaseStorage(String fichero) {
        StorageReference referenciaFichero = getStorageReference().child(fichero);
        referenciaFichero.delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mostrarDialogo(getApplicationContext(),
                        "Fichero borrado correctmente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarDialogo(getApplicationContext(),
                        "Ha ocurrido un error al borrar el fichero");
            }
        });
    }

    public  void confirmarBorrarImagen(final Activity actividad, final String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);

        builder.setTitle("Eliminar Imagen")
                .setMessage("¿Estás seguro de querer eliminar la imagen?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                                borrarFileDeFirebaseStorage(file);
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onNegativeButtonClick();
                            }
                        });
        builder.create().show();
    }

    private void invitarDescuento(){
        String dlInvitacionDescuento;
        dlInvitacionDescuento = "https://eventos67df9.page.link/"; //(1)
        dlInvitacionDescuento = dlInvitacionDescuento +
                "?link=https://descuento.eventos-67df9.firebaseapp.com"; //(2)
        dlInvitacionDescuento = dlInvitacionDescuento + "?descuento%3D15%26evento%3D"+evento; //(3)
        dlInvitacionDescuento = dlInvitacionDescuento + "&apn=org.example.eventos"; //(4)
        dlInvitacionDescuento = dlInvitacionDescuento + "&afl=https://www.androidcurso.com/"; //(5)
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Eventos con decuento del 15%:\n\n" + dlInvitacionDescuento);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Eventos con descuento");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }

    @Override
    protected void onResume(){
        super.onResume();
        mTrace.start();
    }
    @Override
    protected void onStop(){
        super.onStop();
        mTrace.stop();
    }

}
