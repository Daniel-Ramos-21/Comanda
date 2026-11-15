package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comanda.Data.FirebaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de bienvenida de Munchly.
 * Presenta la identidad visual gastronómica de la marca y punto de entrada al menú.
 */
public class MainActivity extends AppCompatActivity {

    private MaterialButton btnExplorarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        initViews();
        setupListeners();
    }

    /**
     * Ajusta los insets de la ventana para garantizar que el contenido
     * respete las barras del sistema (status bar y navigation bar).
     */
    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    /**
     * Inicializa las referencias a los componentes de la vista.
     */
    private void initViews() {
        btnExplorarMenu = findViewById(R.id.btnExplorarMenu);
    }

    /**
     * Configura los listeners de interacción de usuario.
     */
    private void setupListeners() {
        if (btnExplorarMenu != null) {
            btnExplorarMenu.setOnClickListener(v -> onExplorarMenuClicked());
        }
    }

    /**
     * Gestiona la acción de explorar menú.
     * Muestra feedback interactivo inmediato y queda preparado para navegar
     * a la actividad principal del catálogo de platos/menú.
     */
    private void onExplorarMenuClicked() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    /**
     * Funcion auxiliar para validar la conexion con Firebase Firestore.
     * Preservada para verificar persistencia en la base de datos sin bloquear la UI.
     */
    @SuppressWarnings("unused")
    private void probarConexioFirebase() {
        FirebaseFirestore db = FirebaseManager.getDb();

        Map<String, Object> datos = new HashMap<>();
        datos.put("mensaje", "Munchly conectado correctamente");
        datos.put("estado", "Activo");

        db.collection("prueba_conexion")
                .document("verificacion")
                .set(datos)
                .addOnSuccessListener(unused -> Toast.makeText(
                        this,
                        "Datos guardados en Firebase",
                        Toast.LENGTH_SHORT
                ).show())
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Error Firebase: " + e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show());
    }
}
