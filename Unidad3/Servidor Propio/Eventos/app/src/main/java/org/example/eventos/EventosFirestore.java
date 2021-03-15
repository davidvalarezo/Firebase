package org.example.eventos;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventosFirestore {
    public static String EVENTOS = "eventos";
    static String SERVIDOR="http://curso-firebase.000webhostapp.com/";

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
}
