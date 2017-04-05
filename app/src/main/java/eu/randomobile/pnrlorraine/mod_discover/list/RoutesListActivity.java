package eu.randomobile.pnrlorraine.mod_discover.list;


import android.app.Activity;
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
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.filter.combos.SimpleComboRouteCategories.ComboSimpleCategoriasRutasInterface;
import eu.randomobile.pnrlorraine.mod_discover.filter.combos.SimpleComboRouteDifficulties.ComboSimpleDificultadesRutasInterface;
import eu.randomobile.pnrlorraine.mod_discover.map.RoutesGeneralMapActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Route.RoutesInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute.RoutesModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_search.RouteSearch;
import eu.randomobile.pnrlorraine.mod_search.RouteSearchActivity;

public class RoutesListActivity extends Activity implements RoutesInterface, RoutesModeOfflineInterface, ComboSimpleCategoriasRutasInterface, ComboSimpleDificultadesRutasInterface, LocationListener {
    private MainApp app;
    private ImageMap mImageMap = null;
    private ListView listaRoutes;

    // Array con los elementos que contendra
    private ArrayList<Route> arrayRoutes = null;
    // Array con las rutas filtradas
    private ArrayList<Route> arrayFilteredRoutes = null;
    // Adaptador para la lista de items
    private ListRoutesAdapter routeAdaptador;

    private RelativeLayout panelCargando;
    private GPS gps;
    private Context ctxList;

    private int filtroPois = 0;

    // Coordenadas GPS de Fuerteventura
    double lon = 28.1958;
    double lat = -14.2789;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_discover__layout_lista_routes_2);

        // Comprobamos si hay parametros en el Bundle.
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Si hay parametros, miramos si corresponden al "Filtro de especie".
            if (bundle.get("Filtrar por especie") != null) {
                // Si es asi y este no está vacio, lo guardamos.
                filtroPois = Integer.parseInt(bundle.get("Filtrar por especie").toString());

                if (filtroPois != 0) {
                    Log.d("Filtrar Rutas: ", " SI");
                } else {
                    Log.d("Filtrar Rutas: ", " NO");
                }
            }
        }

        //Context
        ctxList = getApplicationContext();
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

        arrayRoutes = app.getRoutesList();

