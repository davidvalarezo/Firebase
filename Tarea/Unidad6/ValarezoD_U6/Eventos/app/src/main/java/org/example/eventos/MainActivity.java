package org.example.eventos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener;
import com.google.firebase.inappmessaging.model.Action;
import com.google.firebase.inappmessaging.model.InAppMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static org.example.eventos.Comun.mFirebaseRemoteConfig;
import static org.example.eventos.Comun.mostrarDialogo;
import static org.example.eventos.Comun.pais;
import static org.example.eventos.Comun.storage;
import static org.example.eventos.Comun.storageRef;
import static org.example.eventos.Comun.mFirebaseAnalytics;
import static org.example.eventos.Comun.colorFondo;
import static org.example.eventos.Comun.acercaDe;
import static org.example.eventos.Comun.performMonitoring;
import static org.example.eventos.EventosFirestore.EVENTOS;
import static org.example.eventos.EventosFirestore.crearEventos;
import static org.example.eventos.EventosFirestore.eventosID;
import static org.example.eventos.EventosFirestore.listarEventos;
import static org.example.eventos.Temas.guardarSuscripcionATemaEnPreferencias;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private AdaptadorEventos adaptador;
    private String dlInvitacion = "https://eventos67df9.page.link/fu7j";
    //public boolean resultDialogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //crearEventos();
        Query query = FirebaseFirestore.getInstance()
                .collection(EVENTOS)
                .limit(50);
        FirestoreRecyclerOptions<Evento> opciones = new FirestoreRecyclerOptions
                .Builder<Evento>()
                .setQuery(query, Evento.class).build();
        adaptador = new AdaptadorEventos(opciones);
        final RecyclerView recyclerView = findViewById(R.id.reciclerViewEventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        final SharedPreferences preferencias = getApplicationContext()
                .getSharedPreferences("Temas", Context.MODE_PRIVATE);
        if (preferencias.getBoolean("Inicializado", false)==false){
            final SharedPreferences prefs = getApplicationContext()
                    .getSharedPreferences( "Temas", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Inicializado", true);
            editor.commit();
            FirebaseMessaging.getInstance().subscribeToTopic("Todos");
            System.out.println("Se ha subscrito a todos");
        }

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                Evento currentItem = (Evento) adaptador.getItem(position);
                String idEvento = adaptador.getSnapshots().getSnapshot(position).getId();
                Context context = getApplicationContext();
                Intent intent = new Intent(context, EventoDetalles.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("evento", idEvento);
                context.startActivity(intent);
            }
        });

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl( "gs://eventos-67df9.appspot.com");
        String[] PERMISOS =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, PERMISOS, 1);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        listarEventos();

/*
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
                        Toast.makeText(MainActivity.this, "Remote config error.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
*/

        inAppClickListener inAppListener = new inAppClickListener();
        FirebaseInAppMessaging.getInstance().addClickListener(inAppListener);
        FirebaseInAppMessaging.getInstance() .setAutomaticDataCollectionEnabled(true);

        //Dialogo para Activar o Desactivar Crashlytics
        if (preferencias.getBoolean("InformeErrores", false)==false){
            final SharedPreferences prefs = getApplicationContext()
                    .getSharedPreferences( "Temas", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String msg = "Con el fin de mejorar la aplicación, te pedimos que participes en el envío " +
                    "automático de errores a nuestros servidores. ¿Estás de acuerdo?";
            confirmarDialogo(this, "Activar informes", msg);
            editor.putBoolean("InformeErrores", true);
            editor.commit();
            System.out.println("Se ha informado Informe-Errores");
        }
        //Inicializa Crashlytics Crashlytics
        if(preferencias.getBoolean("Crashlytics", false)==true){
            activaInformesErrorCrashlytics();
        }

        //Inicializa Controlar Performance Monitoring de forma remota
        try{
            if(performMonitoring != null){
                FirebasePerformance.getInstance()
                        .setPerformanceCollectionEnabled(performMonitoring);
                System.out.println("Se ha Activado Performance Monitoring "+performMonitoring);
            }
        }catch (Exception ex){ }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_temas) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "suscripciones");
            mFirebaseAnalytics.logEvent("menus", bundle);
            Intent intent = new Intent(getBaseContext(), Temas.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.enviar_evento) {
            Intent intent = new Intent(getBaseContext(), NotificacionActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_invitar){
            invitar();
        }
        if (id == R.id.action_error) {
            Crashlytics.getInstance().crash();
            return true;
        }
        if (id == R.id.action_facebook) {
            Intent intent = new Intent(getBaseContext(), FacebookActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_twitter) {
            Intent intent = new Intent(getBaseContext(), TwitterActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart() {
        super.onStart();
        adaptador.startListening();
    }

    @Override public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }

    @Override protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.keySet().size()>7) {
            String evento="";
            evento ="Evento: "+extras.getString("evento")+ "\n";
            evento = evento + "Día: "+ extras.getString("dia")+ "\n";
            evento = evento +"Ciudad: "+extras.getString("ciudad")+ "\n";
            evento = evento +"Comentario: "+extras.getString("comentario");
            mostrarDialogo(getApplicationContext(), evento);
            for (String key : extras.keySet()) {
                getIntent().removeExtra(key);
            }
            extras = null;
        }

        //Inicializa Crashlytics
        final SharedPreferences preferencias = getApplicationContext()
                .getSharedPreferences("Temas", Context.MODE_PRIVATE);
        if (preferencias.getBoolean("Crashlytics", false)==true){
            activaInformesErrorCrashlytics();
        }
    }

    /*public static Context getAppContext() {
        return MainActivity.getAppContext();//getCurrentContext();
    }*/

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[],
                                                     int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MainActivity.this,
                            "Has denegado algún permiso de la aplicación.",
                            Toast.LENGTH_SHORT).show();
                    }
                return;
            }
        }
    }

    private void invitar() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "No te pierdas nunca más un " +
                "evento interesante.:\n\n" + dlInvitacion);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Envio de una invitación a la app Eventos");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }

/*    private void getColorFondo() {
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
    }*/

    public class inAppClickListener implements FirebaseInAppMessagingClickListener {
        @Override public void messageClicked(InAppMessage inAppMessage, Action action) {
            String evento=""; evento ="ID Campaña: "
                    +inAppMessage.getCampaignMetadata().getCampaignId();
            mostrarDialogo(getApplicationContext(), evento);
        }
    }

    public void activaInformesErrorCrashlytics(){
        //Inicializa Crashlytics
        Fabric.with(this, new Crashlytics());
        System.out.println("Activando Crashlytics");
        Crashlytics.log(1 , "Crashlytics", "Activando Crashlytics");
        //Crashlytics.setUserIdentifier(String identifier);
        //Toast.makeText(this, "Se ha activado Crashlytics", Toast.LENGTH_LONG);
    }

    public static void confirmarDialogo(final Activity actividad, final String title, final String message) {

        final SharedPreferences prefs = actividad.getBaseContext()
                .getSharedPreferences( "Temas", Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("Crashlytics", true);
                                editor.commit();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onNegativeButtonClick();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("Crashlytics", false);
                                editor.commit();
                            }
                        });
        builder.create().show();
    }


}
