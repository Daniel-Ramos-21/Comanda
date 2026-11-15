package com.example.comanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comanda.models.FiltroModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class FiltrosActivity extends AppCompatActivity {

    public static final String EXTRA_FILTROS_ACTUALES = "extra_filtros_actuales";
    public static final String EXTRA_RESULT_FILTROS = "extra_result_filtros";

    private ImageView btnCerrar;
    private ChipGroup chipGroupTipoComida;
    private ChipGroup chipGroupIngredientes;
    private ChipGroup chipGroupRangoPrecio;
    private MaterialButton btnLimpiar;
    private MaterialButton btnAplicar;

    private FiltroModel filtroModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_filtros);

        setupWindowInsets();
        initViews();
        recuperarFiltrosIniciales();
        setupListeners();
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.filtrosRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        btnCerrar = findViewById(R.id.btnCerrarFiltros);
        chipGroupTipoComida = findViewById(R.id.chipGroupTipoComida);
        chipGroupIngredientes = findViewById(R.id.chipGroupIngredientes);
        chipGroupRangoPrecio = findViewById(R.id.chipGroupRangoPrecio);
        btnLimpiar = findViewById(R.id.btnLimpiarFiltros);
        btnAplicar = findViewById(R.id.btnAplicarFiltros);
    }

    @SuppressWarnings("deprecation")
    private void recuperarFiltrosIniciales() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_FILTROS_ACTUALES)) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    filtroModel = getIntent().getSerializableExtra(EXTRA_FILTROS_ACTUALES, FiltroModel.class);
                } else {
                    filtroModel = (FiltroModel) getIntent().getSerializableExtra(EXTRA_FILTROS_ACTUALES);
                }
            } catch (Exception ignored) {
                filtroModel = new FiltroModel();
            }
        }
        if (filtroModel == null) {
            filtroModel = new FiltroModel();
        }

        poblarVistasConFiltro(filtroModel);
    }

    private void poblarVistasConFiltro(FiltroModel f) {
        // 1. Tipo de comida
        if (f.getTipoComida() != null) {
            seleccionarChipPorTexto(chipGroupTipoComida, f.getTipoComida());
        }

        // 2. Ingredientes
        if (f.getIngredientes() != null) {
            for (String ing : f.getIngredientes()) {
                seleccionarChipPorTexto(chipGroupIngredientes, ing);
            }
        }

        // 3. Rango de precio con chips predefinidos de Figma
        Double min = f.getPrecioMin();
        Double max = f.getPrecioMax();

        if (min == null && max == null) {
            checkChip(R.id.chipPrecioCualquiera);
        } else if (max != null && max <= 5.0) {
            checkChip(R.id.chipPrecioHasta5);
        } else if (min != null && min >= 5.0 && max != null && max <= 10.0) {
            checkChip(R.id.chipPrecio5a10);
        } else if (min != null && min >= 10.0 && max != null && max <= 15.0) {
            checkChip(R.id.chipPrecio10a15);
        } else if (min != null && min >= 15.0) {
            checkChip(R.id.chipPrecioMas15);
        } else {
            checkChip(R.id.chipPrecioCualquiera);
        }
    }

    private void checkChip(int chipId) {
        if (chipGroupRangoPrecio != null) {
            chipGroupRangoPrecio.check(chipId);
        }
    }

    private void seleccionarChipPorTexto(ChipGroup group, String texto) {
        if (group == null || texto == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.getText() != null && chip.getText().toString().equalsIgnoreCase(texto)) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    private void setupListeners() {
        btnCerrar.setOnClickListener(v -> finish());
        btnLimpiar.setOnClickListener(v -> limpiarTodosLosCampos());
        btnAplicar.setOnClickListener(v -> aplicarYRetornarResultados());
    }

    private void limpiarTodosLosCampos() {
        chipGroupTipoComida.clearCheck();
        chipGroupIngredientes.clearCheck();
        if (chipGroupRangoPrecio != null) {
            chipGroupRangoPrecio.check(R.id.chipPrecioCualquiera);
        }

        if (filtroModel != null) {
            filtroModel.limpiar();
        }
        Toast.makeText(this, "Filtros restablecidos", Toast.LENGTH_SHORT).show();
    }

    private void aplicarYRetornarResultados() {
        FiltroModel nuevoFiltro = new FiltroModel();

        // 1. Tipo de comida / categoría
        int checkedTipoId = chipGroupTipoComida.getCheckedChipId();
        if (checkedTipoId != View.NO_ID) {
            Chip chip = findViewById(checkedTipoId);
            if (chip != null && chip.getText() != null) {
                nuevoFiltro.setTipoComida(chip.getText().toString());
            }
        }

        // 2. Ingredientes (múltiples)
        List<String> ingredientesSeleccionados = new ArrayList<>();
        List<Integer> checkedIngIds = chipGroupIngredientes.getCheckedChipIds();
        for (Integer id : checkedIngIds) {
            Chip chip = findViewById(id);
            if (chip != null && chip.getText() != null) {
                ingredientesSeleccionados.add(chip.getText().toString());
            }
        }
        nuevoFiltro.setIngredientes(ingredientesSeleccionados);

        // 3. Rango de precio según chip seleccionado en Figma
        int checkedPrecioId = chipGroupRangoPrecio != null ? chipGroupRangoPrecio.getCheckedChipId() : View.NO_ID;
        if (checkedPrecioId == R.id.chipPrecioHasta5) {
            nuevoFiltro.setPrecioMin(0.0);
            nuevoFiltro.setPrecioMax(5.0);
        } else if (checkedPrecioId == R.id.chipPrecio5a10) {
            nuevoFiltro.setPrecioMin(5.0);
            nuevoFiltro.setPrecioMax(10.0);
        } else if (checkedPrecioId == R.id.chipPrecio10a15) {
            nuevoFiltro.setPrecioMin(10.0);
            nuevoFiltro.setPrecioMax(15.0);
        } else if (checkedPrecioId == R.id.chipPrecioMas15) {
            nuevoFiltro.setPrecioMin(15.0);
            nuevoFiltro.setPrecioMax(999.0);
        } else {
            // Cualquier precio
            nuevoFiltro.setPrecioMin(null);
            nuevoFiltro.setPrecioMax(null);
        }

        // Tiempo de preparación: Excluido de los filtros según la especificación de Munchly
        nuevoFiltro.setTiempoPrep(null);

        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_FILTROS, nuevoFiltro);
        setResult(RESULT_OK, data);
        finish();
    }
}
