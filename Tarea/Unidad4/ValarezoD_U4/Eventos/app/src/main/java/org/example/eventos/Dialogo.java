package org.example.eventos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import static org.example.eventos.EventosFirestore.encontrarEventos;
import static org.example.eventos.EventosFirestore.mensajeRecibido;

public class Dialogo extends AppCompatActivity {
    String mensaje;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("mensaje")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Mensaje:");
            mensaje = extras.getString("mensaje");
            alertDialog.setMessage(extras.getString("mensaje"));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CERRAR",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String idEvento = mensajeRecibido(mensaje);
                    if (encontrarEventos(idEvento) ) {
                        try{
                        Context context = getBaseContext();
                        //String idEvento = EVENTO_NAME;
                        Intent intent = new Intent(context, EventoDetalles.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("evento", idEvento);
                        context.startActivity(intent);
                        }catch (Exception ex){

                        }
                    }
                    finish();
                }
            });
            alertDialog.show();
            extras.remove("mensaje");
        }
    }

}
