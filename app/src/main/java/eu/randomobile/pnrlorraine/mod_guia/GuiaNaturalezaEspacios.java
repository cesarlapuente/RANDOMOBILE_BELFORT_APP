package eu.randomobile.pnrlorraine.mod_guia;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.RoutesListActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.adapter.PoisAdapter;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_search.PoisSearch;
import eu.randomobile.pnrlorraine.mod_search.PoisSearchActivity;

public class GuiaNaturalezaEspacios extends Activity implements LocationListener, Poi.PoisInterface,
        OfflinePoi.PoisModeOfflineInterface {
    private MainApp app;

    private List<Poi> arrayEspacios = null;
    private ArrayList<Poi> arrayFilteredEspacios = null;

    private ListView listaPois;
    private ListPoisAdapter poiAdaptador;

    private GPS gps;

    private ImageMap mImageMap;
    private RelativeLayout panelCargando;

    private int idFiltroEspecie = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_naturaleza_espacios);

        // Comprobamos si hay parametros en el Bundle.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Si hay parametros, miramos si corresponden al "Filtro de especie".
            if (bundle.get("Filtrar por especie") != null) {
                // Si es asi y este no está vacio, lo guardamos.
                idFiltroEspecie = Integer.parseInt(bundle.get("Filtrar por especie").toString());
                Log.d("ID especie a filtrar: ", bundle.get("Filtrar por especie").toString());
            }
        }

        app = (MainApp) getApplication();

        mImageMap = (ImageMap) findViewById(R.id.menu_naturaleza_intro);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_naturaleza_intro");
        mImageMap.setImageResource(R.drawable.naturaleza_espacios_on);

        initializeComponents();
        initializeGPSandNetwork();
        initializeListeners();
        initializeForm();
        reloadForm();
    }

    // <-------------------->_Initializing_components_methods_<-------------------->

    /**
     * This method is used for connect the .xml components with their variables
     * in the .java.
     */
    private void initializeComponents() {
        listaPois = (ListView) findViewById(R.id.listaEspacios);
        panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
    }

    /**
     * This methods is used for initiliaze the GPS and Network components and, if both of them
     * are disabled, ask the user for enable them throw an alert dialog.
     */
    private void initializeGPSandNetwork() {
        this.gps = new GPS(this, this);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    onBackPressed();
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Volver), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    onBackPressed();
                }
            });
            dialog.show();
        }
    }

    /**
     * This method is used for initialize all the listener components of the
     * activity.
     */
    private void initializeListeners() {
        // Header listener.
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {

                if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
                    loadMainActivity();

                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "BACK")) {
                    finish();

                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "INTRO")) {
                    loadIntroduccionActivity();

                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "ESPECIES")) {
                    loadEspeciesActivity();

                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "BUSCAR")) {
                    // loadSearchActivity();

                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "ESPACIOS")) {

                } else {
                    mImageMap.showBubble(id);
                }
            }

            @Override
            public void onBubbleClicked(int id) {

            }
        });

        // List listener
        listaPois.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Poi poiPulsado = null;

                if (arrayFilteredEspacios != null)
                    poiPulsado = arrayFilteredEspacios.get(index);
                else
                    poiPulsado = arrayEspacios.get(index);

                Intent intent = new Intent(GuiaNaturalezaEspacios.this,
                        PoiDetailActivity.class);
                intent.putExtra(PoiDetailActivity.PARAM_KEY_NID,
                        poiPulsado.getNid());
                intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE,
                        poiPulsado.getDistanceMeters());

                int desnivel = 0;

                if (gps != null) {
                    if (gps.getLastLocation() != null) {
                        desnivel = (int) (poiPulsado.getCoordinates()
                                .getAltitude() - gps.getLastLocation()
                                .getAltitude());
                    }
                }

                intent.putExtra(PoiDetailActivity.PARAM_KEY_DESNIVEL, desnivel);
                intent.putExtra(PoiDetailActivity.PARAM_KEY_NUMBERVOTES,
                        poiPulsado.getVote().getNumVotes());
                intent.putExtra(PoiDetailActivity.PARAM_KEY_VALORATION,
                        poiPulsado.getVote().getValue());
                BitmapManager.INSTANCE.cache.remove(poiPulsado.getMainImage());
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NewApi")
    private void initializeForm() {
        // poner estilos (fuente, color, ...)
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.hide();
            }
        }

        panelCargando.setVisibility(View.GONE);
    }

    // <-------------------->__<-------------------->

    protected void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }

    // <-------------------->_Activities_change_methods_<-------------------->

    /**
     * This method is used for change the current activity (screen) for the
     * MainActivity (the app main menu).
     */
    private void loadMainActivity() {
        Intent intent = new Intent(GuiaNaturalezaEspacios.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * This method is used for change the current activity (screen) for the
     * PoiSearchActivity (the poi search activity).
     */
    private void loadSearchActivity() {
        Intent intent = new Intent(GuiaNaturalezaEspacios.this,
                PoisSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    /**
     * This method is used for change the current activity (screen) for the
     * GuiaNaturalezaEspeciesActivity (the especies activity).
     */
    private void loadEspeciesActivity() {
        // Abrir la pantalla de Guia de Naturaleza
        Intent intent = new Intent(GuiaNaturalezaEspacios.this,
                GuiaNaturalezaEspecies.class);
        startActivity(intent);
    }

    /**
     * This method is used for change the current activity (screen) for the
     * GuiaNaturalezaActivity (the introducction Guia Naturaleza activity).
     */
    private void loadIntroduccionActivity() {
        // Abrir la pantalla de Guia de Naturaleza
        Intent intent = new Intent(GuiaNaturalezaEspacios.this,
                GuiaNaturaleza.class);
        startActivity(intent);
    }

    // <-------------------->_Main_content_<-------------------->

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String name = data.getStringExtra("name");

        if (arrayFilteredEspacios == null)
            arrayFilteredEspacios = new ArrayList<Poi>();

        arrayFilteredEspacios.clear();
        listaPois.invalidateViews();
        arrayFilteredEspacios();
        Cache.filteredPois = arrayFilteredEspacios;

        poiAdaptador = new ListPoisAdapter(this, arrayFilteredEspacios);
        listaPois.setAdapter(poiAdaptador);
    }

    private void arrayFilteredEspacios() {
        if (arrayEspacios != null) {
            for (int i = 0; i < arrayEspacios.size(); i++) {
                if (PoisSearch.checkCriteria(arrayEspacios.get(i), this))
                    arrayFilteredEspacios.add(arrayEspacios.get(i));
            }
        }
    }

    public class ListPoisAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<Poi> listaItems;


        public class ViewHolder {
            RelativeLayout layoutFondo;
            ImageView imgView;
            TextView lblTitulo;
            TextView lblDetalle;
            ImageView imgViewFrame;
            ImageView imgViewCategory;
            TextView lblDistancia;
            TextView lblDistanciaNum;
            TextView lblDesnivel;
            TextView lblDesnivelNum;
            TextView lblValoracion;
            ImageView imgViewValoracion;
            int index;
        }

        public ListPoisAdapter(Context _ctx, ArrayList<Poi> _items) {
            this.listaItems = _items;

            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public int getCount() {
            if (listaItems != null) {
                return listaItems.size();
            } else {
                return 0;
            }
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                Log.d("Milog", "1");
                convertView = mInflater.inflate(
                        R.layout.mod_discover__layout_item_lista_pois, null);
                holder.layoutFondo = (RelativeLayout) convertView
                        .findViewById(R.id.layoutFondo);
                holder.imgView = (ImageView) convertView
                        .findViewById(R.id.imgView);
                holder.lblTitulo = (TextView) convertView
                        .findViewById(R.id.lblTitulo);
                holder.lblDetalle = (TextView) convertView
                        .findViewById(R.id.lblDetalle);
                holder.lblDistancia = (TextView) convertView
                        .findViewById(R.id.lblDist);
                holder.lblDistanciaNum = (TextView) convertView
                        .findViewById(R.id.lblDistNum);
                holder.lblDesnivel = (TextView) convertView
                        .findViewById(R.id.lblDesnivel);
                holder.lblDesnivelNum = (TextView) convertView
                        .findViewById(R.id.lblDesnivelNum);
                holder.imgViewFrame = (ImageView) convertView
                        .findViewById(R.id.imgViewFrame);
                holder.imgViewCategory = (ImageView) convertView
                        .findViewById(R.id.imgViewCategory);
                holder.lblValoracion = (TextView) convertView
                        .findViewById(R.id.lblValoracion);
                holder.imgViewValoracion = (ImageView) convertView
                        .findViewById(R.id.imgViewValoracion);

                // Poner fuentes
                Typeface tfScalaBold = Util.fontScala_Bold(ctx);
                Typeface tfBentonMedium = Util.fontBenton_Medium(ctx);
                Typeface tfBentonBoo = Util.fontBenton_Boo(ctx);
                Typeface tfBentonBold = Util.fontBenton_Bold(ctx);
                holder.lblTitulo.setTypeface(tfScalaBold);
                holder.lblDetalle.setTypeface(tfBentonBoo);
                holder.lblDistancia.setTypeface(tfBentonBoo);
                holder.lblDistanciaNum.setTypeface(tfScalaBold);
                holder.lblDesnivel.setTypeface(tfBentonBoo);
                holder.lblValoracion.setTypeface(tfBentonBoo);
                holder.lblDesnivelNum.setTypeface(tfScalaBold);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Recoger el item
            Poi item = listaItems.get(position);

            // T�tulo
            holder.lblTitulo.setText(item.getTitle());

            // Body con el tipo de categoria
            if ((item.getCategory() != null) && !item.getCategory().getName().equals("null")) {
                holder.lblDetalle.setText(item.getCategory().getName());
            } else {
                String strVacio = ctx.getResources().getString(
                        R.string.mod_global__sin_datos);
                holder.lblDetalle.setText(strVacio);
            }

            // Distancia
            if (item.getDistanceMeters() < 1000) {
                int roundedDistMeters = (int) item.getDistanceMeters();
                holder.lblDistanciaNum.setText(roundedDistMeters + " m");
            } else {
                int roundedDistKms = (int) (item.getDistanceMeters() / 1000);
                holder.lblDistanciaNum.setText(roundedDistKms + " Km");
            }

            // Desnivel
            int desnivel = 0;
            if (gps != null) {
                if (gps.getLastLocation() != null) {
                    desnivel = (int) (item.getCoordinates().getAltitude() - gps
                            .getLastLocation().getAltitude());
                }
            }
            String strDesnivel = "";
            if (desnivel > 0) {
                strDesnivel = "+" + desnivel + " m.";
            } else if (desnivel < 0) {
                strDesnivel = "-" + desnivel + " m.";
            }
            holder.lblDesnivelNum.setText(strDesnivel);

            // Imagen
            if (item.getMainImage() != null) {
                BitmapManager.INSTANCE.loadBitmap(item.getMainImage(),
                        holder.imgView, 80, 60);
            } else {
                if (item.getCategory() != null
                        && item.getCategory().getIcon() != null) {
                    // BitmapManager.INSTANCE.loadBitmap(item.getCategory().getIcon(),
                    // holder.imgView, 36, 40);
                    String category;
                    category = item.getCategory().getName();
                    if (category.equals("Chambre d'h�tes")
                            || category.equals("H�tellerie")
                            || category.equals("H�bergement collectif")
                            || category.equals("H�tellerie de plein air")
                            || category.equals("Meubl�")
                            || category.equals("R�sidence")) {
                        holder.imgView
                                .setBackgroundResource(R.drawable.icono_hotel);
                    } else if (category.equals("Mus�e")
                            || category.equals("Patrimoine Naturel")
                            || category.equals("Site et Monument")
                            || category.equals("Office de Tourisme")
                            || category.equals("Parc et Jardin")) {
                        holder.imgView
                                .setBackgroundResource(R.drawable.icono_descubrir);
                    } else if (category.equals("Restauration"))
                        holder.imgView
                                .setBackgroundResource(R.drawable.icono_restaurante);

                } else {
                    holder.imgView.setImageResource(R.drawable.ic_launcher);
                }
            }

            // Poner la valoraci�n
            if (item.getVote() != null) {
                if (item.getVote().getValue() <= 0) {
                    // Si es menor o igual a 0
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_0_estrellas);
                } else if (item.getVote().getValue() > 0
                        && item.getVote().getValue() < 25) {
                    // Si est� entre 1 y 24
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_1_estrellas);
                } else if (item.getVote().getValue() >= 25
                        && item.getVote().getValue() < 50) {
                    // Si est� entre 25 y 49
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_2_estrellas);
                } else if (item.getVote().getValue() >= 50
                        && item.getVote().getValue() < 75) {
                    // Si est� entre 50 y 74
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_3_estrellas);
                } else if (item.getVote().getValue() >= 75
                        && item.getVote().getValue() <= 90) {
                    // Si est� entre 75 y 90
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_4_estrellas);
                } else {
                    holder.imgViewValoracion
                            .setImageResource(R.drawable.puntuacion_5_estrellas);
                }
            } else {
                holder.imgViewValoracion
                        .setImageResource(R.drawable.puntuacion_0_estrellas);
            }
            String valString = ctx.getResources().getString(
                    R.string.mod_discover__nota);
            holder.lblValoracion.setText(valString + " ("
                    + String.valueOf(item.getVote().getNumVotes()) + " avis)");
            // Poner la flechita de la derecha en funci�n de si la celda es par
            // o no
            if (position % 2 == 0) {
                // Poner flecha 1
                holder.imgViewFrame.setBackgroundResource(R.drawable.frame_pr);
            } else {
                // Poner flecha 2
                holder.imgViewFrame.setBackgroundResource(R.drawable.frame_gr);
            }

            // Poner la categoria
            // Poner la imagen de categoria
            String category;
            if (item.getCategory() != null) {
                category = item.getCategory().getName();
                if (category.equals("Chambre d'h�tes")
                        || category.equals("H�tellerie")
                        || category.equals("H�bergement collectif")
                        || category.equals("H�tellerie de plein air")
                        || category.equals("Meubl�")
                        || category.equals("R�sidence")) {
                    holder.imgViewCategory
                            .setBackgroundResource(R.drawable.icono_hotel);
                } else if (category.equals("Mus�e")
                        || category.equals("Patrimoine Naturel")
                        || category.equals("Site et Monument")
                        || category.equals("Office de Tourisme")
                        || category.equals("Parc et Jardin")) {
                    holder.imgViewCategory
                            .setBackgroundResource(R.drawable.icono_descubrir);
                } else if (category.equals("Restauration"))
                    holder.imgViewCategory
                            .setBackgroundResource(R.drawable.icono_restaurante);

            }

            return convertView;
        }
    }

    private void reloadForm() {
        if (this.gps.getLastLocation() == null) {
            // Si la �ltima posici�n es nula, es que nunca ha localizado.
            // Arrancarlo
            panelCargando.setVisibility(View.VISIBLE);

            this.gps.startLocating();

        } else {
            // Si la �ltima posici�n existe, ya ha localizado alguna vez.
            // Recargar los datos
            panelCargando.setVisibility(View.VISIBLE);

            this.reloadForm();
        }

    }

    private void reloadData() {
        if (Cache.filteredPois != null) {
            this.arrayFilteredEspacios = (ArrayList<Poi>) Cache.filteredPois;

            //Cache.arrayPois = Cache.filteredPois;
            PoisAdapter adapter = new PoisAdapter(this, arrayFilteredEspacios, this.gps);
            listaPois.setAdapter(adapter);

            if (arrayEspacios == null)
                arrayEspacios = Cache.arrayPois;

            panelCargando.setVisibility(View.GONE);

        } else if (DataConection.hayConexion(this)) {
            // Si hay conexi�n, recargar los datos
            panelCargando.setVisibility(View.VISIBLE);
            Poi.poisInterface = this;

            Poi.cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
                    gps.getLastLocation().getLatitude(), // Latitud
                    gps.getLastLocation().getLongitude(), // Longitud
                    0, // Radio en Kms
                    0, // N�mero de elementos por p�gina
                    0, // P�gina
                    "30", // Tid de la categor�a que queremos filtrar  //  "30" para filtrar por espacios naturales.
                    null // Texto a buscar
            );

        } else {
            OfflinePoi.poisInterface = this;
            OfflinePoi.cargaListaPoisOffline(getApplication());

            // Si no hay conexi�n a Internet
            Util.mostrarMensaje(
                    this,
                    getResources().getString(
                            R.string.mod_global__sin_conexion_a_internet),
                    getResources()
                            .getString(
                                    R.string.mod_global__no_dispones_de_conexion_a_internet));

            panelCargando.setVisibility(View.GONE);
        }
    }

    // <-------------------->_GPS_implementation_methods_<-------------------->

    @Override
    public void onLocationChanged(Location location) {
        if (this.gps.getLastLocation() != null) {

            // Parar el gps si ya tenemos una coordenada
            this.gps.stopLocating();

            // Guardar la coordenada en las preferencias
            SharedPreferences.Editor editor = app.preferencias.edit();
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LATITUDE,
                    (float) this.gps.getLastLocation().getLatitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LONGITUDE,
                    (float) this.gps.getLastLocation().getLongitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_ALTITUDE,
                    (float) this.gps.getLastLocation().getAltitude());
            editor.commit();

            // Recargar los datos
            this.reloadData();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    // <-------------------->_PoisInterface_implementation_methods_<-------------------->

    @Override
    public void producidoErrorAlCargarPoi(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoPoi(Poi poi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoListaPois(ArrayList<Poi> pois) {
        // TODO Auto-generated method stub

        if (pois != null) {
            this.arrayEspacios = pois;
            Cache.arrayPois = pois;

            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }

            Log.d("Linea ejecutada: ", " Linea 734.");

            /*
            if (idFiltroEspecie != -1) {
                ArrayList<Poi> temp = new ArrayList<Poi>();

                Log.d("<-------------------> ", " <-------------------> <------------------->");
                Log.d("<-------------------> ", " <-------------------> <------------------->");

                for (int i = 0; i < app.getEspeciesListaEspacios().length; i++) {
                    Log.d("Espacio " + i + " a buscar: ", String.valueOf(app.getEspeciesListaEspacios()[i]));
                }

                Log.d("<-------------------> ", " <-------------------> <------------------->");
                Log.d("<-------------------> ", " <-------------------> <------------------->");

                for (Poi poi : arrayEspacios) {
                    if (contains(app.getEspeciesListaEspacios(), Integer.parseInt(poi.getNid()))) {
                        temp.add(poi);
                    }
                }

                arrayEspacios = temp;

                app.setPoisOfEspecie(temp);
                Intent intent = new Intent(GuiaNaturalezaEspacios.this, RoutesListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("Filtrar por especie", "1");
                intent.putExtras(bundle);

                startActivity(intent);
            }
            */

            PoisAdapter adapter = new PoisAdapter(this, arrayEspacios, this.gps);
            listaPois.setAdapter(adapter);
        }
        panelCargando.setVisibility(View.GONE);
    }

    public boolean contains(final int[] array, final int key) {
        Log.d("Exito", String.valueOf(key));
        boolean result=false;

        for (int element:array){
            if(key==element)
                result=true;
        }

        return result;
    }


    @Override
    public void producidoErrorAlCargarListaPois(String error) {
        Log.d("Milog", "producidoErrorAlCargarListaPois: " + error);
        panelCargando.setVisibility(View.GONE);
    }

    // <-------------------->_PoisModeOfflineInterface_implementation_methods_<-------------------->

    @Override
    public void producidoErrorAlCargarPoiOffline(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoListaPoisOffline(ArrayList<Poi> pois) {
        // TODO Auto-generated method stub

        if (pois != null) {
            this.arrayEspacios = pois;
            Cache.arrayPois = pois;

            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }

            Log.d("Linea ejecutada: ", " Linea 787.");

            /*
            if (idFiltroEspecie != -1) {
                ArrayList<Poi> temp = new ArrayList<Poi>();
                int j = 0;

                Log.d("<-------------------> ", " <-------------------> <------------------->");
                Log.d("<-------------------> ", " <-------------------> <------------------->");

                for (int i = 0; i < app.getEspeciesListaEspacios().length; i++) {
                    Log.d("Espacio " + i + " a buscar: ", String.valueOf(app.getEspeciesListaEspacios()[i]));
                }

                Log.d("<-------------------> ", " <-------------------> <------------------->");
                Log.d("<-------------------> ", " <-------------------> <------------------->");

                for (Poi poi : arrayEspacios) {
                    if (contains(app.getEspeciesListaEspacios(), Integer.parseInt(poi.getNid()))) {
                        temp.add(poi);
                        Log.d("Espacio aceptado: ", "Vuelta: " + poi.getNid()+" -/- "+Integer.parseInt(poi.getNid()));

                    } else {
                        Log.d("Espacio rechazado: ", "Vuelta: " + poi.getNid()+" -/- "+Integer.parseInt(poi.getNid()));
                    }
                }

                arrayEspacios = temp;

                app.setPoisOfEspecie(temp);
                Intent intent = new Intent(GuiaNaturalezaEspacios.this, RoutesListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("Filtrar por especie", "1");
                intent.putExtras(bundle);

                startActivity(intent);
            }
            */

            PoisAdapter adapter = new PoisAdapter(this, arrayEspacios, this.gps);
            listaPois.setAdapter(adapter);
        }
        panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void producidoErrorAlCargarListaPoisOffline(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoPoiOffline(Poi poi) {
        // TODO Auto-generated method stub

    }

    // <-------------------->_END_OF_FILE_<-------------------->
}
