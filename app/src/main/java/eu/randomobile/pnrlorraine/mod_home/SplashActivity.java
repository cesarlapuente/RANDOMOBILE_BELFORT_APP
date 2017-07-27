package eu.randomobile.pnrlorraine.mod_home;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteDifficultyTerm;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiCategoryDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceFileDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceLinkDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RouteCategoryDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RouteDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.VoteDAO;
import eu.randomobile.pnrlorraine.utils.JSONManager;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIME = 1 * 2000; // seconds
    private static ProgressBar progressBar;
    private static boolean splashActivado = true;
    private static int loadedComponents = 0;
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

    private static void cargarListaRutasOrdenadosDistancia(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String difTid, String searchTxt) {
        final HashMap<String, String> params = new HashMap<String, String>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        if (radio > 0) {
            params.put("radio", String.valueOf(radio));
        }
        if (num > 0) {
            params.put("num", String.valueOf(num));
        }
        if (pag > 0) {
            params.put("pag", String.valueOf(pag));
        }
        if (catTid != null && !catTid.equals("")) {
            params.put("cat", catTid);
        }
        if (difTid != null && !difTid.equals("")) {
            params.put("difficulty", difTid);
        }
        if (searchTxt != null && !searchTxt.equals("")) {
            params.put("search", searchTxt);
        }

        final MainApp app = (MainApp) application;

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {

                        ArrayList<Route> listaRutas = null;
                        Log.d("ROUTE: ", response);

                        if (response != null && !response.equals("")) {
                            listaRutas = fillRouteList(response, app);
                        }

                        if (listaRutas != null) {

                            /*for (Route route : app.getRoutesList()) {
                                Log.d("ForEach route sais:", " Route ID: " + route.getNid());
                                Log.d("ForEach route sais:", " Route Name: " + route.getTitle());



                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("nid", route.getNid());

                                listaRutas = new ArrayList<Route>();

                                app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
                                    public void onSuccess(String response) {
                                        if (response != null && !response.equals("")) {
                                            Route route = fillRoute(response);
                                            listaRutas.add(route);

                                            app.getDBHandler().addOrReplaceRoute(route);

                                        } else {
                                            Log.d("DetailedRoute:"," ERROR al descargar la ruta detallada.");
                                        }
                                    }

                                    public void onFailure(Throwable error) {
                                        Log.d("cargarRoute():", error.toString());
                                    }
                                }, params);


                                app.getDBHandler().addOrReplaceRoute(route);

                            }*/

                            progressBar.incrementProgressBy(25);

                            //app.setRoutesList(app.getDBHandler().getRouteList());
                            app.setRoutesList(listaRutas);

                            loadedComponents++;

                        } else {
                            app.setRoutesList(new ArrayList<Route>());
                        }
                    }

                    public void onFailure(Throwable error) {
                    Log.d("JmLog","FAIL connection cargarListaRutasOrdenadosDistancia");
                    }
                },
                params);
    }

    private static ArrayList<Route> fillRouteList(String response, final MainApp application) {
        Context ctx = application.getApplicationContext();

        ArrayList<Route> listaRutas = null;

        int count_GR = 0;
        int count_PR = 0;

        try {
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                listaRutas = new ArrayList<Route>();

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);
                    if (recObj != null) {
                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                            final JSONObject recDic = (JSONObject) recObj;

                            Log.d("JSON Object:", " String: " + recDic.toString());

                            String nid = recDic.getString("nid");
                            String title = recDic.getString("title");
                            String body = recDic.getString("body");

                            Log.d("fillRouteList() sais:", " BODY: " + body);

                            String distance = recDic.getString("distance");
                            String image = recDic.getString("image");
                            String geomWKT = recDic.getString("geom");
                            String url = recDic.getString("map_tpk");

                            Log.d("fillRouteList() sais:", "URL tpk: " + url);

                            final Route item = new Route();

                            item.setNid(nid);
                            item.setTitle(title);
                            item.setBody(body);
                            item.setTrack(geomWKT);
                            item.setUrlMap(url);

                            Log.d("fillRouteList() sais:", "URL en objeto Ruta: " + item.getUrlMap());

                            double distanceKMDouble = Double.valueOf(distance);
                            double distanceMDouble = distanceKMDouble * 1000;

                            item.setDistanceMeters(distanceMDouble);

                            if (!recDic.getString("time").equals("null"))
                                item.setEstimatedTime(recDic.getDouble("time"));
                            if (!recDic.getString("routedistance").equals("null"))
                                item.setRouteLengthMeters(recDic.getDouble("routedistance"));

                            Object objCat = recDic.get("cat");
                            if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicCat = (JSONObject) objCat;
                                String tid = dicCat.getString("tid");
                                String name = dicCat.getString("name");
                                RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
                                routeCatTerm.setTid(tid);
                                routeCatTerm.setName(name);
                                item.setCategory(routeCatTerm);
                                if ("31".equals(item.getCategory().getTid())) {
                                    int n = count_PR % 5;
                                    switch (n) {
                                        case 0:
                                            item.setColor(ctx.getResources().getColor(R.color.pr1_route));
                                            break;
                                        case 1:
                                            item.setColor(ctx.getResources().getColor(R.color.pr2_route));
                                            break;
                                        case 2:
                                            item.setColor(ctx.getResources().getColor(R.color.pr3_route));
                                            break;
                                        case 3:
                                            item.setColor(ctx.getResources().getColor(R.color.pr4_route));
                                            break;
                                        case 4:
                                            item.setColor(ctx.getResources().getColor(R.color.pr5_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    count_PR++;
                                } else if ("32".equals(item.getCategory().getTid())) {
                                    int n = count_GR % 2;
                                    switch (n) {
                                        case 0:
                                            item.setColor(ctx.getResources().getColor(R.color.gr1_route));
                                            break;
                                        case 1:
                                            item.setColor(ctx.getResources().getColor(R.color.gr2_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    count_GR++;
                                } else if ("CR".equals(item.getCategory().getTid())) {
                                    item.setColor(ctx.getResources().getColor(R.color.cr1_route));
                                }
                            }

                            Object objRate = recDic.get("rate");
                            if (objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicRate = (JSONObject) objRate;
                                String numVotosStr = dicRate.getString("count");
                                String resultsAvgStr = dicRate.getString("results");
                                int numVotos = 0;
                                int resultsAvg = 0;
                                if (numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")) {
                                    numVotos = (int) Float.parseFloat(numVotosStr);
                                }
                                if (resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")) {
                                    resultsAvg = (int) Float.parseFloat(resultsAvgStr);
                                }
                                Vote vote = new Vote();
                                vote.setEntity_id(nid);
                                vote.setNumVotes(numVotos);
                                vote.setValue(resultsAvg);
                                item.setVote(vote);
                            }

                            if (image != null && (image.equals("") || image.equals("null"))) {
                                item.setMainImage(null);
                            } else {
                                item.setMainImage(image);
                            }

                            if (!recDic.getString("difficulty").equals("null"))
                                item.setDifficulty_tid(recDic.getString("difficulty"));


                            //"pois":[{"title":"Cuevas","body":null,"lat":"28.403591432499","lon":"-14.155712170242","number":1,"type":"30","nid":"31","languageNone":"und","language":"es"},

                            /*
                            String pois = recDic.getString("pois");
                            Log.d("####################", " ############################################################ ");
                            Log.d("JSON Obj POIS:", "  " + pois);
                            */

                            try {
                                JSONArray arrayPois = recDic.getJSONArray("pois");
                                if (arrayPois != null) {
                                    ArrayList<ResourcePoi> arrayTemp = new ArrayList<>();

                                    for (int j = 0; j < arrayPois.length(); j++) {
                                        JSONObject objPOI = (JSONObject) arrayPois.get(j);

                                        String poi_nid = objPOI.getString("nid");
                                        String poi_title = objPOI.getString("title");
                                        String poi_body = objPOI.getString("body");

                                        Log.d("--------------------", " ------------------------------------------------------------ ");

                                        Log.d("Obj poi:", " Nid " + poi_nid);
                                        Log.d("Obj poi:", " Title " + poi_title);

                                        ResourcePoi poi = new ResourcePoi();
                                        poi.setNid(Integer.parseInt(poi_nid));
                                        poi.setTitle(poi_title);
                                        poi.setBody(poi_body);

                                        arrayTemp.add(poi);

                                        /*
                                        Log.d("--------------------", " ------------------------------------------------------------ ");
                                        Log.d("JSON Obj POIS:", "  " + arrayPois.get(j).toString());
                                        Log.d("--------------------", " ------------------------------------------------------------ ");
                                        */
                                    }

                                    item.setPois(arrayTemp);
                                }

                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("nid", item.getNid());
                                application.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {

                                    public void onSuccess(String response) {
                                        if (response != null && !response.equals("")) {
                                            try {
                                                JSONObject dicRes = new JSONObject(response);
                                                if (dicRes != null) {
                                                    if (!(dicRes.getString("alt_max").equals("null")) /*&& !(dicRes.getString("alt_min").equals("null"))*/)
                                                        item.setSlope(dicRes.getDouble("alt_max")/* - dicRes.getDouble("alt_min")*/);

                                                    Object objDif = dicRes.get("difficulty");

                                                    if (objDif != null && objDif.getClass().getName().equals(JSONObject.class.getName())) {
                                                        JSONObject dicDif = (JSONObject) objDif;
                                                        String tid = dicDif.getString("tid");
                                                        String name = dicDif.getString("name");
                                                        RouteDifficultyTerm routeDifTerm = new RouteDifficultyTerm();
                                                        routeDifTerm.setTid(tid);
                                                        routeDifTerm.setName(name);
                                                        item.setDifficulty(routeDifTerm);
                                                    }

                                                    ArrayList<ResourceFile> arrayResourceImages = new ArrayList<ResourceFile>();

                                                    Object objImages = dicRes.get("images");

                                                    if (objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())) {
                                                        JSONArray array = (JSONArray) objImages;

                                                        for (int i = 0; i < array.length(); i++) {
                                                            Object obj = array.get(i);

                                                            if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                                                                JSONObject dic = (JSONObject) obj;
                                                                String name = dic.getString("name");
                                                                String url = dic.getString("url");
                                                                ResourceFile rf = new ResourceFile();
                                                                rf.setFileName(name);
                                                                rf.setFileUrl(url);
                                                                rf.setFileTitle(JSONManager.getString(dic, "title"));
                                                                rf.setFileBody(JSONManager.getString(dic, "body"));
                                                                rf.setCopyright(JSONManager.getString(dic, "copyright"));
                                                                arrayResourceImages.add(rf);
                                                            }
                                                        }
                                                    }

                                                    item.setImages(arrayResourceImages);

                                                    ArrayList<ResourceFile> arrayResourceAudios = new ArrayList<ResourceFile>();

                                                    Object objAudios = dicRes.get("audios");

                                                    if (objAudios != null && objAudios.getClass().getName().equals(JSONArray.class.getName())) {
                                                        JSONArray array = (JSONArray) objAudios;

                                                        for (int i = 0; i < array.length(); i++) {
                                                            Object obj = array.get(i);

                                                            if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                                                                JSONObject dic = (JSONObject) obj;
                                                                String name = dic.getString("name");
                                                                String url = dic.getString("url");
                                                                ResourceFile rf = new ResourceFile();
                                                                rf.setFileName(name);
                                                                rf.setFileUrl(url);
                                                                arrayResourceAudios.add(rf);
                                                            }
                                                        }
                                                    }

                                                    item.setAudios(arrayResourceAudios);

                                                    ArrayList<ResourceFile> arrayResourceVideos = new ArrayList<ResourceFile>();

                                                    Object objVideos = dicRes.get("videos");

                                                    if (objVideos != null && objVideos.getClass().getName().equals(JSONArray.class.getName())) {
                                                        JSONArray array = (JSONArray) objVideos;

                                                        for (int i = 0; i < array.length(); i++) {
                                                            Object obj = array.get(i);

                                                            if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                                                                JSONObject dic = (JSONObject) obj;
                                                                String name = dic.getString("name");
                                                                String url = dic.getString("url");
                                                                ResourceFile rf = new ResourceFile();
                                                                rf.setFileName(name);
                                                                rf.setFileUrl(url);
                                                                arrayResourceVideos.add(rf);
                                                            }
                                                        }
                                                    }

                                                    item.setVideos(arrayResourceVideos);

                                                    ArrayList<ResourceLink> arrayResourceLinks = new ArrayList<ResourceLink>();
                                                    Object objLinks = dicRes.get("links");
                                                    if (objLinks != null && objLinks.getClass().getName().equals(JSONArray.class.getName())) {
                                                        JSONArray array = (JSONArray) objLinks;
                                                        for (int i = 0; i < array.length(); i++) {
                                                            Object obj = array.get(i);
                                                            if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                                                                JSONObject dic = (JSONObject) obj;
                                                                String name = dic.getString("name");
                                                                String url = dic.getString("url");
                                                                ResourceLink rl = new ResourceLink();
                                                                rl.setTitle(name);
                                                                rl.setUrl(url);
                                                                arrayResourceLinks.add(rl);
                                                            }
                                                        }
                                                    }
                                                    item.setEnlaces(arrayResourceLinks);

                                                } //dicRes != null

                                            } catch (Exception e) {
                                                Log.d("Milog", "Excepcion cargar route: " + e.toString());
                                            }


                                            application.getDBHandler().addOrReplaceRoute(item);

                                        } else {
                                            Log.d("DetailedRoute:", " ERROR al descargar la ruta detallada.");
                                        }
                                    }

                                    public void onFailure(Throwable error) {
                                        Log.d("cargarRoute():", error.toString());
                                    }
                                }, params);

                            } catch (Exception e) {
                                Log.d("--------------------", " ---------------------------v-ERROR-v------------------------------- ");
                                e.printStackTrace();
                                Log.d("--------------------", " ---------------------------^-ERROR-^------------------------------- ");
                            }


                            listaRutas.add(item);
                        }
                    }
                }
            }

        } catch (Exception e) {
            listaRutas = null;
        }

        return listaRutas;
    }

    private static void cargarRoute(Application application, String nid) {
        MainApp app = (MainApp) application;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nid", nid);

        app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Log.d("Milog", "Exito al cargar route: " + response);

                if (response != null && !response.equals("")) {
                    Route route = fillRoute(response);
                    // Informar al delegate
                    if (Route.routesInterface != null) {
                        if (route != null) {
                            //Route.routesInterface.seCargoRoute(route);
                        } else {
                            Log.d("Milog", "Antes de informar al delegate de un error");
                            //Route.routesInterface.producidoErrorAlCargarRoute("Error al cargar route");
                        }
                    }
                }
            }

            public void onFailure(Throwable error) {
                Log.d("cargarRoute():", error.toString());
            }
        }, params);

    }

    private static Route fillRoute(String response) {
        Route route = null;
        try {
            JSONObject dicRes = new JSONObject(response);
            if (dicRes != null) {
                String nid = dicRes.getString("nid");
                String title = dicRes.getString("title");
                String body = dicRes.getString("body");

                Log.d("fillRoute() sais:", " BODY: " + body);

                String geom = dicRes.getString("geom");
                String url_map = dicRes.getString("map");

                route = new Route();
                route.setNid(nid);
                route.setTitle(title);
                route.setBody(body);

                route.setUrlMap(url_map);
                if (!dicRes.getString("time").equals("null"))
                    route.setEstimatedTime(dicRes.getDouble("time"));

                if (!dicRes.getString("distance").equals("null"))
                    route.setRouteLengthMeters(dicRes.getDouble("distance"));

                if (!(dicRes.getString("alt_max").equals("null")) /*&& !(dicRes.getString("alt_min").equals("null"))*/)
                    route.setSlope(dicRes.getDouble("alt_max")/* - dicRes.getDouble("alt_min")*/);

                Object objCat = dicRes.get("type");

                if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicCat = (JSONObject) objCat;
                    String tid = dicCat.getString("tid");
                    String name = dicCat.getString("name");
                    RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
                    routeCatTerm.setTid(tid);
                    routeCatTerm.setName(name);
                    route.setCategory(routeCatTerm);
                }

                Object objDif = dicRes.get("difficulty");

                if (!dicRes.getString("difficulty").equals("null"))
                    route.setDifficulty_tid(dicRes.getString("difficulty"));

                /*if (objDif != null && objDif.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicDif = (JSONObject) objDif;
                    String tid = dicDif.getString("tid");
                    String name = dicDif.getString("name");
                    RouteDifficultyTerm routeDifTerm = new RouteDifficultyTerm();
                    routeDifTerm.setTid(tid);
                    routeDifTerm.setName(name);
                    route.setDifficulty(routeDifTerm);
                }*/

                route.setTrack(geom);

                ArrayList<ResourceFile> arrayResourceImages = new ArrayList<ResourceFile>();

                Object objImages = dicRes.get("images");

                if (objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objImages;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            rf.setFileTitle(JSONManager.getString(dic, "title"));
                            rf.setFileBody(JSONManager.getString(dic, "body"));
                            rf.setCopyright(JSONManager.getString(dic, "copyright"));
                            arrayResourceImages.add(rf);
                        }
                    }
                }

                route.setImages(arrayResourceImages);

                ArrayList<ResourceFile> arrayResourceAudios = new ArrayList<ResourceFile>();

                Object objAudios = dicRes.get("audios");

                if (objAudios != null && objAudios.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objAudios;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            arrayResourceAudios.add(rf);
                        }
                    }
                }

                route.setAudios(arrayResourceAudios);

                ArrayList<ResourceFile> arrayResourceVideos = new ArrayList<ResourceFile>();

                Object objVideos = dicRes.get("videos");

                if (objVideos != null && objVideos.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objVideos;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            arrayResourceVideos.add(rf);
                        }
                    }
                }

                route.setVideos(arrayResourceVideos);

                ArrayList<ResourceLink> arrayResourceLinks = new ArrayList<ResourceLink>();
                Object objLinks = dicRes.get("links");
                if (objLinks != null && objLinks.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objLinks;
                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);
                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceLink rl = new ResourceLink();
                            rl.setTitle(name);
                            rl.setUrl(url);
                            arrayResourceLinks.add(rl);
                        }
                    }
                }
                route.setEnlaces(arrayResourceLinks);

                ArrayList<ResourcePoi> arrayResourcePois = new ArrayList<ResourcePoi>();
                Object objPois = dicRes.get("pois");
                if (objPois != null && objPois.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objPois;
                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);
                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String bodyPoi = dic.getString("body");
                            String titlePoi = dic.getString("title");
                            int numberPoi = dic.getInt("number");
                            double longitudePoi = dic.getDouble("lon");
                            double latitudePoi = dic.getDouble("lat");
                            int typePoi = dic.getInt("type");
                            int nidPoi = dic.getInt("nid");
                            ResourcePoi rl = new ResourcePoi();
                            rl.setBody(bodyPoi);
                            rl.setTitle(titlePoi);
                            rl.setNumber(numberPoi);
                            rl.setLatitude(latitudePoi);
                            rl.setLongitude(longitudePoi);
                            rl.setType(typePoi);
                            rl.setNid(nidPoi);
                            arrayResourcePois.add(rl);
                        }
                    }
                }
                route.setPois(arrayResourcePois);

                Object objRate = dicRes.get("rate");
                if (objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicRate = (JSONObject) objRate;
                    String numVotosStr = dicRate.getString("count");
                    String resultsAvgStr = dicRate.getString("results");
                    int numVotos = 0;
                    int resultsAvg = 0;
                    if (numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")) {
                        numVotos = (int) Float.parseFloat(numVotosStr);
                    }
                    if (resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")) {
                        resultsAvg = (int) Float.parseFloat(resultsAvgStr);
                    }
                    Vote vote = new Vote();
                    vote.setEntity_id(nid);
                    vote.setNumVotes(numVotos);
                    vote.setValue(resultsAvg);
                    route.setVote(vote);
                }

            } //dicRes != null

        } catch (Exception e) {
            Log.d("Milog", "Excepcion cargar route: " + e.toString());
            route = null;
        }

        return route;
    }

    private static void cargarListaPoisOrdenadosDistancia(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String searchTxt) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        if (radio > 0) {
            params.put("radio", String.valueOf(radio));
        }
        if (num > 0) {
            params.put("num", String.valueOf(num));
        }
        if (pag > 0) {
            params.put("pag", String.valueOf(pag));
        }
        if (catTid != null && !catTid.equals("")) {
            params.put("cat", catTid);
        }
        if (searchTxt != null && !searchTxt.equals("")) {
            params.put("search", searchTxt);
        }

        Log.d("Milog", "Parametros enviados a poi/get_list_distance: " + params.toString());

        final MainApp app = (MainApp) application;

        app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {
                        ArrayList<Poi> listaPois = null;

                        if (response != null && !response.equals("")) {
                            listaPois = fillPoiList(response, application);
                        }

                        if (listaPois != null) {
                            app.setPoisList(listaPois);

                            for (Poi poi : listaPois) {
                                Log.d("ForEach poi sais:", " POI ID: " + poi.getNid());
                                Log.d("ForEach poi sais:", " POI Name: " + poi.getTitle());
                                Log.d("ForEach poi sais:", " POI Body: " + poi.getBody());

                                Poi poiDetail = poi;

                                // app.getDBHandler().addOrReplacePoi(poiDetail);
                            }

                            progressBar.incrementProgressBy(25);
                            // app.setPoisList(app.getDBHandler().getPoiList());
                            loadedComponents++;

                        } else {
                            app.setPoisList(new ArrayList<Poi>());
                        }
                    }

                    public void onFailure(Throwable error) {

                    }
                },
                params);
    }

    private static ArrayList<Poi> fillPoiList(String response, Application application) {
        Context ctx = application.getApplicationContext();
        ArrayList<Poi> listaPois = null;

        try {
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                if (arrayRes.length() > 0) {
                    listaPois = new ArrayList<Poi>();
                }

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);

                    if (recObj != null) {
                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject recDic = (JSONObject) recObj;

                            String nid = recDic.getString("nid");
                            String title = recDic.getString("title");
                            String body = recDic.getString("body");
                            String lat = recDic.getString("lat");
                            String lon = recDic.getString("lon");
                            String alt = recDic.getString("altitude");
                            String distance = recDic.getString("distance");
                            String image = recDic.getString("image");

                            Poi item = new Poi();

                            item.setNid(nid);
                            item.setTitle(title);
                            item.setBody(body);

                            double distanceKMDouble = Double.valueOf(distance);
                            double distanceMDouble = distanceKMDouble * 1000;
                            item.setDistanceMeters(distanceMDouble);

                            Object objCat = recDic.get("cat");

                            if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicCat = (JSONObject) objCat;
                                String tid = dicCat.getString("tid");
                                String name = dicCat.getString("name");
                                String icon = dicCat.getString("image");
                                PoiCategoryTerm poiCatTerm = new PoiCategoryTerm();
                                poiCatTerm.setTid(tid);
                                poiCatTerm.setName(name);
                                poiCatTerm.setIcon(icon);
                                item.setCategory(poiCatTerm);
                            }

                            Object objRate = recDic.get("rate");

                            if (objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicRate = (JSONObject) objRate;
                                String numVotosStr = dicRate.getString("count");
                                String resultsAvgStr = dicRate.getString("results");
                                int numVotos = 0;
                                int resultsAvg = 0;
                                if (numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")) {
                                    numVotos = (int) Float.parseFloat(numVotosStr);
                                }
                                if (resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")) {
                                    resultsAvg = (int) Float.parseFloat(resultsAvgStr);
                                }
                                Vote vote = new Vote();
                                vote.setEntity_id(nid);
                                vote.setNumVotes(numVotos);
                                vote.setValue(resultsAvg);
                                item.setVote(vote);
                            }

                            if (image != null && (image.equals("") || image.equals("null"))) {
                                item.setMainImage(null);

                            } else {
                                item.setMainImage(image);
                            }

                            GeoPoint gp = new GeoPoint();

                            if (lat != null && !lat.equals("") && !lat.equals("null")) {
                                gp.setLatitude(Double.parseDouble(lat));
                            }

                            if (lon != null && !lon.equals("") && !lon.equals("null")) {
                                gp.setLongitude(Double.parseDouble(lon));
                            }

                            if (alt != null && !alt.equals("") && !alt.equals("null")) {
                                gp.setAltitude(Double.parseDouble(alt));
                            }
                            item.setCoordinates(gp);

                            listaPois.add(item);
                        }
                    }
                }
            }

        } catch (Exception e) {
            listaPois = null;
        }

        return listaPois;
    }

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
        Log.e("thib", "createGpsDisabledAlert: ");
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
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("thib", "onActivityResult:  enable");
            onCreateBis();
        } else {
            Log.e("thib", "onActivityResult:  disable");
            createGpsDisabledAlert("Le GPS n'est toujours pas actif, voulez-vous l'activer ?");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home__activity_splash);

        ctx = getApplicationContext();

        this.app = (MainApp) getApplication();

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createGpsDisabledAlert("L'application requiert le GPS, voulez-vous l'activer ?");
        } else {
            onCreateBis();
        }
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


        HashMap<String, String> params = new HashMap<String, String>();

        /*params.put("nid", "124");

        app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                String[] t = response.split(",");
                for (String s : t){
                    Log.e("route_item", s);
                }
            }

            public void onFailure(Throwable error) {
                Log.d("cargarRoute():", error.toString());
            }
        }, params);*/

        params = new HashMap<>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        final MainApp app = (MainApp) getApplication();

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                /*String[] t = response.split(",");
                for (String s : t){
                    Log.e("--> " , s);
                }*/

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
                            progressBar.setProgress(progress[0]);

                        }
                        //JSONObject object = arrayRes.getJSONObject(i);
                        /*String[] t = object.toString().split(",");
                        for (String s : t) {
                            Log.e("++", s);
                        }*/
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                routeDAO.insertListRoute(routes);

                /*String[] t = response.split(",");
                for (String s : t){
                    Log.e("route_list_distance", s);
                }*/

            }
        }, params);


        Log.e("---", "---------------------------------------------------------------------------------------------------");

        params.clear();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

               /*String[] t = response.split(",");
                for (String s : t){
                    Log.e("pois liste", s);
                }*/

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
                        progressBar.setProgress(progress[0]);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                poiCategoryDAO.insertListCategory(poiCategoryTerms);
                ressourceLinkDAO.insertListRessourceLink(resourceLinks);
                ressourceFileDAO.insertListRessourceFile(resourceFiles);
                voteDAO.insertListVote(votes);
                poiDAO.insertListPoi(pois);

                show();


                /*List<Poi> r = poiDAO.getAllPois();
                for (Poi rt : r) {
                    Log.e("-->", rt.toString());
                }*/

                /*String[] t = response.split(",");
                for (String s : t){
                    Log.e("poi_list_distance", s);
                }*/
            }

            public void onFailure(Throwable error) {

            }
        }, params);


    }

    private void show() {
        /*Log.e("avant insert", "poi cat : " + poiCategoryTerms.size() + " route cat : " + routeCategoryTerms.size() +
                " rl : " + resourceLinks.size() + " rf : " + resourceFiles.size() + " vote : " + votes.size());*/


        /*List<Poi> p = poiDAO.getAllPois();
        for (Poi rt : p) {
            rt.setCategory(poiCategoryDAO.getPoiCategory(rt.getNid()));
            Log.e(" pois -->", rt.toString());
        }

        List<ResourcePoi> p2 = poiDAO.getResourcePois("123");
        for (ResourcePoi rt : p2) {
            //rt.setCategory(poiCategoryDAO.getPoiCategory(rt.getNid()));
            Log.e(" Rpois -->", rt.toString());
        }*/

       /* List<RouteCategoryTerm> v = routeCategoryDAO.getAllRouteCategory();
        for (RouteCategoryTerm rt : v) {
            Log.e(" rct -->", rt.toString());
        }*/

        /*List<Vote> v = voteDAO.getAllVote();
        for (Vote rt : v) {
            Log.e(" votes -->", rt.toString());
        }*/

        List<ResourceFile> rf = ressourceFileDAO.getAllResourceFiles();
        for (ResourceFile rt : rf) {
            Log.e(" rf -->", rt.toString());
        }

        /*List<ResourceLink> rl = ressourceLinkDAO.getAllResourceLink();
        for (ResourceLink rt : rl) {
            Log.e(" rl -->", rt.toString());
        }

        List<Route> r = routeDAO.getAllRoute();
        for (Route rt : r) {
            Log.e(" route -->", rt.toString());
        }*/
        loadMainActivity();
    }

    private ResourceFile generateRessourceFile(JSONObject o, String nid, String type) {
        return new ResourceFile(
                o.optString("fid"), o.optString("name"),
                o.optString("url"), o.optString("body"),
                o.optString("title"), o.optString("copyright"), nid, type
        );
    }

    private void onCreateBis() {

        Location location = getLastKnownLocation();
        lon = location.getLongitude();
        lat = location.getLatitude();

        progressBar = (ProgressBar) findViewById(R.id.splash_prograssBar);
        progressBar.setMax(100);

        if (app.getNetStatus() != 0) {
            Log.e("+++", "+++++++++++++++++++");
            downloadData();
            //updateLocalDatabase_Routes();
            //loadMainActivity();

        } /*else {
            try {
                app.setRoutesList(app.getDBHandler().getRouteList());

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Se ha producido un error al actualizar las rutas", Toast.LENGTH_LONG).show();
            }

            loadedComponents = 4;

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

            builder.setTitle(R.string.txt_sin_conexion);
            builder.setMessage(R.string.txt_caracteristicas_no_disponibles);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
        }*/
    }

    protected void onResume() {
        super.onResume();
        Log.e("thib", "onResume: ");
        //this.onRestart();

        splashActivado = true;

        /*
        try {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (splashActivado) {
                        // Abrir el home tras unos instantes
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                        SplashActivity.this.finish();

                        splashActivado = false;
                    }
                }
            }, SPLASH_TIME);

        } catch (Exception e) {

        }
        */
    }

    public void onBackPressed() {
        splashActivado = false;

        super.onBackPressed();
    }

    //

    private void updateLocalDatabase_Routes() {
        cargarListaRutasOrdenadosDistancia(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N?mero de elementos por p?gina
                0, // P?gina
                null, // Tid de la categor?a que queremos filtrar
                null, // Dificultad
                null // Texto a buscar
        );

        updateLocalDatabase_Pois();
    }

    private void updateLocalDatabase_Pois() {
        cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N�mero de elementos por p�gina
                0, // P�gina
                null, // Tid de la categor�a que queremos filtrar
                null // Texto a buscar
        );


        progressBar.incrementProgressBy(25);
        updateLocalDatabase_Pages();
    }

    private void updateLocalDatabase_Pages() {
        try {
            AsyncHttpClient client_pages = new AsyncHttpClient();
            client_pages.get(SplashActivity.this, "http://belfort.randomobile.eu/api/routedata/pages/retrieve.json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String string) {
                    super.onSuccess(string);

                    Gson gson = new Gson();
                    Page[] list_Pages = gson.fromJson(string, Page[].class);

                    ArrayList<Page> pages = new ArrayList<Page>(Arrays.asList(list_Pages));

                    for (Page page : pages) {
                        Log.d("updateLocalDatabase():", " Page Nid: " + page.getNid());
                        Log.d("updateLocalDatabase():", " Page Title: " + page.getTitle());
                        Log.d("updateLocalDatabase():", " Page Body: " + page.getBody());

                        app.getDBHandler().addOrReplacePage(page);
                    }

                    progressBar.incrementProgressBy(25);
                    loadedComponents++;
                    loadMainActivity();
                }

                @Override
                public void onFailure(Throwable throwable, String s) {
                    super.onFailure(throwable, s);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ha fallado la descarga de datos P3", Toast.LENGTH_LONG).show();

            progressBar.incrementProgressBy(25);
            loadedComponents++;
            loadMainActivity();
        }
    }

    //

    public void loadMainActivity() {
       // if (loadedComponents > 3) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            SplashActivity.this.finish();
        //}
    }

    //

    public class MyProgressBar extends ProgressBar {
        SplashActivity splashActivity;

        public MyProgressBar(Context context, SplashActivity splashActivity) {
            super(context);

            this.splashActivity = splashActivity;
        }

        @Override
        public void setProgress(int progress) {
            super.setProgress(progress);
            if (progress == this.getMax()) {
                splashActivity.loadMainActivity();
            }
        }
    }
}