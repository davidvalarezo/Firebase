package org.example.eventos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EventosFirestore {
    public static String EVENTOS = "eventos";
    static String SERVIDOR="http://curso-firebase.000webhostapp.com/";
    static ArrayList<String> eventosID = new ArrayList<String>();

    public static void crearEventos() {
        Evento evento;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        evento = new Evento("Carnaval", "Rio de Janeiro", "21/02/2017",
                SERVIDOR+"imagenes/carnaval.jpg");
        db.collection(EVENTOS).document("carnaval").set(evento);
        evento = new Evento("Fallas", "Valencia", "19/03/2017",
                SERVIDOR+"imagenes/fallas.jpg");
        db.collection(EVENTOS).document("fallas").set(evento);
        evento = new Evento("Nochevieja", "Nueva York", "31/12/2016",
                SERVIDOR+"imagenes/nochevieja.jpg");
        db.collection(EVENTOS).document("nochevieja").set(evento);
        evento = new Evento("Noche de San Juan", "Alicante", "23/06/2017",
                SERVIDOR+"imagenes/sanjuan.jpg");
        db.collection(EVENTOS).document("sanjuan").set(evento);
        evento = new Evento("Semana Santa", "Sevilla", "14/04/2017",
                SERVIDOR+"imagenes/semanasanta.jpg");
        db.collection(EVENTOS).document("semanasanta").set(evento);
    }

    public static void listarEventos() {
        //Evento evento;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(EVENTOS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventosID.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                eventosID.add(document.getId());
                                Log.d(TAG, document.getId());
                                //System.out.println(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static boolean encontrarEventos(String evento){
        if(!eventosID.isEmpty()) {
            for(String ev: eventosID){
                if(ev.equals(evento)){
                    return true;
                }
            }
        }
        return false;
    }

    public static String mensajeRecibido(String mensaje){
        String[] atributos = mensaje.split("\n");
        String[] evento = atributos[0].split(" ");
        return evento[1];
    }
}