//        Log.d("JmLOg","Array ROutas ::::::: !!!!! "+arrayRoutes.size());

        mImageMap = (ImageMap) findViewById(R.id.map_routeList);
        mImageMap.setAttributes(true, false, (float) 1.0, "lista_rutas");
        mImageMap.setImageResource(R.drawable.lista_rutas);

        capturarControles();
        escucharEventos();
        inicializarForm();
        recargarForm();

        app.setRoutesList(arrayRoutes);
        panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }

    private void capturarControles() {
        listaRoutes = (ListView) findViewById(R.id.listaRoutes);
        panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
    }

    private void cargaActivityMaps() {
        Intent intent = new Intent(RoutesListActivity.this, RoutesGeneralMapActivity.class);
        intent.putExtra(RoutesGeneralMapActivity.PARAM_KEY_MOSTRAR, RoutesGeneralMapActivity.PARAM_MAPA_GENERAL_MOSTRAR_RUTAS);
        startActivity(intent);
    }

    private void cargaActivityHome() {
        Intent intent = new Intent(RoutesListActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargaActivitySearch() {
        Intent intent = new Intent(RoutesListActivity.this, RouteSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    private void cargaActivityOptions() {
        Intent intent = new Intent(RoutesListActivity.this, OptionsActivity.class);
        startActivity(intent);
    }

    private void filterRoutes() {
        for (int i = 0; i < app.getRoutesList().size(); i++) {
            if (RouteSearch.checkCriteria(app.getRoutesList().get(i), this))
                arrayFilteredRoutes.add(app.getRoutesList().get(i));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String name = data.getStringExtra("name");

        if (arrayFilteredRoutes == null)
            arrayFilteredRoutes = new ArrayList<Route>();

        arrayFilteredRoutes.clear();
        listaRoutes.invalidateViews();

        filterRoutes();

        ArrayList<Route> arrayFilteredRoutes_2 = new ArrayList<Route>();
        arrayFilteredRoutes_2 = applyPoisFilter(arrayFilteredRoutes);

        routeAdaptador = new ListRoutesAdapter(this, arrayFilteredRoutes_2);
        listaRoutes.setAdapter(routeAdaptador);
    }

    private void escucharEventos() {
        // add a click handler to react when areas are tapped
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                // when the area is tapped, show the name in a
                // text bubble
                mImageMap.showBubble(id);

                switch (mImageMap.getAreaAttribute(id, "name")) {
                    case "MAP":
                        cargaActivityMaps();
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

        listaRoutes.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Route routePulsado = null;

                if (arrayFilteredRoutes != null)
                    routePulsado = arrayFilteredRoutes.get(index);

                else
                    routePulsado = arrayRoutes.get(index);

                app.setRoutesList(arrayRoutes);

                Intent intent = new Intent(RoutesListActivity.this, RouteDetailActivity.class);

                intent.putExtra(RouteDetailActivity.PARAM_KEY_NID, routePulsado.getNid());
                intent.putExtra(RouteDetailActivity.PARAM_KEY_DISTANCE, routePulsado.getDistanceMeters());
                intent.putExtra(RouteDetailActivity.PARAM_KEY_CATEGORY_ROUTE, routePulsado.getCategory().getName());
                intent.putExtra(RouteDetailActivity.PARAM_KEY_TITLE_ROUTE, routePulsado.getTitle());
                intent.putExtra(RouteDetailActivity.PARAM_KEY_MAP_URL, routePulsado.getUrlMap());
                intent.putExtra(RouteDetailActivity.PARAM_KEY_COLOR_ROUTE, Integer.toString(routePulsado.getColorForMap(ctxList)));

                BitmapManager.INSTANCE.cache.remove(routePulsado.getMainImage());

                startActivity(intent);
            }
        });
    }

    private void inicializarForm() {
        panelCargando.setVisibility(View.GONE);
        // Inicializar el locationManager
        this.gps = new GPS(this, this);
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

        ArrayList<Route> arrayRoutes_2 = new ArrayList<Route>();
        arrayRoutes_2 = applyPoisFilter(arrayRoutes);

        routeAdaptador = new ListRoutesAdapter(this, arrayRoutes_2);
        listaRoutes.setAdapter(routeAdaptador);
    }

    private void recargarDatos() {
        /*
        if (DataConection.hayConexion(this)) {
            // Si hay conexi�n, recargar los datos
            panelCargando.setVisibility(View.VISIBLE);

            Route.routesInterface = this;

            Route.cargarListaRutasOrdenadosDistancia(getApplication(), // Aplicacion
                    gps.getLastLocation().getLatitude(), // Latitud
                    gps.getLastLocation().getLongitude(), // Longitud
                    0, // Radio en Kms
                    0, // N�mero de elementos por p�gina
                    0, // P�gina
                    null, // Tid de la categor�a que queremos filtrar
                    null, // Dificultad
                    null // Texto a buscar
            );
        } else {

        }
        */
    }

    public class ListRoutesAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<Route> listaItems;

        public class ViewHolder {
            RelativeLayout layoutFondo;
            ImageView imgView;
            ImageView imgViewCategory;
            TextView lblTitulo;
            TextView lblDetalle;
            //			LinearLayout panelPequenio;
            TextView lblDistancia;
            TextView lblDistanciaNum;
            TextView lblDuracion;
            TextView lblValDuracion;
            TextView lblValoracion;
            ImageView imgViewAccessoryArrow;
            ImageView imgViewDificultad;
            ImageView imgViewValoracion;
            ImageView imgViewFrame;
            int index;
        }

        public ListRoutesAdapter(Context _ctx, ArrayList<Route> _items) {
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

                convertView = mInflater.inflate(R.layout.mod_discover__layout_item_lista_routes, null);

                holder.layoutFondo = (RelativeLayout) convertView.findViewById(R.id.layoutFondo);
                holder.imgView = (ImageView) convertView.findViewById(R.id.imgView);
                holder.imgViewCategory = (ImageView) convertView.findViewById(R.id.imgViewCategory);
                holder.lblTitulo = (TextView) convertView.findViewById(R.id.lblTitulo);
                holder.lblDetalle = (TextView) convertView.findViewById(R.id.lblDetalle);
//				holder.panelPequenio = (LinearLayout) convertView.findViewById(R.id.panelPequenio);
                holder.lblDistancia = (TextView) convertView.findViewById(R.id.lblDistancia);
                holder.lblDistanciaNum = (TextView) convertView.findViewById(R.id.lblValDistancia);
                holder.lblDuracion = (TextView) convertView.findViewById(R.id.lblDuracion);
                holder.lblValDuracion = (TextView) convertView.findViewById(R.id.lblValDuracion);
                holder.imgViewDificultad = (ImageView) convertView.findViewById(R.id.imgDificultad);
                holder.imgViewFrame = (ImageView) convertView.findViewById(R.id.imgViewFrame);
                holder.lblValoracion = (TextView) convertView.findViewById(R.id.lblValoracion);
                holder.imgViewAccessoryArrow = (ImageView) convertView.findViewById(R.id.accesory);
                holder.imgViewValoracion = (ImageView) convertView.findViewById(R.id.imgViewValoracion);

                // Poner fuentes
                Typeface tfBentonMedium = Util.fontBenton_Medium(ctx);
                Typeface tfBentonBoo = Util.fontBenton_Boo(ctx);
                Typeface tfBentonBold = Util.fontBenton_Bold(ctx);
                Typeface tfScalaBold = Util.fontScala_Bold(ctx);
                /*
                holder.lblTitulo.setTypeface(tfScalaBold);
				holder.lblDetalle.setTypeface(tfBentonBoo);
				holder.lblDistancia.setTypeface(tfBentonBoo);
				holder.lblDistanciaNum.setTypeface(tfScalaBold);
				holder.lblDuracion.setTypeface(tfBentonBoo);
				holder.lblValDuracion.setTypeface(tfScalaBold);
				holder.lblValoracion.setTypeface(tfBentonBoo);
				*/

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Recoger el item
            Route item = listaItems.get(position);

            // T�tulo
            holder.lblTitulo.setText(item.getTitle());

            // Body
            if (item.getBody() != null && !item.getBody().equals("") && !item.getBody().equals("null")) {
                holder.lblDetalle.setText(Html.fromHtml(item.getBody()).toString().trim(), TextView.BufferType.SPANNABLE);
            } else {
                String strVacio = ctx.getResources().getString(
                        R.string.mod_global__sin_datos);
                holder.lblDetalle.setText(strVacio);
            }

            // Distancia
//			if (item.getDistanceMeters() < 1000) {
//				int roundedDistMeters = (int) item.getDistanceMeters();
//				holder.lblDistanciaNum.setText(roundedDistMeters + " m.");
//			} else {
//				int roundedDistKms = (int) (item.getDistanceMeters() / 1000);
//				holder.lblDistanciaNum.setText(roundedDistKms + " Km.");
//			}
            holder.lblDistanciaNum.setText(Util.formatDistanciaRoute(item.getRouteLengthMeters()));
            holder.lblValDuracion.setText(Util.formatDuracion(item.getEstimatedTime()));
            // Imagen
            if (item.getMainImage() != null) {
                BitmapManager.INSTANCE.loadBitmap(item.getMainImage(),
                        holder.imgView, 80, 60);
            } else {
                holder.imgView.setImageResource(R.drawable.no_picture_2);
            }

            // Poner la imagen de dificultad
            String dificultad = item.getDifficulty_tid();
            //Tr�s Facile
            if (dificultad.equals("18"))
                holder.imgViewDificultad.setBackgroundResource(R.drawable.marcador_facil);
                //Facile
            else if (dificultad.equals("16"))
                holder.imgViewDificultad.setBackgroundResource(R.drawable.marcador_media);
                //Moyen
            else if (dificultad.equals("17"))
                holder.imgViewDificultad.setBackgroundResource(R.drawable.marcador_dificil);
                //Difficile
            else if (dificultad.equals("22"))
                holder.imgViewDificultad.setBackgroundResource(R.drawable.marcador_muydificil);

            // Poner la imagen de categoria
            if (item.getCategory().getName().equals("GR")) {
                holder.imgViewCategory.setBackgroundResource(R.drawable.categoria_gr);
                //holder.imgViewFrame.setBackgroundResource(R.drawable.frame_gr);
            } else if (item.getCategory().getName().equals("PR")){
                holder.imgViewCategory.setBackgroundResource(R.drawable.categoria_pr);
                //holder.imgViewFrame.setBackgroundResource(R.drawable.frame_pr);
            }

            // Poner la flechita de la derecha en funci�n de si la celda es par
            // o no
            if (position % 2 == 0) {
                // Poner flecha 1
                holder.imgViewFrame
                        .setBackgroundResource(R.drawable.frame_pr);
            } else {
                // Poner flecha 2
                holder.imgViewFrame
                        .setBackgroundResource(R.drawable.frame_gr);
            }

            // Poner la valoraci�n
            try {
                String valString = ctx.getResources().getString(
                        R.string.mod_discover__nota);
                holder.lblValoracion.setText(valString + " (" + String.valueOf(item.getVote().getNumVotes()) + " " + ctxList.getResources().getString(R.string.votos) + ")");

                if (item.getVote() != null) {
                    if (item.getVote().getValue() <= 10) {
                        // Si es menor o igual a 0
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
                    } else if (item.getVote().getValue() > 10 && item.getVote().getValue() < 30) {
                        // Si est� entre 1 y 24
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_1_estrellas);
                    } else if (item.getVote().getValue() >= 30 && item.getVote().getValue() < 50) {
                        // Si est� entre 25 y 49
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_2_estrellas);
                    } else if (item.getVote().getValue() >= 50 && item.getVote().getValue() < 70) {
                        // Si est� entre 50 y 74
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_3_estrellas);
                    } else if (item.getVote().getValue() >= 70 && item.getVote().getValue() <= 90) {
                        // Si est� entre 75 y 90
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_4_estrellas);
                    } else {
                        holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_5_estrellas);
                    }
                } else {
                    holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
                }
            } catch (Exception e) {
                holder.imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
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
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LATITUDE,
                    (float) this.gps.getLastLocation().getLatitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LONGITUDE,
                    (float) this.gps.getLastLocation().getLongitude());
            editor.putFloat(app.FILTER_KEY_LAST_LOCATION_ALTITUDE,
                    (float) this.gps.getLastLocation().getAltitude());
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
    public void seCargoListaRoutes(ArrayList<Route> routes) {
        Log.d("<--PRUEBA-->", " <----------------------seCargoListaRoutes---------------------->");

        if (routes != null) {
            this.arrayRoutes = applyPoisFilter(routes);
            routeAdaptador = new ListRoutesAdapter(this, arrayRoutes);
            listaRoutes.setAdapter(routeAdaptador);
        }
        panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaRoutes");
    }

    @Override
    public void producidoErrorAlCargarListaRoutes(String error) {
        Log.d("Milog", "producidoErrorAlCargarListaRoutes: " + error);
        panelCargando.setVisibility(View.GONE);
    }


    public void seCerroComboDificultadesRutas(String tidSeleccionado, String nombreSeleccionado) {
        Log.d("Milog", "Se ha cerrado combo de dificultades de rutas. Seleccionado: "
                + tidSeleccionado + " Nombre dif: " + nombreSeleccionado);
        // Guardar el filtro de cateogor�a
        SharedPreferences.Editor editor = app.preferencias.edit();
        editor.putString(app.FILTER_KEY_ROUTE_DIFFICULTY_TID, tidSeleccionado);
        editor.commit();
        // Lo guardamos tambi�n a nivel de clase
//		difficultyTid = tidSeleccionado;
//
//		difficultyName = nombreSeleccionado;
//		this.btnDificultades.setText(difficultyName);
    }

    public void seCerroComboCategoriasRutas(String tidSeleccionado, String nombreSeleccionado) {
        Log.d("Milog", "Se ha cerrado combo de categorias de rutas. Seleccionado: "
                + tidSeleccionado + " Nombre cat: " + nombreSeleccionado);
        // Guardar el filtro de cateogor�a
        SharedPreferences.Editor editor = app.preferencias.edit();
        editor.putString(app.FILTER_KEY_ROUTE_CATEGORY_TID, tidSeleccionado);
        editor.commit();
        // Lo guardamos tambi�n a nivel de clase
//		categoryTid = tidSeleccionado;
//
//		categoryName = nombreSeleccionado;
//		this.btnCategorias.setText(categoryName);
    }


    @Override
    public void seCargoRoute(Route route) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarRoute(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seCargoListaRoutesOffline(ArrayList<Route> routes) {
        Log.d("<--PRUEBA-->", " <----------------------seCargoListaRoutesOffline---------------------->");

        if (routes != null) {
            this.arrayRoutes = applyPoisFilter(routes);
            routeAdaptador = new ListRoutesAdapter(this, arrayRoutes);
            listaRoutes.setAdapter(routeAdaptador);
        }
        panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaRoutesOffline");

    }

    @Override
    public void producidoErrorAlCargarListaRoutesOffline(String error) {
        // TODO Auto-generated method stub
        Log.d("Milog", "producidoErrorAlCargarListaRoutes: " + error);
        panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void seCargoRouteOffline(Route item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarRouteOffline(String error) {
        // TODO Auto-generated method stub

    }

    private ArrayList<Route> applyPoisFilter(ArrayList<Route> routes) {
        ArrayList<Route> routesFiltred = new ArrayList<Route>();
        int[] poisFilter = app.getPoisOfEspecie();

        Log.d("####################", " ##################################################");
        Log.d("FiltroPois", " " + String.valueOf(filtroPois));
        Log.d("####################", " ##################################################");


        if (filtroPois == 1) {
            Log.d("####################", " Entrada en if() detectada");

            ArrayList<String> routesAded = new ArrayList<>();

            // Recorremos el listado de rutas
            for (int z = 0; z < routes.size(); z++) {

                // Miramos si, cada una de las rutas, tiene POIS.
                if (routes.get(z).getPois() != null) {
                    Log.d("####################", " Ruta con POIs");

                    if (routes.get(z).getPois().size() > 0) {
                        Log.d("####################", " Ruta con POIs mayor a 0 | Size = " + routes.get(z).getPois().size());

                        for(int l = 0; l < routes.get(z).getPois().size(); l++) {
                            Log.d("####################", " " +  routes.get(z).getPois().get(l).getNid());
                        }

                        // Si tiene pois, los recorremos.
                        for (int i = 0; i < routes.get(z).getPois().size(); i++) {
                            Log.d("######## Ruta: " + routes.get(z).getNid(), " POI Ruta: " + routes.get(z).getPois().get(i).getNid());

                            // Por cada POI de la ruta, comparamos su NID con el de cada uno de los POIS en los que aparece una determinada especie.
                            for (int j = 0; j < poisFilter.length; j++) {
                                Log.d("######## Ruta: " + routes.get(z).getNid(), " POI Ruta: " + routes.get(z).getPois().get(i).getNid() + ", POI Especie: " + poisFilter[j]);

                                // Si ambos NID coinciden, es que la especie aparece en la ruta.
                                if (routes.get(z).getPois().get(i).getNid() == poisFilter[j]) {
                                    Log.d("######## Ruta filtrada:", " " + routes.get(z).getTitle());

                                    routesFiltred.add(routes.get(z));
                                    j = poisFilter.length;
                                    i = routes.get(z).getPois().size();

                                } else {
                                    Log.d("########", " No hay coincidencia");
                                }
                            }
                        }
                    } else {
                        Log.d("####################", " Ruta sin POIs mayor a 0");
                    }

                } else {
                    Log.d("####################", " Ruta sin POIs");
                }

                Log.d("####################", "");
                Log.d("####################", "");
                Log.d("####################", "");
            }

            return routesFiltred;

        } else {
            return routes;
        }
    }
}
