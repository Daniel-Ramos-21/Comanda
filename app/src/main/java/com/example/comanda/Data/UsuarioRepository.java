package com.example.comanda.Data;

import com.example.comanda.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class UsuarioRepository {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Callback para autenticación
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String mensaje);
    }

    // Callback para perfil
    public interface PerfilCallback {
        void onSuccess(Usuario usuario);
        void onError(String mensaje);
    }

    public UsuarioRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Registrar usuario
    public void registrarUsuario(String nombre, String correo,
                                 String contrasena, String telefono,
                                 AuthCallback callback) {

        auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        callback.onError(task.getException() != null
                                ? task.getException().getMessage()
                                : "Error al registrar usuario.");
                        return;
                    }

                    FirebaseUser firebaseUser = task.getResult().getUser();

                    if (firebaseUser == null) {
                        callback.onError("No se pudo obtener el usuario.");
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    Usuario usuario = new Usuario(
                            uid,
                            nombre,
                            correo,
                            telefono,
                            new Date()
                    );

                    db.collection("usuarios")
                            .document(uid)
                            .set(usuario)
                            .addOnSuccessListener(unused ->
                                    callback.onSuccess(firebaseUser)
                            )
                            .addOnFailureListener(error ->
                                    callback.onError(
                                            "La cuenta se creó, pero no se pudo guardar el perfil: "
                                                    + error.getMessage()
                                    )
                            );
                });
    }

    // Iniciar sesión
    public void iniciarSesion(String correo, String contrasena,
                              AuthCallback callback) {

        auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = task.getResult().getUser();

                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onError("No se pudo obtener el usuario.");
                        }

                    } else {
                        callback.onError(task.getException() != null
                                ? task.getException().getMessage()
                                : "Error al iniciar sesión.");
                    }
                });
    }

    // Cerrar sesión
    public void cerrarSesion() {
        auth.signOut();
    }

    // Comprobar si existe una sesión activa
    public boolean haySesionActiva() {
        return auth.getCurrentUser() != null;
    }

    // Obtener usuario autenticado
    public FirebaseUser obtenerUsuarioActual() {
        return auth.getCurrentUser();
    }

    // Obtener UID del usuario autenticado
    public String obtenerUID() {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario != null) {
            return usuario.getUid();
        }

        return null;
    }

    // Obtener perfil desde Firestore
    public void obtenerPerfil(PerfilCallback callback) {

        String uid = obtenerUID();

        if (uid == null) {
            callback.onError("No hay una sesión activa.");
            return;
        }

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        Usuario usuario = document.toObject(Usuario.class);

                        if (usuario != null) {
                            callback.onSuccess(usuario);
                        } else {
                            callback.onError("No se pudo convertir el perfil.");
                        }

                    } else {
                        callback.onError("No se encontró el perfil.");
                    }
                })
                .addOnFailureListener(error ->
                        callback.onError(error.getMessage())
                );
    }

    // Actualizar perfil en Firestore
    public void actualizarPerfil(Usuario usuario,
                                 PerfilCallback callback) {

        String uid = obtenerUID();

        if (uid == null) {
            callback.onError("No hay una sesión activa.");
            return;
        }

        // Garantizar que el perfil pertenece al usuario actual
        usuario.setUID(uid);

        db.collection("usuarios")
                .document(uid)
                .set(usuario)
                .addOnSuccessListener(unused ->
                        callback.onSuccess(usuario)
                )
                .addOnFailureListener(error ->
                        callback.onError(error.getMessage())
                );
    }
}