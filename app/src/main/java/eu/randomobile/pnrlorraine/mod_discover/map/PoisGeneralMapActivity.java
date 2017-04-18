package eu.randomobile.pnrlorraine.mod_discover.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
//import eu.randomobile.pnrlorraine.mod_discover.ra.MetaIORAActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi.PoisModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_search.PoisSearch;
import eu.randomobile.pnrlorraine.mod_search.PoisSearchActivity;

public class PoisGeneralMapActivity extends Activity implements
        ComboCapasMapaInterface, PoisInterface, PoisModeOfflineInterface {

    public static final String PARAM_KEY_MOSTRAR = "mapa_mostrar";

    public static final int PARAM_MAPA_GENERAL_MOSTRAR_POIS = 200;
    public static final int PARAM_MAPA_GENERAL_MOSTRAR_RUTAS = 201;

    private int paramMapaGeneralMostrar;

    MainApp app;
    ImageMap mImageMap = null;

    MapView mapa;
    GraphicsOverlay capaGeometrias;
    Callout callout;

    Button btnSeleccionarCapaBase;

    RelativeLayout panelCargando;

    ArrayList<Poi> arrayPois = null;
    // Array con las pois filtrados por categoria
    ArrayList<Poi> arrayFilteredPois = null;

    PictureMarkerSymbol hotel;
    PictureMarkerSymbol descubrir;
    PictureMarkerSymbol restaurante;
    PictureMarkerSymbol info;
    PictureMarkerSymbol icono;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_discover__mapa_general_pois);

        this.app = (MainApp) getApplication();

        // Imagen de Cabecera con botones
        mImageMap = (ImageMap) findViewById(R.id.map_poisGeneral);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_pois");
        mImageMap.setImageResource(R.drawable.mapa_pois);

        //icono = new PictureMarkerSymbol((BitmapDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.poi_icono, null));
        icono = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.poi_icono)));
        hotel = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.icono_hotel)));
        descubrir = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.icono_descubrir)));
        restaurante = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.icono_restaurante)));
        info = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.icono_info)));

        // Recuperar parametros
        Bundle b = getIntent().getExtras();
        if (b != null) {
            setParamMapaGeneralMostrar(b.getInt(PARAM_KEY_MOSTRAR));
        }

        // Capturar controles
        this.capturarControles();

        // Configurar formulario
        this.inicializarMapa();

        // Escuchar eventos
        this.escucharEventos();
    }

    public void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
        // Activar GPS
        LocationDisplay ls = mapa.getLocationDisplay();
