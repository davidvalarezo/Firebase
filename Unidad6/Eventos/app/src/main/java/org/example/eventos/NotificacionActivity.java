package org.example.eventos;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

import static org.example.eventos.Comun.enviarNotificacion;

public class NotificacionActivity extends AppCompatActivity {
    private TextInputLayout mensajeNotificacion;
    private EditText tituloNotificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        mensajeNotificacion = findViewById(R.id.textInputLayout);
        tituloNotificacion = findViewById(R.id.txtTitulo);
    }

    public void enviarMensaje(View view){
        String notificacion = mensajeNotificacion.getEditText().getText().toString();
        String titulo = tituloNotificacion.getText().toString();

        if(!notificacion.isEmpty() || notificacion != null){
            if(titulo.isEmpty()) titulo = "";
            enviarNotificacion(getApplicationContext(), notificacion, titulo);

            Toast.makeText(NotificacionActivity.this,
                    "Notificación enviada!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(NotificacionActivity.this,
                    "Ingresa un mensaje para enviar la notificación.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
