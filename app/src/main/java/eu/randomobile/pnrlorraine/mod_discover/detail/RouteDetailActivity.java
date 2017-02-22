package eu.randomobile.pnrlorraine.mod_discover.detail;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.LocationDisplayManager.AutoPanMode;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.WKTUtil;
import eu.randomobile.pnrlorraine.mod_global.data_access.DownloadAndSaveTPK;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.download.DownloadUrl.OnTaskCompletedInterface;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Route.RoutesInterface;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_guia.GuiaNaturalezaEspecies;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_login.LoginActivity;
import eu.randomobile.pnrlorraine.mod_multi_viewers.imgs.GridImagesActivity;
import eu.randomobile.pnrlorraine.mod_multi_viewers.vids.ListVideosActivity;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_offline.Offline;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi.PoisModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute.RoutesModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_search.PoisSearch;
import eu.randomobile.pnrlorraine.mod_search.PoisSearchActivity;
import eu.randomobile.pnrlorraine.mod_share.Share;
import eu.randomobile.pnrlorraine.mod_vote.VoteActivity;

public class RouteDetailActivity extends Activity implements RoutesInterface, RoutesModeOfflineInterface,
        ComboCapasMapaInterface, PoisInterface, PoisModeOfflineInterface, OnTaskCompletedInterface {
    private MainApp app;
    private Route route;

    private ImageMap imageMap;

    private MapView map;
    private GraphicsLayer geometricLayer;
    private GraphicsLayer geometricPOIsLayer;
    private Callout callout;
    private Point firstPoint;

    private ImageButton btn_Layers;
    private ImageButton btn_Download_Map;
    private ImageButton btn_Rate;
    private ImageButton btn_Related;
    private ImageButton btn_Galery;
    private ImageButton btn_Vids;

    private TextView txt_route_title;
    private TextView txt_ramp;
    private TextView txt_duration;
    private TextView txt_distance;
    private TextView txt_note;
    private TextView txt_description_body;

    private ImageView img_difficulty;
    private ImageView img_star_1, img_star_2, img_star_3, img_star_4, img_star_5;

    private boolean route_tpk_downloaded = false;

    public static final String PARAM_KEY_NID = "nid";
    public static final String PARAM_KEY_DISTANCE = "distance";
    public static double lastLatitude = 0;
    public static double lastLongitude = 0;

    String paramNid;
    String paramTitleRoute;
    String paramCategoryRoute;
    String paramMapUrl;
    double paramDistanceMeters;
    private Dialog dialogPoi = null;


    RelativeLayout panelCargando;

    // RelativeLayout panelCargandoMapas;


    // ProgressBar pb;

    private String TAG;

    /* arrayPois y arrayFilteredPois para las busquedas
     * Funcionalidad: Ver Pois                  */
    // Array con los elementos que contendra
    ArrayList<Poi> arrayPois = null;
    // Array con las pois filtrados por categoria
    ArrayList<Poi> arrayFilteredPois = null;
    //
    ArrayList<ResourcePoi> resourcePois = null;

    public static final String PARAM_KEY_NID_MOSTRAR = "mapa_nid_mostrar";
    public static final String PARAM_KEY_TYPE_DRUPAL = "mapa_type_mostrar";
    public static final String PARAM_KEY_TITLE_ROUTE = "route_title";
    public static final String PARAM_KEY_CATEGORY_ROUTE = "route_category";
    public static final String PARAM_KEY_MAP_URL = "map_url";
    public static final String PARAM_KEY_COLOR_ROUTE = "route_color";

    String paramType;
    int paramColorRoute = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        this.app = (MainApp) getApplication();


        TAG = this.getLocalClassName();

        // Recoger parametros
        Bundle b = getIntent().getExtras();

        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);
            for (Route route : app.getRoutesList()) {
                if (route.getNid().equals(paramNid)) {
                    this.route = route;
                    Log.d("JmLog","Objet route : "+route.getTitle()+" "+route.getUrlMap()+" "+route.getImages());
            }
            }

            paramDistanceMeters = b.getDouble(PARAM_KEY_DISTANCE);
            paramTitleRoute = b.getString(PARAM_KEY_TITLE_ROUTE);
            paramCategoryRoute = b.getString(PARAM_KEY_CATEGORY_ROUTE);
            paramMapUrl = b.getString(PARAM_KEY_MAP_URL);
            paramColorRoute = Integer.valueOf(b.getString(PARAM_KEY_COLOR_ROUTE));
        }

        paramType = app.DRUPAL_TYPE_ROUTE;
        Log.d("JmLog","La route main est : "+route.getTitle()+" si elle a image :"+route.getMainImage()+" gallery : "+route.getImages());

        initializeComponents();
        inicializarMapa();
        inicializarForm();
        setData();
        chekcOfflineMapState();
    }

    private void initializeComponents() {
        // <----------------->_MENU_DECLARATIONS_<----------------->

        imageMap = (ImageMap) findViewById(R.id.map_routeDetail);
        imageMap.setAttributes(true, false, (float) 1.0, "lista_item_ruta");
        imageMap.setImageResource(R.drawable.ficha_ruta4);
        imageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                RouteDetailActivity.this.imageMap.showBubble(id);

                switch (RouteDetailActivity.this.imageMap.getAreaAttribute(id, "name")) {
                    case "HOME": {
                        Intent intent = new Intent(RouteDetailActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        break;
                    }
                    case "BACK": {
                        finish();
                        break;
                    }
                    case "ALLER": {
                        if ((route != null) && (route.getPois() != null)) {

                            String uri = "http://maps.google.com/maps?&daddr=" + String.valueOf(route.getPois().get(0).getLatitude()) + "," + String.valueOf(route.getPois().get(0).getLongitude());

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                            try {
                                startActivity(intent);

                            } catch (ActivityNotFoundException ex) {
                                try {
                                    Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                                    startActivity(unrestrictedIntent);

                                } catch (ActivityNotFoundException innerEx) {
                                    Toast.makeText(RouteDetailActivity.this, "ES NECESARIO UNA APLICACIÓ DE MAPAS PARA ESTA FUNCIÓN", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        break;
                    }
                    case "POINTS": {
                        boolean poisLayerIsEnabled = geometricPOIsLayer.isVisible();

                        geometricPOIsLayer.setVisible(poisLayerIsEnabled ? false : true);
                        break;
                    }
                    case "PLUS": {
                        Share.compartir(app, RouteDetailActivity.this, route);
                        break;
                    }
                    case "INFO": {
                        Toast.makeText(RouteDetailActivity.this, "OPTION DISABLED", Toast.LENGTH_LONG);
                        break;
                    }
                    default:
                        RouteDetailActivity.this.imageMap.showBubble(id);
                }

            }

            @Override
            public void onBubbleClicked(int id) {
                // react to info bubble for area being tapped
            }
        });

        // <----------------->_MAP_DECLARATIONS_<----------------->

        map = (MapView) findViewById(R.id.mapa);
        map.setOnSingleTapListener(new OnSingleTapListener() {

            private static final long serialVersionUID = 1L;

            public void onSingleTap(float x, float y) {
                if (!map.isLoaded()) {
                    return;
                }

                int[] graphicsIDS = geometricPOIsLayer.getGraphicIDs(x, y, 8);
                int[] graphicsPoisSearch = geometricPOIsLayer.getGraphicIDs(x, y, 8);

                if (graphicsIDS != null && graphicsIDS.length > 0) {
                    int targetId = graphicsIDS[0];

                    Graphic gr = geometricPOIsLayer.getGraphic(targetId);

                    if (gr != null) {
                        String nombre = (String) gr.getAttributes().get("nombre");
                        String clase = (String) gr.getAttributes().get("clase");
                        String nid = (String) gr.getAttributes().get("nid");
                        String descripcion = (String) gr.getAttributes().get("descripcion");
                        String cat = (String) gr.getAttributes().get("cat");

                        callout = map.getCallout();
                        callout.setStyle(R.xml.style_callout_mapa_global);
                        callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
                        View contenidoCallout = null;

                        if (nid != null) {
                            contenidoCallout = getViewForCalloutPoi(nombre, clase, nid, cat);
                        }

                        if (contenidoCallout != null) {
                            callout.setContent(contenidoCallout);
                            callout.show(map.toMapPoint(new Point(x, y)));
                        }
                    }

                } else if (graphicsPoisSearch != null && graphicsPoisSearch.length > 0) {
                    int targetId = graphicsPoisSearch[0];

                    Graphic gr = geometricPOIsLayer.getGraphic(targetId);

                    if (gr != null) {
                        String nombre = (String) gr.getAttributes().get("nombre");
                        String clase = (String) gr.getAttributes().get("clase");
                        String nid = (String) gr.getAttributes().get("nid");
                        String descripcion = (String) gr.getAttributes().get("descripcion");
                        String cat = (String) gr.getAttributes().get("cat");

                        callout = map.getCallout();
                        callout.setStyle(R.xml.style_callout_mapa_global);
                        callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
                        View contenidoCallout;

                        if (nid != null) {
                            contenidoCallout = getViewForCallout(nombre, clase, cat, nid);

                        } else {
                            contenidoCallout = getViewForPoiCallout(nombre, descripcion);
                        }

                        callout.setContent(contenidoCallout);
                        callout.show(map.toMapPoint(new Point(x, y)));
                    }

                } else {
                    if (callout != null && callout.isShowing()) {
                        callout.hide();
                    }
                }
            }
        });

        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            public void onStatusChanged(Object source, STATUS status) {
                if (STATUS.INITIALIZED == status && source == map) {
                    LocationDisplayManager ls = map.getLocationDisplayManager();
                    ls.setLocationListener(new MyLocationListener());
                    ls.setAutoPanMode(AutoPanMode.OFF);

                    ls.start();

                    representarGeometrias();
                }
            }

        });

        // <----------------->_BUTTONS_DECLARATIONS_<----------------->

        btn_Layers = (ImageButton) findViewById(R.id.btn_Layer);
        btn_Layers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ComboCapasMapa comboCapas = new ComboCapasMapa(getApplication(), RouteDetailActivity.this);
                comboCapas.comboCapasMapaInterface = RouteDetailActivity.this;

                comboCapas.show();
            }
        });
        btn_Download_Map = (ImageButton) findViewById(R.id.btn_download_map);
        btn_Download_Map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!route_tpk_downloaded) {
                    if (route.getUrlMap().contains(".tpk")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RouteDetailActivity.this);

                        builder.setTitle(R.string.alert_dialog_title_download);
                        builder.setMessage(R.string.alert_dialog_body_download);
                        builder.setPositiveButton(R.string.alert_dialog_button_accept, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    new DownloadAndSaveTPK(app, Integer.parseInt(route.getNid()), route.getUrlMap(), (paramNid + ".tpk"), RouteDetailActivity.this);

                                    route_tpk_downloaded = true;

                                    btn_Download_Map.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.boton_mapa));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton(R.string.alert_dialog_button_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RouteDetailActivity.this);

                        builder.setTitle(R.string.alert_dialog_title_download);
                        builder.setMessage(R.string.alert_dialog_body_no_map);
                        builder.setPositiveButton(R.string.alert_dialog_button_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RouteDetailActivity.this);

                    builder.setTitle(R.string.alert_dialog_title_map_downloaded);
                    builder.setMessage(R.string.alert_dialog_body_map_downloaded);
                    builder.setPositiveButton(R.string.alert_dialog_button_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletefflineMap();

                            route_tpk_downloaded = false;

                            AlertDialog.Builder builder = new AlertDialog.Builder(RouteDetailActivity.this);

                            builder.setTitle(R.string.alert_dialog_title_map_downloaded);
                            builder.setMessage(R.string.alert_dialog_body_map_deleted);
                            builder.setPositiveButton(R.string.alert_dialog_button_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });
                    builder.setNegativeButton(R.string.alert_dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            }
        });
        btn_Rate = (ImageButton) findViewById(R.id.btn_Rate);
        btn_Rate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.isLoggedIn(app)) {
                    Intent intent = new Intent(RouteDetailActivity.this, VoteActivity.class);
                    intent.putExtra(VoteActivity.PARAM_KEY_NID_ITEM_A_VALORAR, route.getNid());
                    intent.putExtra(VoteActivity.PARAM_KEY_TITLE_ITEM_A_VALORAR, route.getTitle());

                    startActivity(intent);

                } else {
                    User.askForloginHere(RouteDetailActivity.this);
                }
            }
        });
        btn_Related = (ImageButton) findViewById(R.id.btn_Related);
        btn_Related.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Especies relacionadas", Toast.LENGTH_SHORT).show();

                if (resourcePois != null){
                    for (ResourcePoi poi:resourcePois) {
                        Log.d("Resource POI:", poi.getTitle());
                    }
                } else {
                    Log.d("Resource POI:", " ----- NULL -----");
                }

                if (resourcePois != null) {
                    int espaciosRuta[] = new int[resourcePois.size()];

                    Intent intent = new Intent(RouteDetailActivity.this, GuiaNaturalezaEspecies.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("fromRouteDetail", "true");

                    for (int i = 0; i < resourcePois.size(); i++) {
                        espaciosRuta[i] = resourcePois.get(i).getNid();
                    }

                    app.setEspaciosRuta(espaciosRuta);
                    intent.putExtras(bundle);

                    startActivity(intent);

                } else {
                    Toast.makeText(RouteDetailActivity.this, "No se han encontrado espacios relacionados.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_Galery = (ImageButton) findViewById(R.id.btn_Galery);
        btn_Galery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteDetailActivity.this, GridImagesActivity.class);
                intent.putParcelableArrayListExtra(GridImagesActivity.PARAM_KEY_ARRAY_RECURSOS, route.getImages());

                startActivity(intent);
            }
        });
        btn_Vids = (ImageButton) findViewById(R.id.btn_Vids);
        btn_Vids.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteDetailActivity.this, ListVideosActivity.class);
                intent.putParcelableArrayListExtra(GridImagesActivity.PARAM_KEY_ARRAY_RECURSOS, route.getEnlaces());

                startActivity(intent);
            }
        });

        // <----------------->_TEXTVIEWS_DECLARATIONS_<----------------->

        txt_route_title = (TextView) findViewById(R.id.txt_routeTitle);
        txt_ramp = (TextView) findViewById(R.id.txt_ramp);
        txt_duration = (TextView) findViewById(R.id.txt_duration);
        txt_distance = (TextView) findViewById(R.id.txt_distance);
        txt_note = (TextView) findViewById(R.id.txt_note);
        txt_note = (TextView) findViewById(R.id.txt_note);
        txt_description_body = (TextView) findViewById(R.id.txt_description_body);

        // <----------------->_IMAGEVIEWS_DECLARATIONS_<----------------->

        img_difficulty = (ImageView) findViewById(R.id.img_difficulty);
        img_star_1 = (ImageView) findViewById(R.id.img_star_1);
        img_star_2 = (ImageView) findViewById(R.id.img_star_2);
        img_star_3 = (ImageView) findViewById(R.id.img_star_3);
        img_star_4 = (ImageView) findViewById(R.id.img_star_4);
        img_star_5 = (ImageView) findViewById(R.id.img_star_5);

    }

    private void setData() {
        txt_route_title.setText(route.getTitle());
        txt_ramp.setText(Util.formatDesnivel(route.getSlope()));
        Log.d("Desnivel:", String.valueOf(route.getSlope()));
        txt_duration.setText(Util.formatDuracion(route.getEstimatedTime()));
        txt_distance.setText(Util.formatDistanciaRoute(route.getRouteLengthMeters()));
        try {
            txt_note.setText("NOTA: (" + String.valueOf(route.getVote().getNumVotes()) + " votos)");
        } catch (Exception e) {
            txt_note.setText("NOTA: (No disponible)");
        }
        txt_description_body.setText(Html.fromHtml(route.getBody()).toString().trim(), TextView.BufferType.SPANNABLE);

        // DIFFICULT PICTURE

        if (route.getDifficulty_tid().equals("18")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.marcador_facil));

        } else if (route.getDifficulty_tid().equals("16")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.marcador_media));

        } else if (route.getDifficulty_tid().equals("17")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.marcador_dificil));

        } else if (route.getDifficulty_tid().equals("22")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.marcador_muydificil));
        }

        // STARS ICONS

        img_star_1.setVisibility(View.GONE);
        img_star_2.setVisibility(View.GONE);
        img_star_3.setVisibility(View.GONE);
        img_star_4.setVisibility(View.GONE);
        img_star_5.setVisibility(View.GONE);

        try {
            if (route.getVote().getValue() > 10 && route.getVote().getValue() < 30) {
                img_star_1.setVisibility(View.VISIBLE);

            } else if (route.getVote().getValue() >= 30 && route.getVote().getValue() < 50) {
                img_star_1.setVisibility(View.VISIBLE);
                img_star_2.setVisibility(View.VISIBLE);

            } else if (route.getVote().getValue() >= 50 && route.getVote().getValue() < 70) {
                img_star_1.setVisibility(View.VISIBLE);
                img_star_2.setVisibility(View.VISIBLE);
                img_star_3.setVisibility(View.VISIBLE);

            } else if (route.getVote().getValue() >= 70 && route.getVote().getValue() < 90) {
                img_star_1.setVisibility(View.VISIBLE);
                img_star_2.setVisibility(View.VISIBLE);
                img_star_3.setVisibility(View.VISIBLE);
                img_star_4.setVisibility(View.VISIBLE);

            } else if (route.getVote().getValue() >= 90) {
                img_star_1.setVisibility(View.VISIBLE);
                img_star_2.setVisibility(View.VISIBLE);
                img_star_3.setVisibility(View.VISIBLE);
                img_star_4.setVisibility(View.VISIBLE);
                img_star_5.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }

        /*
        if (DataConection.hayConexion(this)) {
            // Si hay conexi�n, recargar los datos
            // panelCargando.setVisibility(View.VISIBLE);
            Route.routesInterface = this;
            Route.cargarRoute(app, paramNid);
            OfflineRoute.routesInterface = this;
        } else {
            // Si la ruta est� en la base de datos
            if (Offline.isNidInDB(app, app.DRUPAL_TYPE_ROUTE, Integer.valueOf(paramNid))) {
                OfflineRoute.routesInterface = this;
                OfflineRoute.cargarRouteOffline(app, paramNid);
            }
            // Si no hay conexi�n a Internet y la ruta no esta en la Base de Datos (offline)
            else
                Util.mostrarMensaje(
                        this,
                        getResources().getString(R.string.mod_global__sin_conexion_a_internet),
                        getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet));
        }
        */
    }

    /**
     * This method is used to check if the user has already downloaded the route's map (tpk) or not.
     */
    private void chekcOfflineMapState() {
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File map_directory = new File(root.getAbsolutePath() + "/localmaps");

            String files_list[] = map_directory.list();

            for (String file : files_list) {
                if (file.toString().equals(route.getNid().toString() + ".tpk")) {
                    Log.d("mapsDirectory sais:", " Existe el mapa de la ruta: " + route.getNid());

                    btn_Download_Map.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.boton_mapa));

                    route_tpk_downloaded = true;
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * This method is used for delete the downloaded route's map (tpk).
     */
    private void deletefflineMap() {
        File root = android.os.Environment.getExternalStorageDirectory();

        File[] files = new File(root.getAbsolutePath() + "/localmaps").listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile())
                    return file.getName().toString().equals(route.getNid().toString() + ".tpk");
                return false;
            }
        });

        for (File file : files) {
            file.delete();

            btn_Download_Map.setImageDrawable(ContextCompat.getDrawable(RouteDetailActivity.this, R.drawable.icono_animal_ave));

            route_tpk_downloaded = false;
        }
    }

    public void onResume() {
        super.onResume();
        imageMap.mBubbleMap.clear();
        imageMap.postInvalidate();
        // Activar GPS
        LocationDisplayManager ls = map.getLocationDisplayManager();

        if (ls.getLocationListener() == null) {
            ls.setLocationListener(new MyLocationListener());
            ls.setAutoPanMode(AutoPanMode.OFF);
        }

        ls.start();
        if (Offline.isNidInDB(app, app.DRUPAL_TYPE_ROUTE, Integer.parseInt(this.paramNid)) == false)
            //showDialog();
            Log.d("Dialod here", "Info descarga");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //Parar GPS
        LocationDisplayManager ls = map.getLocationDisplayManager();
        //ls.setLocationListener(new MyLocationListener());

        if (ls != null) {
            ls.stop();
        }
    }

    private void escucharEventos() {
        // add a click handler to react when areas are tapped


//		layoutVerEnMapa.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				Intent intent = new Intent(RouteDetailActivity.this, ConcreteMapActivity.class);
//				intent.putExtra(ConcreteMapActivity.PARAM_KEY_NID_MOSTRAR, miRoute.getNid());
//				intent.putExtra(ConcreteMapActivity.PARAM_KEY_TYPE_DRUPAL, app.DRUPAL_TYPE_ROUTE);
//				startActivity(intent);
//			}
//		});

        /*
        btnMenuMasValorar.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (User.isLoggedIn(app)) {
                    Intent intent = new Intent(RouteDetailActivity.this, VoteActivity.class);
                    intent.putExtra(VoteActivity.PARAM_KEY_NID_ITEM_A_VALORAR, miRoute.getNid());
                    intent.putExtra(VoteActivity.PARAM_KEY_TITLE_ITEM_A_VALORAR, miRoute.getTitle());
                    startActivity(intent);
                } else {
                    User.askForloginHere(RouteDetailActivity.this);
                }
            }
        });

        btnMenuMasMas.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                layoutBotonesMenuMas.setVisibility(View.GONE);
            }
        });

        btnMenuMasTelecarga.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                try {
                    Log.d("RouteDetailActv. sais:", " RUTE URL: " + route.getUrlMap());

                    new DownloadAndSaveTPK(app, Integer.parseInt(route.getNid()), route.getUrlMap(), (paramNid + ".tpk"), RouteDetailActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnValorar.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (User.isLoggedIn(app)) {
                    Intent intent = new Intent(RouteDetailActivity.this, VoteActivity.class);
                    intent.putExtra(VoteActivity.PARAM_KEY_NID_ITEM_A_VALORAR, miRoute.getNid());
                    intent.putExtra(VoteActivity.PARAM_KEY_TITLE_ITEM_A_VALORAR, miRoute.getTitle());
                    startActivity(intent);
                } else {
                    User.askForloginHere(RouteDetailActivity.this);
                }
            }
        });
//		btnCompartir.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				Share.compartir(app, RouteDetailActivity.this, miRoute);
//			}
//		});
        */

        // Al tocar un punto en el map
    }

    @SuppressLint("NewApi")
    private void inicializarForm() {
        //poner estilos (fuente, color, ...)
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.hide();
            }
        }

        Typeface tfScalaBold = Util.fontScala_Bold(this);

        /*
        txtTitulo.setTypeface(tfScalaBold);
        lblTitleHeader.setTypeface(tfScalaBold);

        lblDesnivel.setTypeface(tfScalaBold);
        lblDuracion.setTypeface(tfScalaBold);
        lblDistancia.setTypeface(tfScalaBold);
        lblDificultad.setTypeface(tfScalaBold);
        lblValoracion.setTypeface(tfScalaBold);

        lblValDesnivel.setTypeface(tfScalaBold);
        lblValDuracion.setTypeface(tfScalaBold);
        lblValDistancia.setTypeface(tfScalaBold);

        lblDescripcion.setTypeface(tfScalaBold);

        lblTitleHeader.setText(paramTitleRoute);
        // Poner la imagen de categoria
        if (paramCategoryRoute.equals("GR")) {
            imgViewCategory.setBackgroundResource(R.drawable.categoria_gr);
        } else {
            imgViewCategory.setBackgroundResource(R.drawable.categoria_pr);
        }
        panelCargando.setVisibility(View.GONE);
        */
    }

    public void seCargoRoute(Route route) {
//		if (route != null) {
//			this.miRoute = route;
//
//			// Poner el t�tulo
//			this.lblNombre.setText( this.miRoute.getTitle() );
//			this.txtTitulo.setText( this.miRoute.getTitle() );
//
//			// Poner la categor�a
//			if(this.miRoute.getCategory() != null){
//				this.lblCategoria.setText( this.miRoute.getCategory().getName() );
//			}else{
//				this.lblCategoria.setText( getResources().getString(R.string.mod_global__sin_datos) );
//			}
//
//			// Poner la dificultad
//			if(this.miRoute.getDifficulty() != null){
//				this.lblDificultad.setText( this.miRoute.getDifficulty().getName() );
//			}else{
//				this.lblDificultad.setText( getResources().getString(R.string.mod_global__sin_datos) );
//			}
//
//
//			if(miRoute.getImages() != null){
//				if(miRoute.getImages().size() > 0){
//					ResourceFile rf = miRoute.getImages().get(0);
//					BitmapManager.INSTANCE.loadBitmap(rf.getFileUrl(), imageViewPrincipal, 500, 400);
//				}
//			}
//
//
//			// Poner la descripci�n
//			if(miRoute.getBody() != null && !miRoute.getBody().equals("") && !miRoute.getBody().equals("null") && !miRoute.getBody().equals("(null)")){
//				this.lblDescripcion.setText( Html.fromHtml(miRoute.getBody()), TextView.BufferType.SPANNABLE );
//			}else{
//				this.lblDescripcion.setText( getResources().getString(R.string.mod_global__sin_datos) );
//			}
//		}
        this.route = route;
        //pintar ruta
        if (route != null) {
            Polyline polylineProyectado = null;
            if (route.getTrack() != null) {
                polylineProyectado = WKTUtil.getPolylineFromWKTLineStringField(app, route.getTrack());
            }

            ArrayList<Object> geometrias = new ArrayList<Object>();
            geometrias.add(polylineProyectado);
            dibujarGeometrias(geometrias, route.getTitle(), route.getClass().getName(), route.getNid(), null, paramColorRoute);
        }

        dibujarPoisInRoute(route);
        // Centrar en el extent de la capa
        centrarEnExtentCapa(geometricLayer);

        // Poner la imagen de dificultad
        String dificultad = route.getDifficulty().getName();
        // Tr�s Facile
        /*
        if (dificultad.equals(this.getResources().getString(R.string.muy_facil)))
            imgDificultad.setBackgroundResource(R.drawable.marcador_facil);
            //Facile
        else if (dificultad.equals(this.getResources().getString(R.string.facil)))
            imgDificultad.setBackgroundResource(R.drawable.marcador_media);
            //Moyene
        else if (dificultad.equals(this.getResources().getString(R.string.medio)))
            imgDificultad.setBackgroundResource(R.drawable.marcador_dificil);
            //Difficile
        else if (dificultad.equals(this.getResources().getString(R.string.dificil)))
            imgDificultad.setBackgroundResource(R.drawable.marcador_muydificil);

        // Poner numero de avis e imagen votos
        String valString = app.getApplicationContext().getResources().getString(
                R.string.mod_discover__nota);
        lblValoracion.setText(valString + " (" + String.valueOf(route.getVote().getNumVotes()) + " votos)");
        if (route.getVote() != null) {
            if (route.getVote().getValue() <= 10) {
                // Si es menor o igual a 0
                imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
            } else if (route.getVote().getValue() > 10 && route.getVote().getValue() < 30) {
                // Si est� entre 1 y 24
                imgViewValoracion.setImageResource(R.drawable.puntuacion_1_estrellas);
            } else if (route.getVote().getValue() >= 30 && route.getVote().getValue() < 50) {
                // Si est� entre 25 y 49
                imgViewValoracion.setImageResource(R.drawable.puntuacion_2_estrellas);
            } else if (route.getVote().getValue() >= 50 && route.getVote().getValue() < 70) {
                // Si est� entre 50 y 74
                imgViewValoracion.setImageResource(R.drawable.puntuacion_3_estrellas);
            } else if (route.getVote().getValue() >= 70 && route.getVote().getValue() <= 90) {
                // Si est� entre 75 y 90
                imgViewValoracion.setImageResource(R.drawable.puntuacion_4_estrellas);
            } else {
                imgViewValoracion.setImageResource(R.drawable.puntuacion_5_estrellas);
            }
        } else {
            imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
        }*/
        //panelCargando.setVisibility(View.GONE);

    }

    public void producidoErrorAlCargarRoute(String error) {
        Log.d("Milog", "producidoErrorAlCargarRoute: " + error);
        // panelCargando.setVisibility(View.GONE);
        Util.mostrarMensaje(this, getResources().getString(R.string.mod_global__error), getResources().getString(R.string.mod_global__error));
        finish();
    }

    @Override
    public void seCargoListaRoutes(ArrayList<Route> routes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarListaRoutes(String error) {
        // TODO Auto-generated method stub

    }


    public void inicializarMapa() {
        ponerCapaBase();

        map.setEsriLogoVisible(true);

        geometricLayer = new GraphicsLayer();
        geometricPOIsLayer = new GraphicsLayer();

        map.addLayer(geometricLayer);
        map.addLayer(geometricPOIsLayer);
    }

    private void representarGeometrias() {
        if (DataConection.hayConexion(this)) {
            Log.d("Milog", "Voy a cargar los datos");

            if (paramType != null) {
                if (paramType.equals(app.DRUPAL_TYPE_ROUTE)) {
                    Route.routesInterface = this;
                    Route.cargarRoute(app, paramNid);
                }
            }
        } else {
            // panelCargando.setVisibility(View.GONE);
            if (Offline.isNidInDB(app, app.DRUPAL_TYPE_ROUTE, Integer.valueOf(paramNid))) {
                OfflineRoute.routesInterface = this;
                OfflineRoute.cargarRouteOffline(app, paramNid);
            } else
                Util.mostrarMensaje(
                        this,
                        getResources().getString(
                                R.string.mod_global__sin_conexion_a_internet),
                        getResources()
                                .getString(
                                        R.string.mod_global__no_dispones_de_conexion_a_internet));
        }
    }

    private void dialogPoiDescription(String nombre, String descripcion) {
        dialogPoi = new Dialog(this);
        dialogPoi.setContentView(R.layout.mod_discover_dialog_poi_route);
        dialogPoi.setTitle(nombre);
        dialogPoi.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialogPoi.findViewById(R.id.txtPoiDescription);
        text.setText(descripcion);

        //set up button
        Button button = (Button) dialogPoi.findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPoi.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        dialogPoi.show();
    }

    private View getViewForPoiCallout(final String nombre, final String descripcion) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.mod_discover__layout_callout_poi_in_route, null);
        final TextView lblNombre = (TextView) view
                .findViewById(R.id.lblNombrePunto);
        final TextView lblDescripcion = (TextView) view
                .findViewById(R.id.lblDescripcionPunto);
        lblNombre.setText(nombre);
        lblDescripcion.setText(descripcion);
        final Button btnCerrarDialogo = (Button) view
                .findViewById(R.id.btnVerFichaPunto);
        btnCerrarDialogo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialogPoiDescription(nombre, descripcion);
            }
        });
        return view;
    }

    private View getViewForCalloutPoi(final String nombre, String clase, final String nidDrupal, String cat) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.mod_discover__layout_callout_mapa, null);
        final TextView lblNombre = (TextView) view
                .findViewById(R.id.lblNombrePunto);
        final TextView lblCategoria = (TextView) view
                .findViewById(R.id.lblCategoriaPunto);
        final Button btnCerrarDialogo = (Button) view
                .findViewById(R.id.btnVerFichaPunto);

        // Ponerle las propiedades necesarias
        if (clase.equals(Poi.class.getName())) {
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            lblCategoria.setText(cat);

            // Escuchar el evento del click del bot?n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RouteDetailActivity.this,
                            PoiDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_NID,
                            nidDrupal);
                    //intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE,0.0);
                    //int desnivel = 0;
                    //intent.putExtra(PoiDetailActivity.PARAM_KEY_DESNIVEL, desnivel);
                    //intent.putExtra(PoiDetailActivity.PARAM_KEY_NUMBERVOTES,0);
                    //intent.putExtra(PoiDetailActivity.PARAM_KEY_VALORATION,0);
                    //BitmapManager.INSTANCE.cache.remove(poiPulsado.getMainImage());
                    startActivity(intent);
                    callout.hide();
                }
            });
        }

        return view;
    }


    private View getViewForCallout(String nombre, String clase, final String cat, final String nid) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.mod_discover__layout_callout_mapa, null);
        final TextView lblNombre = (TextView) view
                .findViewById(R.id.lblNombrePunto);
        final TextView lblCategoria = (TextView) view
                .findViewById(R.id.lblCategoriaPunto);
        final Button btnCerrarDialogo = (Button) view
                .findViewById(R.id.btnVerFichaPunto);

        // Ponerle las propiedades necesarias
        if (clase.equals(Poi.class.getName())) {
            Log.d("Milog", "El objeto pulsado lleva un poi");
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            if ((cat == null) || (cat.equals("")))
                lblCategoria.setText("Punto de interés");
            else
                lblCategoria.setText(cat);

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RouteDetailActivity.this,
                            PoiDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
                    startActivity(intent);
                    callout.hide();
                }
            });
        } else if (clase.equals(Route.class.getName())) {
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            lblCategoria.setText("Route");

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
//					Intent intent = new Intent(RouteDetailActivity.this,
//							RouteDetailActivity.class);
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
//					startActivity(intent);
                    callout.hide();
                }
            });
        }

        return view;
    }

    private void dibujarPoisInRoute(Route ruta) {
        resourcePois = ruta.getPois();
        ArrayList<ResourcePoi> pois = resourcePois;
        PictureMarkerSymbol sym;
        String allPoisDescription = "";
        Context ctx = app.getApplicationContext();
        for (int j = 0; j < pois.size(); j++) {
            int number = 1;
            Point puntoProyectado = GeometryEngine.project(pois.get(j).getLongitude(),
                    pois.get(j).getLatitude(), app.spatialReference);
            number = pois.get(j).getNumber();
            int clase = pois.get(j).getType();
            Log.d("LA clase es:", String.valueOf(clase));
            String claseNombre = "";
            allPoisDescription += pois.get(j).getTitle() + "\n" + pois.get(j).getBody() + "\n\n";
            switch (clase) {
                case 36: //Lugar de interes cultural
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_descubrir));
                    firstPoint = puntoProyectado;
                    claseNombre = this.getResources().getString(R.string.lugar_de_interes_cultural);
                    break;
                case 30: //Lugar de interes natural
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_naturaleza));
                    claseNombre = this.getResources().getString(R.string.lugar_de_interes_natural);
                    break;
                case 33: //Servicios estaciones
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.poi_icono));
                    claseNombre = this.getResources().getString(R.string.servicios_estaciones);
                    break;
                case 28: //Servicios sanidad
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_sanidad));
                    claseNombre = this.getResources().getString(R.string.servicios_sanidad);
                    break;
                case 26: //Alojamientos
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_hotel));
                    claseNombre = this.getResources().getString(R.string.alojamientos);
                    break;
                case 27: //REstauracion
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_restaurant));
                    claseNombre = this.getResources().getString(R.string.restauracion);
                    break;
                case 25: //Servicios oficinas de turismo
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.icono_info));
                    claseNombre = this.getResources().getString(R.string.servicios_oficinas_de_turismo);
                    break;

                default:
                    sym = new PictureMarkerSymbol(ctx, getResources().getDrawable(R.drawable.ic_launcher));
                    claseNombre = this.getResources().getString(R.string.punto_de_interes);
            }

            final HashMap<String, Object> attrs = new HashMap<String, Object>();
            attrs.put("nombre", pois.get(j).getTitle());
            attrs.put("descripcion", Html.fromHtml(pois.get(j).getBody()));
            attrs.put("nid", Integer.toString(pois.get(j).getNid()));
            Log.d("EL id del poi es: ", Integer.toString(pois.get(j).getNid()));
            attrs.put("clase", Poi.class.getName());
            attrs.put("cat", claseNombre);

            Graphic gr = new Graphic(puntoProyectado, sym, attrs);
            geometricPOIsLayer.addGraphic(gr);
        }
        // lblPoisDescripcion.setText(Html.fromHtml(allPoisDescription));
    }

    private void dibujarGeometrias(ArrayList<Object> geometrias,
                                   String paramNombre, String paramNombreClase, String paramNid, final String urlIcon, int color) {
        int polygonFillColor = Color.rgb(55, 132, 218);
        int polygonBorderColor = Color.rgb(27, 87, 187);
        //int pointColor = Color.rgb(206, 240, 5);
        if (geometrias != null) {
            for (int j = 0; j < geometrias.size(); j++) {
                Object geomObj = geometrias.get(j);

                final HashMap<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("clase", paramNombreClase);
                attrs.put("nid", paramNid);
                attrs.put("nombre", paramNombre);

                if (geomObj != null
                        && geomObj.getClass().getName()
                        .equals(Polygon.class.getName())) {
                    Polygon polygon = (Polygon) geomObj;
                    SimpleFillSymbol sym = new SimpleFillSymbol(
                            polygonFillColor);
                    sym.setAlpha(100);
                    sym.setOutline(new SimpleLineSymbol(polygonBorderColor, 4,
                            STYLE.SOLID));
                    Graphic gr = new Graphic(polygon, sym, attrs);
                    geometricLayer.addGraphic(gr);
                } else if (geomObj != null
                        && geomObj.getClass().getName()
                        .equals(Point.class.getName())) {

                    final Point point = (Point) geomObj;

                    // Cargar los iconos de remoto con AsyncTask
                    new AsyncTask<Integer, Float, Integer>() {

                        PictureMarkerSymbol sym = null;

                        // Ejecucion pesada
                        protected Integer doInBackground(Integer... params) {
                            Log.d("Milog", "URL Icono: " + urlIcon);
                            if (urlIcon != null) {
                                try {
                                    sym = new PictureMarkerSymbol(urlIcon);
                                } catch (Exception e) {
                                    Log.d("Milog", "Excepcion al cargar icono: " + e.toString());
                                    sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.ic_launcher));
                                }
                            } else {
                                sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.ic_launcher));
                            }
                            return 0;
                        }

                        // Acciones despu�s de ejecutarse (Main Thread)
                        protected void onPostExecute(Integer bytes) {
                            Graphic gr = new Graphic(point, sym, attrs);
                            geometricLayer.addGraphic(gr);

                            // Centrar en el poi y hacer zoom
                            double scale = 5000.0;
                            map.centerAt(point, true);
                            map.setScale(scale);
                        }
                    }.execute(1);

                } else if (geomObj != null
                        && geomObj.getClass().getName()
                        .equals(Polyline.class.getName())) {
                    Polyline polyline = (Polyline) geomObj;
                    //int color = paramColorRoute;
                    //TOTO
                    Graphic gr = new Graphic(polyline, new SimpleLineSymbol(color, 6, STYLE.SOLID), attrs);
                    geometricLayer.addGraphic(gr);
                }
            }
        }
    }

    private void centrarEnExtentCapa(GraphicsLayer capa) {
        // Hacer zoom a la capa de geometrias
        Envelope env = new Envelope();
        Envelope NewEnv = new Envelope();
        for (int i : capa.getGraphicIDs()) {
            Geometry geom = capa.getGraphic(i).getGeometry();
            geom.queryEnvelope(env);
            NewEnv.merge(env);
        }

        this.map.setExtent(NewEnv, 100);
    }

    private void copyMapFileInAppFileDir(String fileOutput, String fileAssetsInput) {
        FileOutputStream destinationFileStream = null;
        InputStream assetsOriginFileStream = null;
        try {
            //destinationFileStream = openFileOutput(fileOutput, Context.MODE_PRIVATE);
            destinationFileStream = new FileOutputStream(new File(fileOutput));
            assetsOriginFileStream = getAssets().open(fileAssetsInput);

            byte[] buf = new byte[1024];
            int len;
            while ((len = assetsOriginFileStream.read(buf)) > 0) {
                destinationFileStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            Log.d("Sauvage", e.getMessage());
        } finally {
            try {
                assetsOriginFileStream.close();
                destinationFileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ponerCapaBase() {
        /* Codigo de prueba */
        if (!DataConection.hayConexion(this)) {
            String basemapurl = Util.getUrlRouteBaseLayerOffline(app, paramNid, paramMapUrl);
            ArcGISLocalTiledLayer baseLayer;
            baseLayer = new ArcGISLocalTiledLayer(basemapurl);
            map.addLayer(baseLayer);
            map.setMaxScale(1000);
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
        Layer[] capas = map.getLayers();
        if (capas != null) {
            Log.d("Milog", "capas no es nulo");
            if (capas.length > 0) {

                Log.d("Milog", "Hay alguna capa");
                Object capa0 = capas[0];
                Log.d("Milog", "Tenemos capa0");
                // si la capa base seleccionada es del mismo tipo que la capa 0
                if (capaBase.getClass().getName()
                        .equals(capa0.getClass().getName())) {
                    Log.d("Milog",
                            "La clase de la capa base es igual que la clase de la capa0");
                    if (capaBase.getClass() == BingMapsLayer.class) {
                        Log.d("Milog", "capaBase es de tipo BING");
                        BingMapsLayer capaBaseCasted = (BingMapsLayer) capaBase;
                        BingMapsLayer capa0Casted = (BingMapsLayer) capa0;

                        if (capaBaseCasted.getMapStyle().equals(
                                capa0Casted.getMapStyle())) {
                            return;
                        } else {
                            map.removeLayer(0);
                            Log.d("Milog",
                                    "PUNTO INTERMEDIO BING: el map tiene "
                                            + map.getLayers().length
                                            + " capas");
                        }
                    } else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
                        Log.d("Milog", "capaBase es de tipo TiledMap");
                        ArcGISTiledMapServiceLayer capaBaseCasted = (ArcGISTiledMapServiceLayer) capaBase;
                        ArcGISTiledMapServiceLayer capa0Casted = (ArcGISTiledMapServiceLayer) capa0;
                        String strUrlCapaBaseCasted = capaBaseCasted.getUrl().toString();
                        String strUrlCapa0Casted = capa0Casted.getUrl().toString();
                        if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
                            return;
                        } else {
                            map.removeLayer(0);
                            Log.d("Milog",
                                    "PUNTO INTERMEDIO TILED: el map tiene "
                                            + map.getLayers().length
                                            + " capas");
                        }
                    }
                    Log.d("Milog", "La capa 0 es de clase "
                            + capa0.getClass().getName());
                } else {// si la capa base seleccionada no es del mismo tipo que
                    // la capa 0

                    if (capaBase.getClass() == BingMapsLayer.class) {
                        map.removeLayer(0);
                    } else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
                        map.removeLayer(0);
                    }
                }
            }
            // btnAbrirCapas.setEnabled(true);
            if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {

                if (capas.length > 0) {
                    map.addLayer((ArcGISTiledMapServiceLayer) capaBase, 0);
                } else {
                    map.addLayer((ArcGISTiledMapServiceLayer) capaBase);
                }

            } else if (capaBase.getClass() == BingMapsLayer.class) {

                if (capas.length > 0) {
                    map.addLayer((BingMapsLayer) capaBase, 0);
                } else {
                    map.addLayer((BingMapsLayer) capaBase);
                }

            } else {
                // otro tipo de capa
            }

            app.capaBaseSeleccionada = capaSeleccionada;
            Log.d("Milog", "El map tiene " + map.getLayers().length
                    + " capas");
        }
    }


    /**
     * Location listener propio
     *
     * @author
     */
    private class MyLocationListener implements LocationListener {

        public MyLocationListener() {
            super();
        }

        public void onLocationChanged(Location loc) {
            if (loc == null)
                return;
//			else
//				GPS.setLastLocation(loc);
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }


    @Override
    public void seCargoListaRoutesOffline(ArrayList<Route> routes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarListaRoutesOffline(String error) {
        // TODO Auto-generated method stub
        // panelCargando.setVisibility(View.GONE);

    }

    @Override
    public void seCargoRouteOffline(Route item) {
        // TODO Auto-generated method stub
        if (item != null)
            seCargoRoute(item);
        // panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void producidoErrorAlCargarRouteOffline(String error) {
        // TODO Auto-generated method stub
        // panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void seCerroComboCapas() {
        // TODO Auto-generated method stub
        ponerCapaBase();
    }

    // El interface de Pois es solo para el filtrado de pois.
    private void cargaActivitySearch() {
        Intent intent = new Intent(RouteDetailActivity.this,
                PoisSearchActivity.class);
        intent.putExtra(PoisSearchActivity.PARAM_KEY_HIDE_EVENTS, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    @Override
    public void seCargoListaPois(ArrayList<Poi> pois) {
        // TODO Auto-generated method stub
        if (pois != null) {
            this.arrayPois = pois;
            Cache.arrayPois = pois;
            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }
            if (Cache.hashMapPois == null) {
                Cache.hashMapPois = new HashMap<String, Integer>();
            }
        }
        // panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaPois");
    }

    @Override
    public void producidoErrorAlCargarListaPois(String error) {
        // TODO Auto-generated method stub
        Log.d("Milog", "producidoErrorAlCargarListaPois: " + error);
        // panelCargando.setVisibility(View.GONE);
    }

    @Override
    public void seCargoPoi(Poi poi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void producidoErrorAlCargarPoi(String error) {
        // TODO Auto-generated method stub

    }

    private void recargarDatosPois() {
        if (Cache.arrayPois != null) {
            this.arrayPois = Cache.arrayPois;
            return;
        }
        if (DataConection.hayConexion(this)) {
            // Si hay conexi�n, recargar los datos
            // panelCargando.setVisibility(View.VISIBLE);
            Poi.poisInterface = this;
            Poi.cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
                    getLoc()[0], // Latitud
                    getLoc()[1], // Longitud
                    0, // Radio en Kms
                    0, // N�mero de elementos por p�gina
                    0, // P�gina
                    null, // Tid de la categor�a que queremos filtrar
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
        }

        // Poner el texto al bot�n que hace de combo
        //this.btnCategorias.setText(this.categoryName);
    }

    private void filterPois() {
        if (arrayPois != null) {
            for (int i = 0; i < arrayPois.size(); i++) {
                if (PoisSearch.checkCriteria(arrayPois.get(i), this.getApplicationContext()))
                    arrayFilteredPois.add(arrayPois.get(i));
            }
            dibujarPoisEnMapa(arrayFilteredPois);
        }
        lastLatitude = getLoc()[0];
        lastLongitude = getLoc()[1];
    }

    private void dibujarPoisEnMapa(ArrayList<Poi> aFilteredPois) {

        for (int i = 0; i < aFilteredPois.size(); i++) {
            Poi poi = aFilteredPois.get(i);
            // Por cada poi obtener sus coordenadas y construir un objeto
            // Point de Arcgis
            GeoPoint gp = poi.getCoordinates();
            Point puntoProyectado = GeometryEngine.project(
                    gp.getLongitude(), gp.getLatitude(),
                    app.spatialReference);
            ArrayList<Object> geometrias = new ArrayList<Object>();
            geometrias.add(puntoProyectado);

            String cat = "poi";
            if (poi.getCategory() != null) {
                cat = poi.getCategory().getName();
            }

            String icon = null;
            if (poi.getCategory() != null && poi.getCategory().getIcon() != null) {
                icon = poi.getCategory().getIcon();
            }
            final HashMap<String, Object> attrs = new HashMap<String, Object>();
            attrs.put("clase", poi.getClass().getName());
            attrs.put("nid", poi.getNid());
            attrs.put("nombre", poi.getTitle());
            attrs.put("cat", cat);

            String paramCat = cat;
            PictureMarkerSymbol sym = null;
            if (paramCat.equals("Chambre d'h�tes") || paramCat.equals("H�tellerie")
                    || paramCat.equals("H�bergement collectif") || paramCat.equals("H�tellerie de plein air")
                    || paramCat.equals("Meubl�") || paramCat.equals("R�sidence")) {
                //Drawable d = getResources().getDrawable(R.drawable.icono_hotel);
                //Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.icono_hotel));
                // Scale it to 50 x 50
                //Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                //sym = new PictureMarkerSymbol(dr);
                //sym.setOffsetY(-px2dip(this.getApplicationContext(),d.getIntrinsicHeight() / 2));
            } else if (paramCat.equals("Mus�e") || paramCat.equals("Patrimoine Naturel")
                    || paramCat.equals("Site et Monument") || paramCat.equals("Office de Tourisme")
                    || paramCat.equals("Parc et Jardin")) {
                sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.icono_descubrir));
            } else if (paramCat.equals("Restauration"))
                sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.icono_restaurante));
            else
                sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.ic_launcher));

            Graphic gr = new Graphic(puntoProyectado, sym, attrs);
            geometricPOIsLayer.addGraphic(gr);
        }
    }

    /* Proceso que se realizar� tras la busqueda de Pois */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        geometricPOIsLayer.removeAll();
        if (data == null) {
            return;
        }
        String name = data.getStringExtra("name");
        if (arrayFilteredPois == null)
            arrayFilteredPois = new ArrayList<Poi>();
        arrayFilteredPois.clear();
        filterPois();
    }

    /* This is a fast code to get the last known location of the phone. If there is no exact
     * gps-information it falls back to the network-based location info.
     */
    private double[] getLoc() {
        app.getApplicationContext();
        LocationManager lm = (LocationManager) app.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

	    /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }

    /*
    private void showDialog () {
        // Creating alert Dialog with one Button

        dialogPoi = new Dialog(this);
        dialogPoi.setContentView(R.layout.mod_discover_dialog_poi_route);
        dialogPoi.setTitle("Info " + "T�l�chargement");
        dialogPoi.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialogPoi.findViewById(R.id.txtPoiDescription);
        text.setText(R.string.mod_home__info_telecarga);

        //set up button
        Button button = (Button) dialogPoi.findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick(View v) {
                dialogPoi.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialogPoi.show();

    }
*/
    @Override
    public void seCargoListaPoisOffline(ArrayList<Poi> pois) {
        // TODO Auto-generated method stub
        if (pois != null) {
            this.arrayPois = pois;
            Cache.arrayPois = pois;
            if (Cache.hashMapPois == null) {
                Cache.iniHashMapPois();
            }
        }
        // panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "seCargoListaPoisOffline");
    }

    @Override
    public void producidoErrorAlCargarListaPoisOffline(String error) {
        // TODO Auto-generated method stub
        // panelCargando.setVisibility(View.GONE);
        Log.d("Milog", "Error al cargar Pois Offline");
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
    public void seCompletoDescarga() {
        // TODO Auto-generated method stub
        // panelCargandoMapas.setVisibility(View.GONE);
        Util.mostrarMensaje(RouteDetailActivity.this, getResources().getString(R.string.mod_discover__telecarga_completada_titulo),
                getResources().getString(R.string.mod_discover__telecarga_completada_info));
    }

    @Override
    public void setProgresoDescarga(int progress) {
        // TODO Auto-generated method stub
        //ProgressBar pb = (ProgressBar) panelCargandoMapas.getChildAt(0);
        // RouteDetailActivity.this.pb.setProgress(progress);
    }

    @Override
    public void inicioDescarga() {
        // TODO Auto-generated method stub
        // RouteDetailActivity.this.panelCargandoMapas.setVisibility(View.VISIBLE);

    }

}