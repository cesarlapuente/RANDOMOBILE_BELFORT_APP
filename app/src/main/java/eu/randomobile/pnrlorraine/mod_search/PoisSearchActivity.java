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
    //private ImageButton btnSearch = null;
    Button btnSearch = null;
    private Boolean paramHideEvents = false;

    private CheckBox cb1 = null, cb2 = null, cb3 = null, cb4 = null, cb5 = null;
    private TextView lb1 = null, lb2 = null, lb3 = null, lb4 = null, lb5 = null;

    // ajout sauvage


    Button btnEvents = null;
    CheckBox cbADescubrir = null;
    CheckBox cbOTurismo = null;
    CheckBox cbSMonumentos = null;
    CheckBox cbMuseos = null;
    CheckBox cbPatrimonio = null;
    CheckBox cbParques = null;
    CheckBox cbHebergement = null;
    CheckBox cbHabitacionesHotel = null;
    CheckBox cbColectivos = null;
    CheckBox cbHostales = null;
    CheckBox cbHostalesAire = null;
    CheckBox cbAmueblado = null;
    CheckBox cbResidencias = null;
    CheckBox cbRestaurantes = null;
    TextView lbADescubrir = null;
    TextView lbOTurismo = null;
    TextView lbSMonumentos = null;
    TextView lbMuseos = null;
    TextView lbPatrimonio = null;
    TextView lbParques = null;
    TextView lbHebergement = null;
    TextView lbHabitacionesHotel = null;
    TextView lbColectivos = null;
    TextView lbHostales = null;
    TextView lbHostalesAire = null;
    TextView lbAmueblado = null;
    TextView lbResidencias = null;
    TextView lbRestaurantes = null;

    // fin ajout


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

        /*btnSearch = (ImageButton) findViewById(R.id.btn_buscar);
        cb1 = (CheckBox) findViewById(R.id.cb_1);
        cb2 = (CheckBox) findViewById(R.id.cb_2);
        cb3 = (CheckBox) findViewById(R.id.cb_3);
        cb4 = (CheckBox) findViewById(R.id.cb_4);
        cb5 = (CheckBox) findViewById(R.id.cb_5);

        lb1 = (TextView) findViewById(R.id.lb_1);
        lb2 = (TextView) findViewById(R.id.lb_2);
        lb3 = (TextView) findViewById(R.id.lb_3);
        lb4 = (TextView) findViewById(R.id.lb_4);
        lb5 = (TextView) findViewById(R.id.lb_5);*/

        btnSearch = (Button) findViewById(R.id.btn_buscar);
        btnEvents = (Button) findViewById(R.id.btn_eventos);
        btnEvents = (Button) findViewById(R.id.btn_eventos);
        cbADescubrir = (CheckBox) findViewById(R.id.cb_ADescubrir);
        cbOTurismo = (CheckBox) findViewById(R.id.cb_OTurismo);
        cbSMonumentos = (CheckBox) findViewById(R.id.cb_SMonumentos);
        cbMuseos = (CheckBox) findViewById(R.id.cb_Museos);
        cbPatrimonio = (CheckBox) findViewById(R.id.cb_Patrimonio);
        cbParques = (CheckBox) findViewById(R.id.cb_Parques);
        cbHebergement = (CheckBox) findViewById(R.id.cb_Hebergement);
        cbHabitacionesHotel = (CheckBox) findViewById(R.id.cb_HabitacionesHotel);
        cbColectivos = (CheckBox) findViewById(R.id.cb_Colectivos);
        cbHostales = (CheckBox) findViewById(R.id.cb_Hostales);
        cbHostalesAire = (CheckBox) findViewById(R.id.cb_HostalesAire);
        cbAmueblado = (CheckBox) findViewById(R.id.cb_Amueblado);
        cbResidencias = (CheckBox) findViewById(R.id.cb_Residencias);
        cbRestaurantes = (CheckBox) findViewById(R.id.cb_Restaurantes);

        lbADescubrir = (TextView) findViewById(R.id.lb_ADescubrir);
        lbOTurismo = (TextView) findViewById(R.id.lb_OTurismo);
        lbSMonumentos = (TextView) findViewById(R.id.lb_SMonumentos);
        lbMuseos = (TextView) findViewById(R.id.lb_Museos);
        lbPatrimonio = (TextView) findViewById(R.id.lb_Patrimonio);
        lbParques = (TextView) findViewById(R.id.lb_Parques);
        lbHebergement = (TextView) findViewById(R.id.lb_Hebergement);
        lbHabitacionesHotel = (TextView) findViewById(R.id.lb_HabitacionesHotel);
        lbColectivos = (TextView) findViewById(R.id.lb_Colectivos);
        lbHostales = (TextView) findViewById(R.id.lb_Hostales);
        lbHostalesAire = (TextView) findViewById(R.id.lb_HostalesAire);
        lbAmueblado = (TextView) findViewById(R.id.lb_Amueblado);
        lbResidencias = (TextView) findViewById(R.id.lb_Residencias);
        lbRestaurantes = (TextView) findViewById(R.id.lb_Restaurantes);
    }

    private void inicializarForm() {
        /*cb1.setChecked(PoisSearch.getCheck1());
        cb2.setChecked(PoisSearch.getCheck2());
        cb3.setChecked(PoisSearch.getCheck3());
        cb4.setChecked(PoisSearch.getCheck4());
        cb5.setChecked(PoisSearch.getCheck5());

        Typeface tfScalaBold = Util.fontScala_Bold(app.getApplicationContext());
        lb1.setTypeface(tfScalaBold);
        lb2.setTypeface(tfScalaBold);
        lb3.setTypeface(tfScalaBold);
        lb4.setTypeface(tfScalaBold);
        lb5.setTypeface(tfScalaBold);*/

        // Ocultar el botón de eventos si no tiene que estar
        //  (cuando se da al boton de pois en la pantalla detalle de una ruta)
        if (paramHideEvents)
            btnEvents.setVisibility(View.GONE);
        cbOTurismo.setChecked(PoisSearch.getCheckOTurismo());
        cbSMonumentos.setChecked(PoisSearch.getCheckSMonumentos());
        cbMuseos.setChecked(PoisSearch.getCheckMuseos());
        cbPatrimonio.setChecked(PoisSearch.getCheckPatrimonio());
        cbParques.setChecked(PoisSearch.getCheckParques());
        cbADescubrir.setChecked(PoisSearch.getCheckADescubrir());
        cbHebergement.setChecked(PoisSearch.getCheckHebergement());
        cbHabitacionesHotel.setChecked(PoisSearch.getCheckHabitacionesHotel());
        cbColectivos.setChecked(PoisSearch.getCheckColectivos());
        cbHostales.setChecked(PoisSearch.getCheckHostales());
        cbHostalesAire.setChecked(PoisSearch.getCheckHostalesAire());
        cbAmueblado.setChecked(PoisSearch.getCheckAmueblado());
        cbResidencias.setChecked(PoisSearch.getCheckResidencias());
        cbRestaurantes.setChecked(PoisSearch.getCheckRestaurantes());

        Typeface tfScalaBold = Util.fontScala_Bold(app.getApplicationContext());
        lbOTurismo.setTypeface(tfScalaBold);
        lbSMonumentos.setTypeface(tfScalaBold);
        lbMuseos.setTypeface(tfScalaBold);
        lbPatrimonio.setTypeface(tfScalaBold);
        lbParques.setTypeface(tfScalaBold);
        lbADescubrir.setTypeface(tfScalaBold);
        lbHebergement.setTypeface(tfScalaBold);
        lbHabitacionesHotel.setTypeface(tfScalaBold);
        lbColectivos.setTypeface(tfScalaBold);
        lbHostales.setTypeface(tfScalaBold);
        lbHostalesAire.setTypeface(tfScalaBold);
        lbAmueblado.setTypeface(tfScalaBold);
        lbResidencias.setTypeface(tfScalaBold);
        lbRestaurantes.setTypeface(tfScalaBold);
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
        /*mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
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
        });*/

        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap)
            {

                // when the area is tapped, show the name in a
                // text bubble
                mImageMap.showBubble(id);
                switch (mImageMap.getAreaAttribute(id, "name"))
                {
                    case "PLUS": cargaActivityOptions();
                        break;
                    case "HOME": cargaActivityHome();
                        break;
                    case  "BACK": finish();
                        break;
                    default: mImageMap.showBubble(id);
                }

            }

            @Override
            public void onBubbleClicked(int id)
            {
                // react to info bubble for area being tapped

            }
        });


        cbADescubrir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked()) {
                    PoisSearch.setCheckADescubrir(true);
                    cbOTurismo.setChecked(true);
                    PoisSearch.setCheckOTurismo(true);
                    cbSMonumentos.setChecked(true);
                    PoisSearch.setCheckSMonumentos(true);
                    cbMuseos.setChecked(true);
                    PoisSearch.setCheckMuseos(true);
                    cbPatrimonio.setChecked(true);
                    PoisSearch.setCheckPatrimonio(true);
                    cbParques.setChecked(true);
                    PoisSearch.setCheckParques(true);
                }
                else {
                    PoisSearch.setCheckADescubrir(false);
                    cbOTurismo.setChecked(false);
                    PoisSearch.setCheckOTurismo(false);
                    cbSMonumentos.setChecked(false);
                    PoisSearch.setCheckSMonumentos(false);
                    cbMuseos.setChecked(false);
                    PoisSearch.setCheckMuseos(false);
                    cbPatrimonio.setChecked(false);
                    PoisSearch.setCheckPatrimonio(false);
                    cbParques.setChecked(false);
                    PoisSearch.setCheckParques(false);

                }
            }
        });

        cbHebergement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked()) {
                    PoisSearch.setCheckHebergement(true);
                    cbHabitacionesHotel.setChecked(true);
                    PoisSearch.setCheckHabitacionesHotel(true);
                    cbColectivos.setChecked(true);
                    PoisSearch.setCheckColectivos(true);
                    cbHostales.setChecked(true);
                    PoisSearch.setCheckHostales(true);
                    cbHostalesAire.setChecked(true);
                    PoisSearch.setCheckHostalesAire(true);
                    cbAmueblado.setChecked(true);
                    PoisSearch.setCheckAmueblado(true);
                    cbResidencias.setChecked(true);
                    PoisSearch.setCheckResidencias(true);
                }
                else {
                    PoisSearch.setCheckHebergement(false);
                    cbHabitacionesHotel.setChecked(false);
                    PoisSearch.setCheckHabitacionesHotel(false);
                    cbColectivos.setChecked(false);
                    PoisSearch.setCheckColectivos(false);
                    cbHostales.setChecked(false);
                    PoisSearch.setCheckHostales(false);
                    cbHostalesAire.setChecked(false);
                    PoisSearch.setCheckHostalesAire(false);
                    cbAmueblado.setChecked(false);
                    PoisSearch.setCheckAmueblado(false);
                    cbResidencias.setChecked(false);
                    PoisSearch.setCheckResidencias(false);
                }
            }
        });

        cbOTurismo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckOTurismo(true);
                else
                    PoisSearch.setCheckOTurismo(false);

            }
        });

        cbSMonumentos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckSMonumentos(true);
                else
                    PoisSearch.setCheckSMonumentos(false);

            }
        });

        cbMuseos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckMuseos(true);
                else
                    PoisSearch.setCheckMuseos(false);

            }
        });

        cbPatrimonio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckPatrimonio(true);
                else
                    PoisSearch.setCheckPatrimonio(false);

            }
        });

        cbParques.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckParques(true);
                else
                    PoisSearch.setCheckParques(false);

            }
        });


        cbHabitacionesHotel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckHabitacionesHotel(true);
                else
                    PoisSearch.setCheckHabitacionesHotel(false);

            }
        });

        cbColectivos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckColectivos(true);
                else
                    PoisSearch.setCheckColectivos(false);

            }
        });

        cbHostales.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckHostales(true);
                else
                    PoisSearch.setCheckHostales(false);

            }
        });

        cbHostalesAire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckHostalesAire(true);
                else
                    PoisSearch.setCheckHostalesAire(false);

            }
        });

        cbAmueblado.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckAmueblado(true);
                else
                    PoisSearch.setCheckAmueblado(false);

            }
        });

        cbResidencias.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckResidencias(true);
                else
                    PoisSearch.setCheckResidencias(false);

            }
        });


        cbRestaurantes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkProximidad checked?
                if (((CheckBox) v).isChecked())
                    PoisSearch.setCheckRestaurantes(true);
                else
                    PoisSearch.setCheckRestaurantes(false);

            }
        });


        /*btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.putExtra("op", "buscar");
                setResult(RESULT_OK, intent);
                finish();
            }
        });*/
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

        btnEvents.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Abrir la pantalla de eventos
                Intent intent = new Intent(PoisSearchActivity.this,
                        EventsListActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean applyFilter() {
        ArrayList<Integer> listaFiltros = new ArrayList<Integer>();

        /*if (cb1.isChecked()) {
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
        }*/

        if (cbOTurismo.isChecked()) {
            listaFiltros.add(25);
        }

        if (cbSMonumentos.isChecked()) {
            listaFiltros.add(36);
        }
        if (cbMuseos.isChecked()) {
            listaFiltros.add(28);
        }
        if (cbPatrimonio.isChecked()) {
            listaFiltros.add(30);
        }
        if (cbParques.isChecked()) {
            listaFiltros.add(45);
        }
        if (cbADescubrir.isChecked()) {
            listaFiltros.add(20000);
        }
        if (cbHebergement.isChecked()) {
            listaFiltros.add(30000);
        }
        if (cbHabitacionesHotel.isChecked()) {
            listaFiltros.add(47);
        }
        if (cbColectivos.isChecked()) {
            listaFiltros.add(26);
        }
        if (cbHostales.isChecked()) {
            listaFiltros.add(48);
        }
        if (cbHostalesAire.isChecked()) {
            listaFiltros.add(49);
        }
        if (cbAmueblado.isChecked()) {
            listaFiltros.add(50);
        }
        if (cbResidencias.isChecked()) {
            listaFiltros.add(51);
        }
        if (cbRestaurantes.isChecked()) {
            listaFiltros.add(27);
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