//		ls.setLocationListener(new MyLocationListener());
//		ls.setAutoPanMode(AutoPanMode.OFF);
        ls.startAsync();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //mapa.getGraphicsOverlays().clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapa.getGraphicsOverlays().clear();
        hotel = null;
        restaurante = null;
        descubrir = null;
        icono = null;
        info = null;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        LocationDisplay ls = mapa.getLocationDisplay();
        //ls.setLocationListener(new MyLocationListener());

        if (ls.isStarted()) {
            ls.stop();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d("Milog", "Cambio la configuracion");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (callout != null && callout.isShowing()) {
                callout.dismiss();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void capturarControles() {
        mapa = (MapView) findViewById(R.id.mapa);
        btnSeleccionarCapaBase = (Button) findViewById(R.id.btnAbrirCapas);
        panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
    }

    private void cargaActivityHome() {
        Intent intent = new Intent(PoisGeneralMapActivity.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargaActivityPoisList() {
        Intent intent = new Intent(PoisGeneralMapActivity.this,
                PoisListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargaActivitySearch() {
        Intent intent = new Intent(PoisGeneralMapActivity.this,
                PoisSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

//    private void cargaRAActivity() {
////		Intent intent = new Intent(PoisGeneralMapActivity.this,
////				RAActivity.class);
////		intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, "RA");
////		intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
////		intent.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, JSONPOIParser.parseToJSONArray(app, arrayPois).toString() );
//        Intent intent = new Intent(PoisGeneralMapActivity.this,
//                MetaIORAActivity.class);
//        startActivity(intent);
//    }

    private void cargaActivityOptions() {
        // Abrir la pantalla de opciones
        Intent intent = new Intent(PoisGeneralMapActivity.this,
                OptionsActivity.class);
        startActivity(intent);
    }

    public void inicializarMapa() {

        ponerCapaBase();

        capaGeometrias = new GraphicsOverlay();
        mapa.getGraphicsOverlays().add(capaGeometrias);

        // Tipografias
        Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
        this.btnSeleccionarCapaBase.setTypeface(tfBubleGum);
    }

    public void escucharEventos() {



        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                // when the area is tapped, show the name in a
                // text bubble
                mImageMap.showBubble(id);
                switch (mImageMap.getAreaAttribute(id, "name")) {
                    case "LIST":
                        cargaActivityPoisList();
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
                    case "RA":
                        // cargaRAActivity();
                        cargaActivityPoisList();
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


        this.mapa.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapa){
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e) {
                if (mapa.getMap().getLoadStatus() == LoadStatus.NOT_LOADED) {
                    return false;
                }
                final android.graphics.Point point = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                final double tolerance = 8;
                final ListenableFuture<List<IdentifyGraphicsOverlayResult>> graphicsIDS = mapa.identifyGraphicsOverlaysAsync(point, tolerance, false);

                graphicsIDS.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (graphicsIDS != null && graphicsIDS.get().size() > 0) {
                                Log.d("Milog", "Hay graficos en la zona pulsada");
                                /*List lr = graphicsIDS.get();

                                Graphic gr = capaGeometrias.getGraphics().get(capaGeometrias.getGraphics().indexOf(lr.get(0)));*/
                                List<IdentifyGraphicsOverlayResult> lr = graphicsIDS.get();
                                List<Graphic> listGraphic = new ArrayList<Graphic>();
                                Graphic gr = null;
                                if (!lr.isEmpty())
                                    listGraphic = lr.get(0).getGraphics();
                                if (!listGraphic.isEmpty())
                                    gr = listGraphic.get(0);

                                if (gr != null) {
                                    String nombre = (String) gr.getAttributes().get(
                                            "nombre");
                                    String clase = (String) gr.getAttributes().get("clase");
                                    String nid = (String) gr.getAttributes().get("nid");
                                    String cat = (String) gr.getAttributes().get("cat");
                                    String distanceMeters = (String) gr.getAttributes().get("distanceMeters");

                                    callout = mapa.getCallout();
                                    // Establecer el estilo del callout
                                    callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.style_callout_mapa_global));
                                    callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
                                    // Establecer el contenido del callout
                                    View contenidoCallout = getViewForCallout(nombre,
                                            clase, nid, cat, distanceMeters);
                                    callout.setContent(contenidoCallout);
                                    callout.setLocation(mapa.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()))));
                                    callout.show();                            }

                            } else {
                                Log.d("Milog", "No hay graficos en la zona pulsada");
                                if (callout != null && callout.isShowing()) {
                                    callout.dismiss();
                                }
                            }
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });


        this.mapa.getMap().addLoadStatusChangedListener(new LoadStatusChangedListener() {
            @Override
            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
                if (LoadStatus.LOADED == loadStatusChangedEvent.getNewLoadStatus()) {

                    LocationDisplay ls = mapa.getLocationDisplay();
                    ls.addLocationChangedListener(new PoisGeneralMapActivity.MyLocationListener());
                    ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                    ls.startAsync();

                    representarGeometrias();
                }
            }
        });

        btnSeleccionarCapaBase.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ComboCapasMapa comboCapas = new ComboCapasMapa(
                        getApplication(), PoisGeneralMapActivity.this);
                comboCapas.comboCapasMapaInterface = PoisGeneralMapActivity.this;
                comboCapas.show();
            }
        });

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
        filterPois();
        Cache.filteredPois = arrayFilteredPois;
        capaGeometrias.getGraphics().clear();
        if (arrayFilteredPois.size() > 0)
            seCargoListaPois(arrayFilteredPois);
    }

    private void filterPois() {
        if (arrayPois != null)
            for (int i = 0; i < arrayPois.size(); i++) {
                if (PoisSearch.checkCriteria(arrayPois.get(i), this))
                    arrayFilteredPois.add(arrayPois.get(i));
            }
    }

    public void seCerroComboCapas() {
        Log.d("Milog", "Antes de poner capa base");
        ponerCapaBase();
        Log.d("Milog", "Despues de poner capa base");
    }

    private void representarGeometrias() {

        if (Cache.filteredPois != null) {
            this.arrayFilteredPois = (ArrayList<Poi>) Cache.filteredPois;
            //Cache.arrayPois = Cache.filteredPois;
            if (arrayPois == null)
                arrayPois = (ArrayList<Poi>) Cache.arrayPois;
            this.seCargoListaPois(this.arrayFilteredPois);
            panelCargando.setVisibility(View.GONE);
        } else if (DataConection.hayConexion(this)) {

            // Recoger los filtros
            double lat = (double) app.preferencias.getFloat(
                    app.FILTER_KEY_LAST_LOCATION_LATITUDE, 0);
            double lon = (double) app.preferencias.getFloat(
                    app.FILTER_KEY_LAST_LOCATION_LONGITUDE, 0);

            if (paramMapaGeneralMostrar == PARAM_MAPA_GENERAL_MOSTRAR_POIS) {
                String tidCat = app.preferencias.getString(
                        app.FILTER_KEY_POI_CATEGORY_TID, null);
                String txtBuscar = app.preferencias.getString(
                        app.FILTER_KEY_POI_TEXT, null);

                Poi.poisInterface = this;
                Poi.cargarListaPoisOrdenadosDistancia(app, lat, lon, 0, 0, 0,
                        tidCat, txtBuscar);
            }

        } else {
            OfflinePoi.poisInterface = this;
            OfflinePoi.cargaListaPoisOffline(getApplication());
            panelCargando.setVisibility(View.GONE);
            Util.mostrarMensaje(
                    this,
                    getResources().getString(
                            R.string.mod_global__sin_conexion_a_internet),
                    getResources()
                            .getString(
                                    R.string.mod_global__no_dispones_de_conexion_a_internet));
        }
    }

    @Override
    public void seCargoListaPois(ArrayList<Poi> pois) {

        if (arrayFilteredPois == null)
            arrayPois = pois;

        Log.d("Milog", "Se carg� lista pois");
        if (pois != null) {
            Log.d("Milog", "Lista pois no es nulo");

            for (int i = 0; i < pois.size(); i++) {
                Poi poi = pois.get(i);
                // Por cada poi obtener sus coordenadas y construir un objeto
                // Point de Arcgis
                GeoPoint gp = poi.getCoordinates();
                Point puntoProyectado = new Point(gp.getLongitude(), gp.getLatitude(), SpatialReferences.getWgs84());//(Point) GeometryEngine.project(new Point(gp.getLongitude(), gp.getLatitude()),
                //          SpatialReferences.getWgs84());
                ArrayList<Object> geometrias = new ArrayList<Object>();
                geometrias.add(puntoProyectado);

                String cat = "poi";
                if (poi.getCategory() != null) {
                    cat = poi.getCategory().getTid();
                }

                String icon = null;
                if (poi.getCategory() != null && poi.getCategory().getTid() != null) {
                    icon = poi.getCategory().getIcon();
                }

                dibujarGeometrias(geometrias, poi.getTitle(), poi.getClass().getName(),
                        poi.getNid(), cat, icon, Double.toString(poi.getDistanceMeters()));
            }

            centrarEnExtentCapa(capaGeometrias);
        }

        // Quitar el panel de cargando
        panelCargando.setVisibility(View.GONE);

    }

    @Override
    public void producidoErrorAlCargarListaPois(String error) {
        // Quitar el panel de cargando
        panelCargando.setVisibility(View.GONE);
    }


    private View getViewForCallout(final String nombre, String clase, final String nid, String cat, final String distanceMeters) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.mod_discover__layout_callout_mapa, null);
        Log.d("Milog", "1");
        final TextView lblNombre = (TextView) view
                .findViewById(R.id.lblNombrePunto);
        Log.d("Milog", "2");
        final TextView lblCategoria = (TextView) view
                .findViewById(R.id.lblCategoriaPunto);
        Log.d("Milog", "3");
        final Button btnCerrarDialogo = (Button) view
                .findViewById(R.id.btnVerFichaPunto);
        Log.d("Milog", "4");

        // Ponerle las propiedades necesarias
        if (clase.equals(Poi.class.getName())) {
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            lblCategoria.setText(cat);

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(PoisGeneralMapActivity.this,
                            PoiDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE, Double.valueOf(distanceMeters));
                    startActivity(intent);
                    callout.dismiss();
                }
            });
        } else if (clase.equals(Route.class.getName())) {
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            lblCategoria.setText("Route");

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(PoisGeneralMapActivity.this,
                            RouteDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
                    startActivity(intent);
                    callout.dismiss();
                }
            });
        }

        return view;
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void dibujarGeometrias(ArrayList<Object> geometrias, String paramNombre, String paramNombreClase, String paramNid, String paramCat, final String urlIcon, final String distanceMeters) {

        int polygonFillColor = Color.rgb(55, 132, 218);
        int polygonBorderColor = Color.rgb(27, 87, 187);
        //int pointColor = Color.rgb(206, 240, 5);
        if (geometrias != null) {
            for (int j = 0; j < geometrias.size(); j++) {
                Object geomObj = geometrias.get(j);
                Geometry geo = null;
                Symbol symb = null;

                final HashMap<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("clase", paramNombreClase);
                attrs.put("nid", paramNid);
                attrs.put("nombre", paramNombre);
                attrs.put("cat", paramCat);
                attrs.put("distanceMeters", distanceMeters);

                if (geomObj != null
                        && geomObj.getClass().getName()
                        .equals(Polygon.class.getName())) {
                    Polygon polygon = (Polygon) geomObj;
                    SimpleFillSymbol sym = new SimpleFillSymbol();
                    sym.setColor(polygonFillColor);
                    sym.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, polygonBorderColor, 8));
                    /*Graphic gr = new Graphic(polygon, attrs, sym);
                    capaGeometrias.getGraphics().add(gr);*/

                    geo = polygon;
                    symb = sym;

                } else if (geomObj != null
                        && geomObj.getClass().getName()
                        .equals(Point.class.getName())) {


                    final Point point = (Point) geomObj;

                    PictureMarkerSymbol sym = null;
                    switch (paramCat) {
                        case "26":
                        case "47":
                        case "48":
                        case "49":
                        case "50":
                        case "51":
                            sym = hotel;
                            //sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_hotel));
                            break;
                        case "36":
                        case "28":
                            sym = descubrir;
                            //sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_descubrir));
                            break;
                        case "27":
                            sym = restaurante;
                            //sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_restaurante));
                            break;
                        case "25":
                            sym = info;
                            //sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_info));
                            break;
                        default:
                            sym = icono;
                            //sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.poi_icono));
                    }
                    /*Graphic gr = new Graphic(point, attrs, sym );
                    capaGeometrias.getGraphics().add(gr);*/

                    geo = point;
                    symb = sym;

                    // Centrar en el extent de la capa
                    //centrarEnExtentCapa(capaGeometrias);

                } else if (geomObj != null
                        && geomObj.getClass().isInstance(Polyline.class)) {
                    Polyline polyline = (Polyline) geomObj;

                    int color = Color.BLUE;

                    /*Graphic gr = new Graphic(polyline, attrs, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, 10));
                    capaGeometrias.getGraphics().add(gr);*/

                    geo = polyline;
                    symb = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, 10);

                }

                Graphic gr = null;

                if (geo != null)
                    gr = new Graphic(geo, attrs, symb);
                else
                    Log.e("thib", "geo null  ");
                try {
                    capaGeometrias.getGraphics().add(gr);

                } catch (Exception e) {

                    capaGeometrias.getGraphics().remove(gr);
                    Log.e("thib", "echec add " + gr.getAttributes().toString() + "\n"
                            + symb.equals(hotel) + "\n" + symb.equals(restaurante) + "\n"
                            + symb.equals(descubrir) + "\n" + symb.equals(icono) + "\n"
                            + symb.equals(info) + "\n" + geo.toJson() + "\n", e);
                }
                //capaGeometrias.getGraphics().add(gr);
            }
        }
    }

    private void centrarEnExtentCapa(GraphicsOverlay capa) {
        // Hacer zoom a la capa de geometrias
        Envelope env ;
        Envelope NewEnv = capa.getExtent();
        if (capa.getGraphics() != null)
            for (int i=0; i<capa.getGraphics().size(); i++) {
                Geometry geom = capa.getGraphics().get(i).getGeometry();
                env = geom.getExtent();
                //geom.queryEnvelope(env);
                NewEnv.createFromInternal(env.getInternal());
                //NewEnv.merge(env);
            }
        this.mapa.setViewpointGeometryAsync(NewEnv, 100);
        //this.mapa.setExtent(NewEnv, 100);
    }


    public void ponerCapaBase() {
        /* Codigo de prueba */
        if (!DataConection.hayConexion(this)) {
            String basemapurl = Util.getUrlGeneralBaseLayerOffline(app);
            ArcGISTiledLayer baseLayer;
            baseLayer = new ArcGISTiledLayer(basemapurl);
            mapa.getMap().getOperationalLayers().add(baseLayer);
            mapa.getMap().setMaxScale(1000);
            return;
        }
		/* Fin de codigo de prueba */
        CapaBase capaSeleccionada = app.capaBaseSeleccionada;
        Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
        Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

        Object capaBase = capaSeleccionada.getMapLayer();
        Log.d("Milog", "Object capaBase");

        // Correcci�n, para que no cambie la capa base cuando la seleccionada es
        // la misma que ya estaba (ahorra datos)
        mapa.setMap(new ArcGISMap(Basemap.Type.IMAGERY, 56.008993, -2.725301, 10));
        LayerList capas = mapa.getMap().getOperationalLayers();
        if (capas != null) {
            Log.d("Milog", "capas no es nulo");
            if (capas.size() > 0) {

                Log.d("Milog", "Hay alguna capa");
                Object capa0 = capas.get(0);
                Log.d("Milog", "Tenemos capa0");
                // si la capa base seleccionada es del mismo tipo que la capa 0
                if (capaBase.getClass().getName()
                        .equals(capa0.getClass().getName())) {
                    Log.d("Milog",
                            "La clase de la capa base es igual que la clase de la capa0");
                    /*if (capaBase.getClass() == BingMapsLayer.class) {
                        Log.d("Milog", "capaBase es de tipo BING");
                        BingMapsLayer capaBaseCasted = (BingMapsLayer) capaBase;
                        BingMapsLayer capa0Casted = (BingMapsLayer) capa0;

                        if (capaBaseCasted.getMapStyle().equals(
                                capa0Casted.getMapStyle())) {
                            return;
                        } else {
                            mapa.removeLayer(0);
                            Log.d("Milog",
                                    "PUNTO INTERMEDIO BING: el mapa tiene "
                                            + mapa.getLayers().length
                                            + " capas");
                        }
                    } else*/ if (capaBase.getClass() == ArcGISTiledLayer.class) {
                        Log.d("Milog", "capaBase es de tipo TiledMap");
                        ArcGISTiledLayer capaBaseCasted = (ArcGISTiledLayer) capaBase;
                        ArcGISTiledLayer capa0Casted = (ArcGISTiledLayer) capa0;
                        String strUrlCapaBaseCasted = capaBaseCasted.getUri()
                                .toString();
                        String strUrlCapa0Casted = capa0Casted.getUri()
                                .toString();
                        if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
                            return;
                        } else {
                            mapa.getMap().getOperationalLayers().remove(0);
                            Log.d("Milog",
                                    "PUNTO INTERMEDIO TILED: el mapa tiene "
                                            + mapa.getMap().getOperationalLayers().size()
                                            + " capas");
                        }
                    }
                    Log.d("Milog", "La capa 0 es de clase "
                            + capa0.getClass().getName());
                } else {// si la capa base seleccionada no es del mismo tipo que
                    // la capa 0

                    /*if (capaBase.getClass() == BingMapsLayer.class) {
                        mapa.removeLayer(0);
                    } else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
                        mapa.removeLayer(0);
                    }*/
                }
            }
            // btnAbrirCapas.setEnabled(true);
            if (capaBase.getClass() == ArcGISTiledLayer.class) {

                if (capas.size() > 0) {
                    mapa.getMap().getOperationalLayers().add(0, (ArcGISTiledLayer) capaBase);
                } else {
                    mapa.getMap().getOperationalLayers().add((ArcGISTiledLayer) capaBase);
                }

            } /*else if (capaBase.getClass() == BingMapsLayer.class) {

                if (capas.length > 0) {
                    mapa.addLayer((BingMapsLayer) capaBase, 0);
                } else {
                    mapa.addLayer((BingMapsLayer) capaBase);
                }

            } */else {
                // otro tipo de capa
            }

            app.capaBaseSeleccionada = capaSeleccionada;
            Log.d("Milog", "El mapa tiene " + mapa.getMap().getOperationalLayers().size()
                    + " capas");
        }
    }

    public int getParamMapaGeneralMostrar() {
        return paramMapaGeneralMostrar;
    }

    public void setParamMapaGeneralMostrar(int paramMapaGeneralMostrar) {
        this.paramMapaGeneralMostrar = paramMapaGeneralMostrar;
    }


    /**
     * Location listener propio
     *
     * @author
     */
    private class MyLocationListener implements LocationDisplay.LocationChangedListener {

        public MyLocationListener() {
            super();
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
            if (locationChangedEvent.getLocation() == null)
                return;
        }
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
            seCargoListaPois(pois);
            // poiAdaptador = new ListPoisAdapter(this, arrayPois);
            // listaPois.setAdapter(poiAdaptador);
        }
        panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaPois");

    }

}