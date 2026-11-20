package com.example.comanda.Data;

import com.example.comanda.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class UsuarioRepository {
    public UsuarioRepository(){
        auth = FirebaseAuth.getInstance();
    }
}
