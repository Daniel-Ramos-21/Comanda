package com.example.comanda.Data;
import  com.google.firebase.firestore.FirebaseFirestore;
public class FirebaseManager {
    //obtiene la instancia de nuestra base de datos.
    //mantiene una única instancia compartida.
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseManager() {
    }

    //permite acceder a Firestore desde otras clases.
    public static FirebaseFirestore getDb(){
        return db;
    }
}
