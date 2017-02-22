package eu.randomobile.pnrlorraine.mod_guia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class GuiaNaturalezaEspecies extends Activity {
    private ImageMap mImageMap;

    //Get ESpecies
    // Request JSON
    AsyncHttpClient client;
    Gson gson;
    private final String URL_SERVICE = "http://pajara.randomobile.eu/api/routedata/route/retrieve.json";
    ArrayList<Especie> especies = new ArrayList<Especie>();
    ListView lvEspecies;
    MainApp app;
    EspeciesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_naturaleza_especies);

        app = (MainApp) this.getApplication();

        // find the image map in the view
        mImageMap = (ImageMap) findViewById(R.id.menu_naturaleza_intro);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_naturaleza_intro");
        mImageMap.setImageResource(R.drawable.naturaleza_especies_on);
        mImageMap.addOnImageMapClickedHandler(this.crearListenerMenu());

        lvEspecies = (ListView) findViewById(R.id.listViewEspecies);

        getData();
    }

    private ImageMap.OnImageMapClickedHandler crearListenerMenu() {
        ImageMap.OnImageMapClickedHandler listener = null;

        try {
            listener = new ImageMap.OnImageMapClickedHandler() {
                @Override
                public void onImageMapClicked(int id, ImageMap imageMap) {

                    if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
                        cargaActivityHome();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "BACK")) {
                        finish();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "INTRO")) {
                        Log.d("Guia Naturaleza: ", "Intro pulsado");
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "ESPECIES")) {
                        Log.d("Guia Naturaleza: ", "Especies pulsado");
                        goToEspecies();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "BUSCAR")) {
                        Log.d("Guia Naturaleza: ", "Buscar pulsado");
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "ESPACIOS")) {
                        Log.d("Guia Naturaleza: ", "Espacios pulsado");
                        goToEspacios();
                    } else {
                        // when the area is tapped, show the name in a
                        // text bubble
                        mImageMap.showBubble(id);
                    }
                }

                @Override
                public void onBubbleClicked(int id) {
                }
            };
        } catch (Exception ex) {
        }
        return listener;
    }

    private void goToEspecies() {
        Intent intent = new Intent(GuiaNaturalezaEspecies.this, GuiaNaturalezaEspecies.class);
        startActivity(intent);
    }

    private void goToEspacios() {
        Intent intent = new Intent(GuiaNaturalezaEspecies.this, GuiaNaturalezaEspacios.class);
        startActivity(intent);
    }


    private void cargaActivityHome() {
        Intent intent = new Intent(GuiaNaturalezaEspecies.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }

    public void getData() {
        especies = app.getEspecies();

        // Comprobamos si hay parametros en el Bundle.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Si hay parametros, miramos si corresponden al "Filtro de especie".
            if (bundle.get("fromRouteDetail").equals("true")) {
                Log.d("Bundle said:", " I'm in.");

                int espaciosRuta[] = app.getEspaciosRuta();
                Log.d("Bundle said:", " EspaciosRuta size: " + espaciosRuta.length);

                ArrayList<Especie> especiesInRoute = new ArrayList<Especie>();

                for (int i = 0; i < espaciosRuta.length; i++){
                    for (Especie especie: especies){
                        if(especie.isEspacioIn(espaciosRuta[i])){
                            especiesInRoute.add(especie);
                            Log.d("Bundle said:", " Added especieId: " + especie.getNid());
                        }
                    }
                }

                especies = especiesInRoute;
                Log.d("Bundle said:", " Especies in route: " + especies.size());
                app.setEspeciesInRoute(especies);
            }
        }

        setAdapter();
    }

    private void setAdapter() {
        adapter = new EspeciesAdapter(this, especies);

        lvEspecies.setAdapter(adapter);

        SetClickListenerTolist();
    }

    private void SetClickListenerTolist() {
        lvEspecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                String nidEspecie = adapter.getItemNID(index);

                app.setEspeciesListaEspacios(null);
                app.setEspeciesListaEspacios(especies.get(index).getEspacios());

                Log.d("", Integer.toString(index));

                Intent intent = new Intent(GuiaNaturalezaEspecies.this,
                        FichaEspecie.class);

                intent.putExtra(app.KEY_ESPECIES_NID, nidEspecie);

                startActivity(intent);
            }
        });
    }
}




