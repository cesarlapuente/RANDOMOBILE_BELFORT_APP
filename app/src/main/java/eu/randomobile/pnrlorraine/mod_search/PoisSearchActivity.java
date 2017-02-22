package eu.randomobile.pnrlorraine.mod_search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
import eu.randomobile.pnrlorraine.mod_events.EventsListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;

public class PoisSearchActivity extends Activity {
    public static final String PARAM_KEY_HIDE_EVENTS = "hide_events";
    private MainApp app;

    private ImageMap mImageMap = null;

    private CheckBox cb1 = null, cb2 = null, cb3 = null, cb4 = null, cb5 = null;
    private TextView lb1 = null, lb2 = null, lb3 = null, lb4 = null, lb5 = null;

    private ImageButton btnSearch = null;

    private Boolean paramHideEvents = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_discover__layout_search_pois);

        this.app = (MainApp) getApplication();

        //Para el tratamiento del menu
        mImageMap = (ImageMap) findViewById(R.id.map_poisSearch);
        mImageMap.setAttributes(true, false, (float) 1.0, "menu_busqueda");
        mImageMap.setImageResource(R.drawable.menu_busqueda_pois);

        // Recoger par�metros
        Bundle b = getIntent().getExtras();

        if (b != null) {
            paramHideEvents = b.getBoolean(PARAM_KEY_HIDE_EVENTS);
        }

        capturarControles();
        inicializarForm();
        escucharEventos();
    }

    private void capturarControles() {
        btnSearch = (ImageButton) findViewById(R.id.btn_buscar);
        cb1 = (CheckBox) findViewById(R.id.cb_1);
        cb2 = (CheckBox) findViewById(R.id.cb_2);
        cb3 = (CheckBox) findViewById(R.id.cb_3);
        cb4 = (CheckBox) findViewById(R.id.cb_4);
        cb5 = (CheckBox) findViewById(R.id.cb_5);

        lb1 = (TextView) findViewById(R.id.lb_1);
        lb2 = (TextView) findViewById(R.id.lb_2);
        lb3 = (TextView) findViewById(R.id.lb_3);
        lb4 = (TextView) findViewById(R.id.lb_4);
        lb5 = (TextView) findViewById(R.id.lb_5);
    }

    private void inicializarForm() {
        cb1.setChecked(PoisSearch.getCheck1());
        cb2.setChecked(PoisSearch.getCheck2());
        cb3.setChecked(PoisSearch.getCheck3());
        cb4.setChecked(PoisSearch.getCheck4());
        cb5.setChecked(PoisSearch.getCheck5());

        Typeface tfScalaBold = Util.fontScala_Bold(app.getApplicationContext());
        lb1.setTypeface(tfScalaBold);
        lb2.setTypeface(tfScalaBold);
        lb3.setTypeface(tfScalaBold);
        lb4.setTypeface(tfScalaBold);
        lb5.setTypeface(tfScalaBold);
    }

    public void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }

    private void cargaActivityHome() {
        Intent intent = new Intent(PoisSearchActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargaActivityOptions() {
        Intent intent = new Intent(PoisSearchActivity.this, OptionsActivity.class);
        startActivity(intent);
    }

    private void escucharEventos() {
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                mImageMap.showBubble(id);

                switch (mImageMap.getAreaAttribute(id, "name")) {
                    case "PLUS":
                        cargaActivityOptions();
                        break;
                    case "HOME":
                        cargaActivityHome();
                        break;
                    case "BACK":
                        finish();
                        break;
                    default:
                        mImageMap.showBubble(id);
                }
            }

            @Override
            public void onBubbleClicked(int id) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoisSearchActivity.this, PoisListActivity.class);

                if (applyFilter()) {
                    Bundle bundle = new Bundle();

                    bundle.putString("serchActive", "true");
                    intent.putExtras(bundle);
                }

                startActivity(intent);
                finish();
            }
        });
    }

    private boolean applyFilter() {
        ArrayList<Integer> listaFiltros = new ArrayList<Integer>();

        if (cb1.isChecked()) {
            listaFiltros.add(36);
        }

        if (cb2.isChecked()) {
            listaFiltros.add(30);
        }

        if (cb3.isChecked()) {
            listaFiltros.add(33);
        }

        if (cb4.isChecked()) {
            listaFiltros.add(25);
        }

        if (cb5.isChecked()) {
            listaFiltros.add(28);
        }

        int filtroCategoriasPOIs[] = new int[listaFiltros.size()];

        for (int i = 0; i < listaFiltros.size(); i++) {
            filtroCategoriasPOIs[i] = listaFiltros.get(i);
        }

        app.setFiltroCategoriasPOIs(filtroCategoriasPOIs);

        // Si se ha almacenado, al menos, un elemento en el arraylist de filtros;
        if (listaFiltros.size() > 0) {
            return true;

        } else {
            return false;
        }
    }
}
