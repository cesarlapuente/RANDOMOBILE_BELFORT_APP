package eu.randomobile.pnrlorraine.mod_discover.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
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
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.WKTUtil;
import eu.randomobile.pnrlorraine.mod_global.data_access.DownloadAndSaveTPK;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_multi_viewers.imgs.GridImagesActivity;
import eu.randomobile.pnrlorraine.mod_multi_viewers.vids.ListVideosActivity;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceFileDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RouteDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.VoteDAO;
import eu.randomobile.pnrlorraine.mod_share.Share;
import eu.randomobile.pnrlorraine.mod_vote.VoteActivity;

public class RouteDetailActivity extends Activity implements
        ComboCapasMapaInterface {

    public static final String PARAM_KEY_NID = "nid";
    public static final String PARAM_KEY_DISTANCE = "distance";
    public static final String PARAM_KEY_TITLE_ROUTE = "route_title";
    public static final String PARAM_KEY_CATEGORY_ROUTE = "route_category";
    public static final String PARAM_KEY_MAP_URL = "map_url";
    public static final String PARAM_KEY_COLOR_ROUTE = "route_color";
    public static double lastLatitude = 0;
    public static double lastLongitude = 0;
    String paramNid;
    String paramTitleRoute;
    String paramCategoryRoute;
    String paramMapUrl;
    double paramDistanceMeters;
    LinearLayout wrapper_description;
    ScrollView scrollView2;
    String paramType;
    int paramColorRoute = 0;
    private MainApp app;
    private Route route;
    private ImageMap imageMap;
    private MapView map;
    private GraphicsOverlay geometricLayer;
    private GraphicsOverlay geometricPOIsLayer;
    private GraphicsOverlay poisGr;
    private Callout callout;
    private Point firstPoint;
    private ImageButton btn_Layers;
    private ImageButton btn_Download_Map;
    private ImageButton btn_Rate;
    private ImageButton btn_Galery;



    // ProgressBar pb;
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
    private Dialog dialogPoi = null;
    private String TAG;

    private RouteDAO routeDAO;
    private PoiDAO poiDAO;
    private VoteDAO voteDAO;
    private RessourceFileDAO fileDAO;


    private Envelope envelope;
    private Basemap basemap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        routeDAO = new RouteDAO(getApplicationContext());
        poiDAO = new PoiDAO((getApplicationContext()));
        voteDAO = new VoteDAO(getApplicationContext());
        fileDAO = new RessourceFileDAO(getApplicationContext());

        envelope = new Envelope(6.8000, 47.7000, 6.9, 47.6, SpatialReferences.getWgs84());

        basemap = Basemap.createImagery();

        this.app = (MainApp) getApplication();

        poisGr = new GraphicsOverlay();


        TAG = this.getLocalClassName();

        // Recoger parametros
        Bundle b = getIntent().getExtras();

        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);

            route = routeDAO.getRoute(paramNid);
            route.setPois(poiDAO.getResourcePois(route.getIdsPois()));
            route.setVote(voteDAO.getVote(route.getNid()));
            route.setImages(fileDAO.getListResourceFiles(route.getNid(), "images"));

            paramDistanceMeters = b.getDouble(PARAM_KEY_DISTANCE);
            paramTitleRoute = b.getString(PARAM_KEY_TITLE_ROUTE);
            paramCategoryRoute = b.getString(PARAM_KEY_CATEGORY_ROUTE);
            paramMapUrl = b.getString(PARAM_KEY_MAP_URL);
            paramColorRoute = route.getColor();
        }

        paramType = app.DRUPAL_TYPE_ROUTE;

        map = (MapView) findViewById(R.id.mapa);

        ArcGISMap mapArgis = new ArcGISMap(basemap);
        map.setMap(mapArgis);
        initializeComponents();
        setData();
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
                                    Toast.makeText(RouteDetailActivity.this, getString(R.string.mod_discover__appli_carte), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        break;
                    }
                    case "POINTS": {

                        poisGr.setVisible(!poisGr.isVisible());
                        break;
                    }
                    case "PLUS": {
                        Share.compartir(app, RouteDetailActivity.this, route);
                        break;
                    }
                    case "INFO": {
                        if (scrollView2.getVisibility() == View.GONE) {
                            scrollView2.setVisibility(View.VISIBLE);
                        }
                        else {
                            scrollView2.setVisibility(View.GONE);
                        }
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

        map.setOnTouchListener(new DefaultMapViewOnTouchListener(this, map){
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e) {
                // Si el mapa no est� cargado, salir
                if (map.getMap().getLoadStatus() == LoadStatus.NOT_LOADED) {
                    return false;
                }
                final android.graphics.Point point = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                final double tolerance = 8;
                final ListenableFuture<List<IdentifyGraphicsOverlayResult>> graphicsIDS = map.identifyGraphicsOverlaysAsync(point, tolerance, false);
                graphicsIDS.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        final ListenableFuture<List<IdentifyGraphicsOverlayResult>> graphicsPoisSearch = map.identifyGraphicsOverlaysAsync(point, tolerance, false);
                        graphicsPoisSearch.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (graphicsIDS != null && !graphicsIDS.get().isEmpty()) {
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
                                            String descripcion = (String) gr.getAttributes().get("descripcion");
                                            String cat = (String) gr.getAttributes().get("cat");

                                            callout = map.getCallout();
                                            callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.style_callout_mapa_global));
                                            callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
                                            View contenidoCallout = null;

                                            if (!"Point directionnel".equals(cat)) {
                                                if (!clase.equals(Route.class.getName()))
                                                    contenidoCallout = getViewForCallout(nombre,
                                                            clase, cat, nid);
                                            } else {
                                                if (callout.isShowing()) {
                                                    callout.dismiss();
                                                }
                                                dialogPoiDescription(nombre, descripcion);
                                            }

                                            if (contenidoCallout != null) {
                                                callout.setContent(contenidoCallout);
                                                callout.setLocation(map.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()))));
                                                callout.show();
                                            }
                                        }

                                    } else if (graphicsPoisSearch != null && !graphicsPoisSearch.get().isEmpty()) {
                                        List lrSearch = graphicsPoisSearch.get();

                                        Graphic gr = geometricPOIsLayer.getGraphics().get(geometricLayer.getGraphics().indexOf(lrSearch.get(0)));

                                        if (gr != null) {
                                            String nombre = (String) gr.getAttributes().get("nombre");
                                            String clase = (String) gr.getAttributes().get("clase");
                                            String nid = (String) gr.getAttributes().get("nid");
                                            String descripcion = (String) gr.getAttributes().get("descripcion");
                                            String cat = (String) gr.getAttributes().get("cat");

                                            callout = map.getCallout();
                                            callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.style_callout_mapa_global));
                                            callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
                                            View contenidoCallout;

                                            if (nid != null) {
                                                contenidoCallout = getViewForCallout(nombre, clase, cat, nid);

                                            } else {
                                                contenidoCallout = getViewForPoiCallout(nombre, descripcion);
                                            }

                                            callout.setContent(contenidoCallout);
                                            callout.setLocation(map.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()))));
                                            callout.show();
                                        }

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
                    }
                });
                return true;
            }
        });

        map.getMap().addLoadStatusChangedListener(new LoadStatusChangedListener() {
            @Override
            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
                if (LoadStatus.LOADED == loadStatusChangedEvent.getNewLoadStatus()) {
                /*LocationDisplay ls = map.getLocationDisplay();
                ls.addLocationChangedListener(new MyLocationListener());
                ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                ls.startAsync();*/
                    representarGeometrias();
                }
            }
        });


        // <----------------->_BUTTONS_DECLARATIONS_<----------------->

        btn_Layers = (ImageButton) findViewById(R.id.btn_Layer);
        btn_Layers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ComboCapasMapa comboCapas = new ComboCapasMapa(getApplication(), RouteDetailActivity.this, basemap);
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
        txt_description_body = (TextView) findViewById(R.id.txt_description_body);

        // <----------------->_IMAGEVIEWS_DECLARATIONS_<----------------->

        img_difficulty = (ImageView) findViewById(R.id.img_difficulty);
        img_star_1 = (ImageView) findViewById(R.id.img_star_1);
        img_star_2 = (ImageView) findViewById(R.id.img_star_2);
        img_star_3 = (ImageView) findViewById(R.id.img_star_3);
        img_star_4 = (ImageView) findViewById(R.id.img_star_4);
        img_star_5 = (ImageView) findViewById(R.id.img_star_5);

        wrapper_description = (LinearLayout) findViewById(R.id.wrapper_description);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);

    }

    private void setData() {

        txt_route_title.setText(route.getTitle());
        txt_ramp.setText(Util.formatDesnivel(route.getSlope()));
        txt_duration.setText(Util.formatDuracion(route.getEstimatedTime()));
        txt_distance.setText(Util.formatDistanciaRoute(route.getRouteLengthMeters()));
        try {
            txt_note.setText("NOTE: (" + String.valueOf(route.getVote().getNumVotes()) + " votes)");
        } catch (Exception e) {
            txt_note.setText("NOTE: (Pas disponible)");
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
        /*LocationDisplay ls = map.getLocationDisplay();

        if (ls.isShowLocation() == false) {
            ls.addLocationChangedListener(new MyLocationListener());
            ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
        }

        ls.startAsync();
        if (Offline.isNidInDB(app, app.DRUPAL_TYPE_ROUTE, Integer.parseInt(this.paramNid)) == false)
            //showDialog();
            Log.d("Dialod here", "Info descarga");*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //Parar GPS
        LocationDisplay ls = map.getLocationDisplay();
        //ls.setLocationListener(new MyLocationListener());

        if (ls != null) {
            ls.stop();
        }
    }

    private void representarGeometrias() {

        GraphicsOverlay gr = new GraphicsOverlay();
        if (route != null) {
            Polyline polylineProyectado = null;
            double latmin = 180;
            double latmax = -180;
            double lonmin = 180;
            double lonmax = -180;
            polylineProyectado = WKTUtil.getPolylineFromWKTLineStringField(app, route.getTrack());

            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, route.getColor(), 6);

            gr.getGraphics().add(new Graphic(polylineProyectado, lineSymbol));

            for (ResourcePoi p : route.getPois()) {
                PictureMarkerSymbol sym;
                int type = p.getType();
                int number = p.getNumber();
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
                String claseNombre = "";
                switch (type) {
                    case 52:
                        try {
                            String num = String.valueOf(number);
                            if (number == 1) {
                                firstPoint = puntoProyectado;
                            }
                            if (number < 10) {
                                num = "0" + number;
                            }
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(getResources().getIdentifier("mapa_ruta_num_" + num, "drawable", getPackageName())));
                            claseNombre = this.getResources().getString(R.string.punto_de_direccion);
                        } catch (Exception e) {
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher));
                        }
                        break;
                    case 26:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51: // Hébergements
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_hotel));
                        claseNombre = this.getResources().getString(R.string.alojamientos);
                        firstPoint = puntoProyectado;
                        break;
                    case 30: //Patrimoine naturel
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_naturaleza));
                        claseNombre = this.getResources().getString(R.string.lugar_de_interes_natural);
                        break;
                    case 36:  //Monuments
                    case 28:
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_descubrir));
                        claseNombre = this.getResources().getString(R.string.lugar_de_interes_cultural);
                        break;
                    case 27: //Restauracion
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_restaurante));
                        claseNombre = this.getResources().getString(R.string.restauracion);
                        break;
                    case 25: //Offices de tourisme
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_info));
                        claseNombre = this.getResources().getString(R.string.servicios_oficinas_de_turismo);
                        break;
                    default:
                        sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.poi_icono));
                        claseNombre = this.getResources().getString(R.string.punto_de_interes);
                }

                final HashMap<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("nombre", p.getTitle());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    attrs.put("descripcion", Html.fromHtml(p.getBody(), Html.FROM_HTML_MODE_LEGACY).toString());
                } else {
                    attrs.put("descripcion", Html.fromHtml(p.getBody()).toString());
                }
                attrs.put("nid", Integer.toString(p.getNid()));
                attrs.put("clase", Poi.class.getName());
                attrs.put("cat", claseNombre);

                if (type == 52) {
                    gr.getGraphics().add(new Graphic(puntoProyectado, attrs, sym));
                } else {
                    poisGr.getGraphics().add(new Graphic(puntoProyectado, attrs, sym));
                }
            }
            envelope = new Envelope(lonmax, latmin, lonmin, latmax, SpatialReferences.getWgs84());

            map.getGraphicsOverlays().add(gr);
            map.getGraphicsOverlays().add(poisGr);

            map.setViewpointGeometryAsync(new Envelope(lonmax, latmin, lonmin, latmax, SpatialReferences.getWgs84()), 60);
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
            // Log.d("Milog", "El objeto pulsado lleva un poi");
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            if ((cat == null) || (cat.equals("")))
                lblCategoria.setText(getString(R.string.mod_home__pois));
            else
                lblCategoria.setText(cat);

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RouteDetailActivity.this,
                            PoiDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
                    startActivity(intent);
                    callout.dismiss();
                }
            });
        } else if (clase.equals(Route.class.getName())) {
            // Poner las propiedades en el layout
            lblNombre.setText(nombre);
            lblCategoria.setText(getString(R.string.mod_home__rutas));

            // Escuchar el evento del click del bot�n
            btnCerrarDialogo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
//					Intent intent = new Intent(RouteDetailActivity.this,
//							RouteDetailActivity.class);
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
//					startActivity(intent);
                    callout.dismiss();
                }
            });
        }

        return view;
    }

    @Override
    public void seCerroComboCapas(Basemap basemap) {
        if (!this.basemap.equals(basemap)) {
            this.basemap = basemap;
            ArcGISMap mapArgis = new ArcGISMap(this.basemap);
            map.setMap(mapArgis);
            map.setViewpointGeometryAsync(envelope, 60);
        }
    }
}
