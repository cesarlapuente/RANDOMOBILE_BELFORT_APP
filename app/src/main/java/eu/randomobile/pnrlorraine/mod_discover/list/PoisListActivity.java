package eu.randomobile.pnrlorraine.mod_discover.list;

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
import android.widget.AdapterView.OnItemClickListener;
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
import eu.randomobile.pnrlorraine.mod_discover.filter.combos.SimpleComboPOICategories;
import eu.randomobile.pnrlorraine.mod_discover.list.adapter.PoisAdapter;
import eu.randomobile.pnrlorraine.mod_discover.map.PoisGeneralMapActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi.PoisModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_search.PoisSearch;
import eu.randomobile.pnrlorraine.mod_search.PoisSearchActivity;

public class PoisListActivity extends Activity implements PoisInterface, PoisModeOfflineInterface,
        LocationListener {
    public static final String PARAM_KEY_LAST_NEAREST_POI_NID = "last_nearest_poi_nid";
    private MainApp app;

    private ImageMap mImageMap = null;

    private String categoryTid = null;
    private String categoryName = null;

    private ListView listaPois;

    // Array con los elementos que contendra
    private List<Poi> arrayPois = null;
    // Array con las pois filtrados por categoria
    private ArrayList<Poi> arrayFilteredPois = null;
    // Adaptador para la lista de items
    private ListPoisAdapter poiAdaptador;

    // Array con TODAS las categorias.
    private int filtroCategoriasPOIs[] = null; // = {25, 26, 27, 28, 30, 33, 36};

    private TextView txtTitulo;

    private RelativeLayout panelCargando;

    private GPS gps;

    private SimpleComboPOICategories comboCategoriasPOIS;

    private TextView noresults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_discover__layout_lista_pois);

        // Set GPS
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

        // Obtener la app
        this.app = (MainApp) getApplication();
        mImageMap = (ImageMap) findViewById(R.id.menuPoisList);
        mImageMap.setAttributes(true, false, (float) 1.0, "lista_pois");
        mImageMap.setImageResource(R.drawable.lista_pois);

        // Recoger par�metros del bundle
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.get("serchActive").equals("true")) {
                Log.d("onCreate sais:", " Filtro de POIs detectado. Aplicando filtro.");

                filtroCategoriasPOIs = app.getFiltroCategoriasPOIs();
            }
        }

        capturarControles();

        // Recoger filtros
        categoryTid = app.preferencias.getString(app.FILTER_KEY_POI_CATEGORY_TID, null);
        String txtBuscarSaved = app.preferencias.getString(app.FILTER_KEY_POI_TEXT, null);

        Log.d("Milog", "Texto de b�squeda guardado: '" + txtBuscarSaved + "'");
        // editTxtBuscar.setText(txtBuscarSaved);

        categoryName = this.getResources().getString(
                R.string.mod_discover__todas_las_categorias);

        escucharEventos();
        inicializarForm();
        recargarForm();

        panelCargando.setVisibility(View.GONE);
    }

    public void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void capturarControles() {
        listaPois = (ListView) findViewById(R.id.listaPois);
        txtTitulo = (TextView) findViewById(R.id.txtNombre);
        panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
        noresults = (TextView) findViewById(R.id.noresults);
    }

    private void filterPois() {
        Log.d("filterPois() sais:", " Entrada al metodo detectada.");

        if (arrayPois != null) {
            if (arrayFilteredPois == null) {
                arrayFilteredPois = new ArrayList<Poi>();

            } else {
                arrayFilteredPois.clear();
            }

            for (int i = 0; i < arrayPois.size(); i++) {
                if (PoisSearch.checkCriteria(arrayPois.get(i), this))
                    arrayFilteredPois.add(arrayPois.get(i));
            }
        }

        if (filtroCategoriasPOIs != null) {
            Log.d("filterPois() sais:", " filtroCategoriasPOIs != null");

            for (int i = 0; i< filtroCategoriasPOIs.length; i++){
                Log.d("Listado de categorias:", "Categoria de Poi: " + String.valueOf(filtroCategoriasPOIs[i]));
            }

            if (arrayFilteredPois == null) {
                arrayFilteredPois = new ArrayList<Poi>();
            } else {
                arrayFilteredPois.clear();
            }

            for (int i = 0; i < filtroCategoriasPOIs.length; i++) {
                int categoria = filtroCategoriasPOIs[i];

                for (int j = 0; j < arrayPois.size(); j++){
                    Log.d("", String.valueOf(arrayPois.get(j).getCategory().getTid()));

                     if(arrayPois.get(j).getCategory().getTid().equals(String.valueOf(categoria))){
                         arrayFilteredPois.add(arrayPois.get(j));
                         Log.d("Categoria itroducida: ", arrayPois.get(j).getTitle());
                    }
                }

            }


        } else {
            Log.d("filterPois() sais:", " filtroCategoriasPOIs == null");
            filtroCategoriasPOIs = new int[] {25, 36, 28, 30, 45, 20000, 30000, 48, 26, 47, 49, 50, 51, 27};
            filterPois();
        }
        if(arrayFilteredPois.size() == 0) {
            noresults.setVisibility(View.VISIBLE);
        } else{
            noresults.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String name = data.getStringExtra("name");

        if (arrayFilteredPois == null)
            arrayFilteredPois = new ArrayList<Poi>();

        arrayFilteredPois.clear();
        listaPois.invalidateViews();

        // filterPois();

        Cache.filteredPois = arrayFilteredPois;

        poiAdaptador = new ListPoisAdapter(this, arrayFilteredPois);
        listaPois.setAdapter(poiAdaptador);
    }

    private void escucharEventos() {
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                // when the area is tapped, show the name in a
                // text bubble
                mImageMap.showBubble(id);

                switch (mImageMap.getAreaAttribute(id, "name")) {
                    case "MAP":
                    case "RA":
                        cargaPoisGeneralMapActivity();
                        break;
                    case "HOME":
                        cargaActivityHome();
                        break;
                    case "RECHERCHER":
                        cargaActivitySearch();
                        break;
                    case "PLUS":
                        cargaActivityOptions();
                        break;
                    /*case "RA":
                        cargaPoisGeneralMapActivity();
                        break;*/
                    case "BACK":
                        finish();
                        break;
                    default:
                        mImageMap.showBubble(id);
                }
            }

            @Override
            public void onBubbleClicked(int id) {
                // react to info bubble for area being tapped
            }
        });

        listaPois.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Poi poiPulsado = null;

                if (arrayFilteredPois != null)
                    poiPulsado = arrayFilteredPois.get(index);

                else
                    poiPulsado = arrayPois.get(index);

                Intent intent = new Intent(PoisListActivity.this, PoiDetailActivity.class);

                intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, poiPulsado.getNid());
                intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE, poiPulsado.getDistanceMeters());

                int desnivel = 0;

                if (gps != null) {
                    if (gps.getLastLocation() != null) {
                        desnivel = (int) (poiPulsado.getCoordinates().getAltitude() - gps.getLastLocation().getAltitude());
                    }
                }

                intent.putExtra(PoiDetailActivity.PARAM_KEY_DESNIVEL, desnivel);
                intent.putExtra(PoiDetailActivity.PARAM_KEY_NUMBERVOTES, poiPulsado.getVote().getNumVotes());
                intent.putExtra(PoiDetailActivity.PARAM_KEY_VALORATION, poiPulsado.getVote().getValue());
                BitmapManager.INSTANCE.cache.remove(poiPulsado.getMainImage());

                startActivity(intent);
            }
        });
    }

    @SuppressLint("NewApi")
    private void inicializarForm() {
        // poner estilos (fuente, color, ...)
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.hide();
            }
        }

        panelCargando.setVisibility(View.GONE);

        // Inicializar el locationManager
        this.gps = new GPS(this, this);
    }

    private void cargaActivityHome() {
        Intent intent = new Intent(PoisListActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargaActivitySearch() {
        Intent intent = new Intent(PoisListActivity.this, PoisSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    private void cargaActivityOptions() {
        // Abrir la pantalla de opciones
        Intent intent = new Intent(PoisListActivity.this, OptionsActivity.class);
        startActivity(intent);
    }

    private void cargaPoisGeneralMapActivity() {
        Intent intent = new Intent(PoisListActivity.this, PoisGeneralMapActivity.class);
        intent.putExtra(PoisGeneralMapActivity.PARAM_KEY_MOSTRAR, PoisGeneralMapActivity.PARAM_MAPA_GENERAL_MOSTRAR_POIS);
        startActivity(intent);
    }

    private void recargarForm() {
        if (this.gps.getLastLocation() == null) {
            // Si la �ltima posici�n es nula, es que nunca ha localizado.
            // Arrancarlo
            panelCargando.setVisibility(View.VISIBLE);
            this.gps.startLocating();

        } else {
            // Si la �ltima posici�n existe, ya ha localizado alguna vez.
            // Recargar los datos
            panelCargando.setVisibility(View.VISIBLE);
            this.recargarDatos();
        }


        Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");

        arrayPois = app.getPoisList();

        filterPois();

        PoisAdapter adapter = new PoisAdapter(this, arrayFilteredPois, this.gps);
        listaPois.setAdapter(adapter);

        panelCargando.setVisibility(View.GONE);
    }

    private void recargarDatos() {
        /*
        Log.d("PoiListActivity sais:", "Entrada en recargarDatos() detectada.");

        if (Cache.filteredPois != null) {
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en primera condición detectada.");

            this.arrayFilteredPois = (ArrayList<Poi>) Cache.filteredPois;
            //Cache.arrayPois = Cache.filteredPois;

            // filterPois();

            PoisAdapter adapter = new PoisAdapter(this, arrayFilteredPois, this.gps);
            listaPois.setAdapter(adapter);

            if (arrayPois == null)
                arrayPois = Cache.arrayPois;

            panelCargando.setVisibility(View.GONE);

        } else if (DataConection.hayConexion(this)) {
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en segunda condición detectada.");

            arrayPois = app.getPoisList();
            panelCargando.setVisibility(View.GONE);

            // Si hay conexi�n, recargar los datos
            /*
            panelCargando.setVisibility(View.VISIBLE);
            Poi.poisInterface = this;
            Poi.cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
                    gps.getLastLocation().getLatitude(), // Latitud
                    gps.getLastLocation().getLongitude(), // Longitud
                    0, // Radio en Kms
                    0, // N�mero de elementos por p�gina
                    0, // P�gina
                    null, // Tid de la categor�a que queremos filtrar
                    null // Texto a buscar
            );
            *
        } else {
            Log.d("PoiListActivity sais:", "recargarDatos() sais:: Entrada en tercera condición detectada.");

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

        // Poner el texto al bot�n que hace de combo
        // this.btnCategorias.setText(this.categoryName);
        Log.d("PoiListActivity sais:", "Salida de recargarDatos() detectada.");
        */
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
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				/*Log.d("Adapter Especies",item.getImage());*/
				BitmapManager.INSTANCE.loadBitmap(item.getMainImage(),holder.imgView, 80, 60);
                //ImageLoader.getInstance().displayImage(item.getMainImage(), holder.imgView);
            } else {

                holder.imgView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.no_picture_2));
            }

            /*
            if (item.getMainImage() != null) {
                BitmapManager.INSTANCE.loadBitmap(item.getMainImage(), holder.imgView, 80, 60);

            } else {
                if (item.getCategory() != null && item.getCategory().getIcon() != null) {
                    String category;

                    category = item.getCategory().getName();

                    if (category.equals("Chambre d'h�tes")
                            || category.equals("H�tellerie")
                            || category.equals("H�bergement collectif")
                            || category.equals("H�tellerie de plein air")
                            || category.equals("Meubl�")
                            || category.equals("R�sidence")) {
                        holder.imgView.setBackgroundResource(R.drawable.icono_hotel);

                    } else if (category.equals("Mus�e")
                            || category.equals("Patrimoine Naturel")
                            || category.equals("Site et Monument")
                            || category.equals("Office de Tourisme")
                            || category.equals("Parc et Jardin")) {
                        holder.imgView.setBackgroundResource(R.drawable.icono_descubrir);

                    } else if (category.equals("Restauration"))
                        holder.imgView.setBackgroundResource(R.drawable.icono_restaurante);

                } else {
                    holder.imgView.setImageResource(R.drawable.ic_launcher);
                }
            }
            */

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

    public void onLocationChanged(Location location) {
        if (this.gps.getLastLocation() != null) {

            // Parar el gps si ya tenemos una coordenada
            this.gps.stopLocating();

            // Guardar la coordenada en las preferencias
            SharedPreferences.Editor editor = app.preferencias.edit();
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LATITUDE, (float) this.gps.getLastLocation().getLatitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LONGITUDE, (float) this.gps.getLastLocation().getLongitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_ALTITUDE, (float) this.gps.getLastLocation().getAltitude());
            editor.commit();

            // Recargar los datos
            this.recargarDatos();
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

    @Override
    public void seCargoListaPois(ArrayList<Poi> pois) {
        if (pois != null) {
            this.arrayPois = pois;
            Cache.arrayPois = pois;

            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }

            filterPois();

            PoisAdapter adapter = new PoisAdapter(this, arrayFilteredPois, this.gps);
            listaPois.setAdapter(adapter);
        }
        panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaPois");
    }

    @Override
    public void producidoErrorAlCargarListaPois(String error) {
        Log.d("Milog", "producidoErrorAlCargarListaPois: " + error);
        panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void seCargoPoi(Poi poi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarPoi(String error) {
        // TODO Auto-generated method stub

    }


    @Override
    public void producidoErrorAlCargarListaPoisOffline(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoPoiOffline(Poi poi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarPoiOffline(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoListaPoisOffline(ArrayList<Poi> pois) {
        // TODO Auto-generated method stub
        if (pois != null) {
            this.arrayPois = pois;
            Cache.arrayPois = pois;
            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }
            // poiAdaptador = new ListPoisAdapter(this, arrayPois);
            // listaPois.setAdapter(poiAdaptador);
            PoisAdapter adapter = new PoisAdapter(this, arrayPois, this.gps);
            listaPois.setAdapter(adapter);
        }
        panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaPois");

    }

}
