package eu.randomobile.pnrlorraine.mod_discover.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiDAO;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_search.PoisSearchActivity;


public class PoisGeneralMapActivity extends Activity implements
        ComboCapasMapaInterface {

    public static final String PARAM_KEY_MOSTRAR = "mapa_mostrar";

    public static final int PARAM_MAPA_GENERAL_MOSTRAR_POIS = 200;
    MainApp app;
    ImageMap mImageMap = null;
    MapView mapa;
    GraphicsOverlay capaGeometrias;
    Callout callout;
    Button btnSeleccionarCapaBase;
    RelativeLayout panelCargando;
    List<Poi> arrayPois = null;
    // Array con las pois filtrados por categoria
    ArrayList<Poi> arrayFilteredPois = null;
    PictureMarkerSymbol hotel;
    PictureMarkerSymbol descubrir;
    PictureMarkerSymbol restaurante;
    PictureMarkerSymbol info;
    PictureMarkerSymbol icono;
    PictureMarkerSymbol naturaleza;
    private PoiDAO poiDAO;
    private ProgressBar progressBar;

    private Envelope envelope;
    private Basemap basemap;
    private List<Integer> filtre;

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_discover__mapa_general_pois);
        progressBar = (ProgressBar) findViewById(R.id.ruedaCargandoMapa);

        this.app = (MainApp) getApplication();

        // Imagen de Cabecera con botones
        mImageMap = (ImageMap) findViewById(R.id.map_poisGeneral);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_pois");
        mImageMap.setImageResource(R.drawable.mapa_pois);

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
        naturaleza = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
                R.drawable.icono_naturaleza)));

        poiDAO = new PoiDAO(getApplicationContext());
        arrayPois = poiDAO.getAllPois();

        Intent intent = getIntent();

        filtre = intent.getIntegerArrayListExtra("filtre");
        if (filtre == null)
            filtre = Poi.getFiltreDefault();

        arrayPois = filterPois();

        // Capturar controles
        this.capturarControles();

        envelope = new Envelope(47.7000, 6.8000, 47.6, 6.9, SpatialReferences.getWgs84());

        basemap = Basemap.createImagery();

        mapa = (MapView) findViewById(R.id.mapa);
        ArcGISMap mapArgis = new ArcGISMap(basemap);
        mapa.setMap(mapArgis);
        mapa.setViewpointGeometryAsync(envelope, 60);


        // Escuchar eventos
        this.escucharEventos();
    }

    private List<Poi> filterPois() {
        List<Poi> list = new ArrayList<>();
        for (Poi poi : arrayPois) {
            if (filtre.contains(poi.getCat())) {
                list.add(poi);
            }
        }
        return list;
    }

    public void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
        // Activar GPS
        /*LocationDisplay ls = mapa.getLocationDisplay();
//		ls.setLocationListener(new MyLocationListener());
//		ls.setAutoPanMode(AutoPanMode.OFF);
        ls.startAsync();*/

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        //LocationDisplay ls = mapa.getLocationDisplay();
        //ls.setLocationListener(new MyLocationListener());

        /*if (ls.isStarted()) {
            ls.stop();
        }*/
    }

    /*public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d("Milog", "Cambio la configuracion");
    }*/

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
        intent.putExtra("liste", false);
        startActivityForResult(intent, 1);
        finish();
    }

    private void cargaActivityOptions() {
        // Abrir la pantalla de opciones
        Intent intent = new Intent(PoisGeneralMapActivity.this,
                OptionsActivity.class);
        startActivity(intent);
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


        mapa.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapa) {
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
                                List<IdentifyGraphicsOverlayResult> lr = graphicsIDS.get();
                                List<Graphic> listGraphic = new ArrayList<Graphic>();
                                Graphic gr = null;
                                if (!lr.isEmpty())
                                    listGraphic = lr.get(0).getGraphics();
                                if (!listGraphic.isEmpty())
                                    gr = listGraphic.get(0);

                                if (gr != null) {
                                    String nombre = (String) gr.getAttributes().get("nombre");
                                    String clase = (String) gr.getAttributes().get("clase");
                                    String nid = (String) gr.getAttributes().get("nid");
                                    String cat = (String) gr.getAttributes().get("cat");
                                    String distanceMeters = (String) gr.getAttributes().get("distanceMeters");

                                    callout = mapa.getCallout();
                                    // Establecer el estilo del callout
                                    callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.style_callout_mapa_global));
                                    callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, getApplicationContext()));
                                    // Establecer el contenido del callout
                                    View contenidoCallout = getViewForCallout(nombre,
                                            clase, nid, cat, distanceMeters);
                                    callout.setContent(contenidoCallout);
                                    callout.setLocation(mapa.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()))));
                                    callout.show();                            }

                            } else {
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
                return true;
            }
        });


        this.mapa.getMap().addLoadStatusChangedListener(new LoadStatusChangedListener() {
            @Override
            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
                if (LoadStatus.LOADED == loadStatusChangedEvent.getNewLoadStatus()) {

                    /*LocationDisplay ls = mapa.getLocationDisplay();
                    ls.addLocationChangedListener(new PoisGeneralMapActivity.MyLocationListener());
                    ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                    ls.startAsync();*/
                    representarGeometrias();
                }
            }
        });

        btnSeleccionarCapaBase.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ComboCapasMapa comboCapas = new ComboCapasMapa(
                        getApplication(), PoisGeneralMapActivity.this, basemap);
                comboCapas.comboCapasMapaInterface = PoisGeneralMapActivity.this;
                comboCapas.show();
            }
        });

    }


    public void seCerroComboCapas(Basemap basemap) {
        if (!this.basemap.equals(basemap)) {
            this.basemap = basemap;
            ArcGISMap mapArgis = new ArcGISMap(this.basemap);
            mapa.setMap(mapArgis);
            mapa.setViewpointGeometryAsync(envelope, 60);
        }
    }

    private void representarGeometrias() {

        GraphicsOverlay gr = new GraphicsOverlay();

        if (!arrayPois.isEmpty()) {
            double latmin = 180;
            double latmax = -180;
            double lonmin = 180;
            double lonmax = -180;

            for (Poi poi : arrayPois) {
                GeoPoint p = poi.getCoordinates();
                if (p.getLatitude() < latmin)
                    latmin = p.getLatitude();
                if (p.getLatitude() > latmax)
                    latmax = p.getLatitude();
                if (p.getLongitude() < lonmin)
                    lonmin = p.getLongitude();
                if (p.getLongitude() > lonmax)
                    lonmax = p.getLongitude();

                Point puntoProyectado = new Point(p.getLongitude(),
                        p.getLatitude(), SpatialReferences.getWgs84());

                PictureMarkerSymbol sym;
                String claseNombre = Poi.getCategoryName(poi.getCat());

                switch (poi.getCat()) {
                    case 26:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51: // Hébergements
                        sym = hotel;
                        break;
                    case 30: //Patrimoine naturel
                        sym = naturaleza;
                        break;
                    case 36:  //Monuments
                    case 28:
                        sym = descubrir;
                        break;
                    case 27: //Restauracion
                        sym = restaurante;
                        break;
                    case 25: //Offices de tourisme
                        sym = info;
                        break;
                    default:
                        sym = icono;
                        claseNombre = this.getResources().getString(R.string.punto_de_interes);
                }

                final HashMap<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("clase", claseNombre);
                attrs.put("nid", poi.getNid());
                attrs.put("nombre", poi.getTitle());

                gr.getGraphics().add(new Graphic(puntoProyectado, attrs, sym));


            }

            mapa.getGraphicsOverlays().add(gr);

            envelope = new Envelope(lonmax, latmin, lonmin, latmax, SpatialReferences.getWgs84());

            mapa.setViewpointGeometryAsync(envelope, 60);

            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private View getViewForCallout(final String nombre, String clase, final String nid, String cat, final String distanceMeters) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.mod_discover__layout_callout_mapa, null);
        final TextView lblNombre = (TextView) view
                .findViewById(R.id.lblNombrePunto);
        final TextView lblCategoria = (TextView) view
                .findViewById(R.id.lblCategoriaPunto);
        final Button btnCerrarDialogo = (Button) view
                .findViewById(R.id.btnVerFichaPunto);

        lblNombre.setText(nombre);
        lblCategoria.setText(clase);

        // Escuchar el evento del click del bot�n
        btnCerrarDialogo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PoisGeneralMapActivity.this,
                        PoiDetailActivity.class);
                intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
                //intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE, Double.valueOf(distanceMeters));
                startActivity(intent);
                callout.dismiss();
            }
        });

        return view;
    }

}