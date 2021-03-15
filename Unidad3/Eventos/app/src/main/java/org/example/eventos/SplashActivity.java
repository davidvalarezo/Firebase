package org.example.eventos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static org.example.eventos.Comun.mFirebaseRemoteConfig;
import static org.example.eventos.Comun.pais;
import static org.example.eventos.Comun.colorFondo;
import static org.example.eventos.Comun.acercaDe;

public class SplashActivity extends Activity {

    // Duración en milisegundos que se mostrará el splash
    private final int DURACION_SPLASH = 3000; // 3 segundos

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tenemos una plantilla llamada splash.xml donde mostraremos la información que queramos (logotipo, etc.)
        setContentView(R.layout.splash);


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


        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
    }
}
