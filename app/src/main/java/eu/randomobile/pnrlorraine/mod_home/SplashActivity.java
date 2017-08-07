package eu.randomobile.pnrlorraine.mod_home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Page;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Vote;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiCategoryDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceFileDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceLinkDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RouteCategoryDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RouteDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.VoteDAO;

public class SplashActivity extends Activity {
    private static ProgressBar progressBar;
    // Coordenadas GPS de Fuerteventura
    double lat = 47.6333;
    double lon = 6.8667;
    LocationManager mLocationManager;
    //
    //
    private MainApp app;

    //
    private PoiDAO poiDAO;
    private PoiCategoryDAO poiCategoryDAO;
    private RessourceFileDAO ressourceFileDAO;
    private RessourceLinkDAO ressourceLinkDAO;
    private RouteCategoryDAO routeCategoryDAO;
    private RouteDAO routeDAO;
    private VoteDAO voteDAO;
    private List<Poi> pois;
    private List<PoiCategoryTerm> poiCategoryTerms;
    private List<ResourceFile> resourceFiles;
    private List<ResourceLink> resourceLinks;
    private List<RouteCategoryTerm> routeCategoryTerms;
    private List<Route> routes;
    private List<Vote> votes;

    private Context ctx;
    private int m_permissionCode = 1414;

    //get gps position
    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
            }
        return bestLocation;
    }

    private void createGpsDisabledAlert(String message) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Activer GPS ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                showGpsOptions();
                            }
                        }
                );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                        finish();
                    }
                }
        );
        localBuilder.create().show();
    }

    private void showGpsOptions() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1818);
    }

    private void showNetworkOptions() {
        startActivityForResult(new Intent("android.settings.NETWORK_OPERATOR_SETTINGS"), 1);
    }

    private void checkPermissions(int code) {
        String[] permissions_required = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE};
        List permissions_not_granted_list = new ArrayList<>();
        for (String permission : permissions_required) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_not_granted_list.add(permission);
            }
        }
        if (permissions_not_granted_list.size() > 0) {
            String[] permissions = new String[permissions_not_granted_list.size()];
            permissions_not_granted_list.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, code);
        } else {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                createGpsDisabledAlert("L'application requiert le GPS, voulez-vous l'activer ?");
            } else {
                onCreateBis();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onCreateBis();
        } else {
            createGpsDisabledAlert("Le GPS n'est toujours pas actif, voulez-vous l'activer ?");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == m_permissionCode) {
            boolean ok = true;
            for (int i = 0; i < grantResults.length; ++i) {
                ok = ok && (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            if (ok) {
                mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    createGpsDisabledAlert("L'application requiert le GPS, voulez-vous l'activer ?");
                } else {
                    onCreateBis();
                }
            } else {
                Toast.makeText(this, "Error: required permissions not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home__activity_splash);

        checkPermissions(m_permissionCode);
    }

    private void downloadData() {

        final int[] progress = {0};

        routes = new ArrayList<>();
        routeCategoryTerms = new ArrayList<>();
        resourceFiles = new ArrayList<>();
        resourceLinks = new ArrayList<>();
        poiCategoryTerms = new ArrayList<>();
        pois = new ArrayList<>();
        votes = new ArrayList<>();

        routeDAO = new RouteDAO(getApplicationContext());
        poiDAO = new PoiDAO(getApplicationContext());
        poiCategoryDAO = new PoiCategoryDAO(getApplicationContext());
        routeCategoryDAO = new RouteCategoryDAO(getApplicationContext());
        ressourceFileDAO = new RessourceFileDAO(getApplicationContext());
        ressourceLinkDAO = new RessourceLinkDAO(getApplicationContext());
        voteDAO = new VoteDAO(getApplicationContext());

        updateLocalDatabasePages();


        HashMap<String, String> params = new HashMap<String, String>();

        params = new HashMap<>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        final MainApp app = (MainApp) getApplication();

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                int countPR = 0;
                int countGR = 0;

                try {
                    JSONArray arrayRes = new JSONArray(response);


                    for (int i = 0; i < arrayRes.length(); i++) {

                        JSONObject r = arrayRes.getJSONObject(i);
                        Route route = new Route();

                        String nid = r.optString("nid", "");

                        if (!"".equals(nid)) {
                            route.setNid(nid);
                            route.setTitle(r.optString("title"));
                            JSONObject cat = r.getJSONObject("cat");
                            if (cat != null) {
                                RouteCategoryTerm rct = new RouteCategoryTerm(cat.optString("tid", ""),
                                        cat.optString("title"), cat.optString("image", ""), nid);
                                routeCategoryTerms.add(rct);
                                if ("31".equals(cat.optString("tid", ""))) {
                                    int n = countPR % 5;
                                    switch (n) {
                                        case 0:
                                            route.setColor(ctx.getResources().getColor(R.color.pr1_route));
                                            break;
                                        case 1:
                                            route.setColor(ctx.getResources().getColor(R.color.pr2_route));
                                            break;
                                        case 2:
                                            route.setColor(ctx.getResources().getColor(R.color.pr3_route));
                                            break;
                                        case 3:
                                            route.setColor(ctx.getResources().getColor(R.color.pr4_route));
                                            break;
                                        case 4:
                                            route.setColor(ctx.getResources().getColor(R.color.pr5_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    countPR++;
                                } else if ("32".equals(cat.optString("tid", ""))) {
                                    int n = countGR % 2;
                                    switch (n) {
                                        case 0:
                                            route.setColor(ctx.getResources().getColor(R.color.gr1_route));
                                            break;
                                        case 1:
                                            route.setColor(ctx.getResources().getColor(R.color.gr2_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    countGR++;
                                } else {
                                    route.setColor(ctx.getResources().getColor(R.color.cr1_route));
                                }
                            }
                            route.setBody(r.optString("body", ""));
                            route.setDifficulty_tid(r.optString("difficulty", ""));
                            route.setDistanceMeters(r.optDouble("distance", -1L));
                            route.setRouteLengthMeters(r.optDouble("routedistance", -1L));
                            route.setEstimatedTime(r.optDouble("time", -1L));
                            route.setSlope(r.optDouble("alt_max"));
                            route.setMainImage(r.optString("image"));
                            route.setTrack(r.optString("geom", ""));
                            route.setUrlMap(r.optString("map_tpk", ""));


                            JSONArray images = r.optJSONArray("images");
                            if (images != null) {
                                for (int j = 0; j < images.length(); j++) {
                                    JSONObject im = images.getJSONObject(j);
                                    resourceFiles.add(generateRessourceFile(im, nid, "images"));
                                }
                            }


                            JSONArray videos = r.optJSONArray("videos");
                            if (videos != null) {
                                for (int j = 0; j < videos.length(); j++) {
                                    JSONObject vi = videos.getJSONObject(j);
                                    resourceFiles.add(generateRessourceFile(vi, nid, "videos"));
                                }
                            }

                            JSONArray audios = r.optJSONArray("audios");
                            if (audios != null) {
                                for (int j = 0; j < audios.length(); j++) {
                                    JSONObject im = audios.getJSONObject(j);
                                    resourceFiles.add(generateRessourceFile(im, nid, "audios"));
                                }
                            }

                            JSONArray links = r.optJSONArray("links");
                            if (links != null) {
                                for (int j = 0; j < links.length(); j++) {
                                    JSONObject li = links.getJSONObject(j);
                                    ResourceLink rl = new ResourceLink(li.optString("url"), li.optString("title"), nid);
                                    resourceLinks.add(rl);
                                }
                            }

                            JSONObject v = r.optJSONObject("rate");
                            if (votes != null) {
                                Vote vote = new Vote(v.optString("uid"),
                                        v.optInt("count", 0), v.optInt("results", 0), nid);

                                votes.add(vote);
                            }

                            JSONArray poisR = r.optJSONArray("pois");
                            final ArrayList<ResourcePoi> pois1 = new ArrayList<>();
                            for (int j = 0; j < poisR.length(); j++) {
                                JSONObject pj = poisR.optJSONObject(j);
                                ResourcePoi pTmp = new ResourcePoi();
                                pTmp.setNid(pj.optInt("nid"));
                                pois1.add(pTmp);
                            }

                            route.setPois(pois1);

                            routeCategoryDAO.insertListCategory(routeCategoryTerms);
                            routes.add(route);
                            progress[0] += 50 / arrayRes.length();
                            progressBar.incrementProgressBy(25);


                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                routeDAO.insertListRoute(routes);

            }
        }, params);

        progressBar.incrementProgressBy(25);

        params.clear();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(String response) {

                JSONArray arrayRes = null;
                try {
                    arrayRes = new JSONArray(response);

                    for (int i = 0; i < arrayRes.length(); i++) {

                        JSONObject p = arrayRes.getJSONObject(i);

                        Poi poi = new Poi();
                        poi.setNid(p.optString("nid"));
                        poi.setBody(p.optString("body"));
                        poi.setTitle(p.optString("title"));
                        poi.setDistanceMeters(p.optDouble("distance"));
                        poi.setCoordinates(new GeoPoint(p.optDouble("lat"), p.optDouble("lon"),
                                p.optDouble("altitude")));
                        poi.setMainImage(p.optString("image"));
                        poi.setNumber(p.optInt("number"));

                        JSONObject cat = p.getJSONObject("cat");
                        if (cat != null) {
                            poi.setCat(cat.optInt("tid", -1));
                        }

                        JSONArray images = p.optJSONArray("images");
                        if (images != null) {
                            for (int j = 0; j < images.length(); j++) {
                                JSONObject im = images.getJSONObject(j);
                                resourceFiles.add(generateRessourceFile(im, poi.getNid(), "images"));
                            }
                        }
                        JSONArray videos = p.optJSONArray("videos");
                        if (videos != null) {
                            for (int j = 0; j < videos.length(); j++) {
                                JSONObject vi = videos.getJSONObject(j);
                                resourceFiles.add(generateRessourceFile(vi, poi.getNid(), "videos"));
                            }
                        }

                        JSONArray audios = p.optJSONArray("audios");
                        if (audios != null) {
                            for (int j = 0; j < audios.length(); j++) {
                                JSONObject im = audios.getJSONObject(j);
                                resourceFiles.add(generateRessourceFile(im, poi.getNid(), "audios"));
                            }
                        }

                        JSONArray links = p.optJSONArray("links");
                        if (links != null) {
                            for (int j = 0; j < links.length(); j++) {
                                JSONObject li = links.getJSONObject(j);
                                ResourceLink rl = new ResourceLink(li.optString("url"), li.optString("title"), poi.getNid());
                                resourceLinks.add(rl);
                            }
                        }

                        JSONObject v = p.optJSONObject("rate");
                        if (votes != null) {
                            Vote vote = new Vote(v.optString("uid"),
                                    v.optInt("count", 0), v.optInt("resut", 0), poi.getNid());

                            votes.add(vote);
                        }
                        pois.add(poi);
                        progress[0] += 50 / arrayRes.length();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                poiCategoryDAO.insertListCategory(poiCategoryTerms);
                ressourceLinkDAO.insertListRessourceLink(resourceLinks);
                ressourceFileDAO.insertListRessourceFile(resourceFiles);
                voteDAO.insertListVote(votes);
                poiDAO.insertListPoi(pois);
                progressBar.incrementProgressBy(25);

            }
        }, params);
        progressBar.incrementProgressBy(25);

    }

   /* private void show() {
        Log.e("avant insert", "poi cat : " + poiCategoryTerms.size() + " route cat : " + routeCategoryTerms.size() +
                " rl : " + resourceLinks.size() + " rf : " + resourceFiles.size() + " vote : " + votes.size());


        List<Poi> p = poiDAO.getAllPois();
        for (Poi rt : p) {
            rt.setCategory(poiCategoryDAO.getPoiCategory(rt.getNid()));
            Log.e(" pois -->", rt.toString());
        }

        List<ResourcePoi> p2 = poiDAO.getResourcePois("123");
        for (ResourcePoi rt : p2) {
            //rt.setCategory(poiCategoryDAO.getPoiCategory(rt.getNid()));
            Log.e(" Rpois -->", rt.toString());
        }

        List<RouteCategoryTerm> v = routeCategoryDAO.getAllRouteCategory();
        for (RouteCategoryTerm rt : v) {
            Log.e(" rct -->", rt.toString());
        }

        List<Vote> v = voteDAO.getAllVote();
        for (Vote rt : v) {
            Log.e(" votes -->", rt.toString());
        }

        List<ResourceFile> rf = ressourceFileDAO.getAllResourceFiles();
        for (ResourceFile rt : rf) {
            Log.e(" rf -->", rt.toString());
        }

        List<ResourceLink> rl = ressourceLinkDAO.getAllResourceLink();
        for (ResourceLink rt : rl) {
            Log.e(" rl -->", rt.toString());
        }

        List<Route> r = routeDAO.getAllRoute();
        for (Route rt : r) {
            Log.e(" route -->", rt.toString());
        }
        //loadMainActivity();
    }*/

    private ResourceFile generateRessourceFile(JSONObject o, String nid, String type) {
        return new ResourceFile(
                o.optString("fid"), o.optString("name"),
                o.optString("url"), o.optString("body"),
                o.optString("title"), o.optString("copyright"), nid, type
        );
    }

    private void onCreateBis() {

        this.app = (MainApp) getApplication();

        app.init();

        ctx = getApplicationContext();

        Location location = getLastKnownLocation();
        if (location != null) {
            lon = location.getLongitude();
            lat = location.getLatitude();
        }

        progressBar = (ProgressBar) findViewById(R.id.splash_prograssBar);

        if (app.getNetStatus() != 0) {
            downloadData();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

            builder.setTitle(R.string.txt_sin_conexion);
            builder.setMessage(R.string.txt_caracteristicas_no_disponibles);
            builder.setPositiveButton("Acepter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadMainActivity();
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    loadMainActivity();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    loadMainActivity();
                }
            });

            builder.show();
        }
    }

    private void updateLocalDatabasePages() {
        try {
            AsyncHttpClient clientPages = new AsyncHttpClient();
            clientPages.get(SplashActivity.this, "http://belfort.randomobile.eu/api/routedata/pages/retrieve.json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String string) {
                    super.onSuccess(string);

                    Gson gson = new Gson();
                    Page[] listPages = gson.fromJson(string, Page[].class);

                    ArrayList<Page> pages = new ArrayList<Page>(Arrays.asList(listPages));

                    for (Page page : pages) {
                        app.getDBHandler().addOrReplacePage(page);
                    }
                    loadMainActivity();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ha fallado la descarga de datos P3", Toast.LENGTH_LONG).show();
            loadMainActivity();
        }
    }

    //

    public void loadMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}