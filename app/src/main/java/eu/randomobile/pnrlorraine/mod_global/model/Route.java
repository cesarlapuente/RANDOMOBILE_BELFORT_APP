package eu.randomobile.pnrlorraine.mod_global.model;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteDifficultyTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.TagTerm;
import eu.randomobile.pnrlorraine.utils.JSONManager;

public class Route {
    private String nid;
    private String title;
    private RouteCategoryTerm category;
    private String body;
    private RouteDifficultyTerm difficulty;
    private String difficulty_tid;
    private boolean circular;
    private double distanceMeters;
    private double routeLengthMeters;
    private double estimatedTime;
    private double slope;
    private String mainImage;
    private String track;
    private String url_map;
    private String local_directory_map;
    private ArrayList<ResourceFile> images;
    private ArrayList<ResourceFile> videos;
    private ArrayList<ResourceFile> audios;
    private ArrayList<ResourceLink> enlaces;
    private ArrayList<ResourcePoi> pois;
    private ArrayList<TagTerm> tags;
    private Vote vote;
    private int color;

    public static RoutesInterface routesInterface;

    public static interface RoutesInterface {
        public void seCargoListaRoutes(ArrayList<Route> routes);

        public void producidoErrorAlCargarListaRoutes(String error);

        public void seCargoRoute(Route route);

        public void producidoErrorAlCargarRoute(String error);
    }

    public Route(){}

    public Route(String ROUTE_NID, String ROUTE_TITLE, String ROUTE_BODY, String ROUTE_DIFFICULTY_TID, double ROUTE_DISTANCE,
                 double ROUTE_LENGTH, double ROUTE_TIME, double ROUTE_SLOPE, String ROUTE_MAIN_PICTURE, String ROUTE_TRACK,
                 String ROUTE_MAP_URL, String ROUTE_MAP_DIRECTORY){
        this.nid = ROUTE_NID;
        this.title = ROUTE_TITLE;
        this.body = ROUTE_BODY;


    }

    public static void cargarListaRutasOrdenadosDistancia(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String difTid, String searchTxt) {

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
        if (difTid != null && !difTid.equals("")) {
            params.put("difficulty", difTid);
        }
        if (searchTxt != null && !searchTxt.equals("")) {
            params.put("search", searchTxt);
        }

        Log.d("Milog", "Parametros enviados a route/get_list_distance: " + params.toString());

        MainApp app = (MainApp) application;

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {

                        ArrayList<Route> listaRutas = null;
                        Log.d("Milog", "Respuesta de cargar rutas distance: " + response);

                        if (response != null && !response.equals("")) {

                            listaRutas = fillRouteList(response, application);
                        }

                        // Informar al delegate
                        if (Route.routesInterface != null) {
                            // Informar al delegate
                            if (listaRutas != null)
                                Route.routesInterface.seCargoListaRoutes(listaRutas);
                            else {
                                Log.d("Milog", "Antes de informar al delegate de un error");
                                Route.routesInterface.producidoErrorAlCargarListaRoutes("Error al cargar lista de routes");
                            }
                        }
                    }

                    public void onFailure(Throwable error) {
                        // Informar al delegate
                        if (Route.routesInterface != null) {
                            Log.d("Milog", "Antes de informar al delegate de un error Ordenados DIstancia: " + error.toString());
                            Route.routesInterface.producidoErrorAlCargarListaRoutes(error.toString());
                        }
                    }
                },
                params);
    }

    public int getColorForMap(Context ctx) {
        int result = Color.RED;
        switch (difficulty_tid) {
            case "22":
                result = ctx.getResources().getColor(R.color.black);
                break;
            case "16":
                result = ctx.getResources().getColor(R.color.blue_routes);
                break;
            case "17":
                result = ctx.getResources().getColor(R.color.red_routes);
                break;
            case "18":
                result = ctx.getResources().getColor(R.color.green_routes);
                break;
        }
        return result;
    }

    public static ArrayList<Route> fillRouteList(String response, Application application) {
        Context ctx = application.getApplicationContext();
        ArrayList<Route> listaRutas = null;

        int count_GR = 0;
        int count_PR = 0;

        try {
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                Log.d("fillRouteList() sais:", " Array devuelto. Contiene al menos 1 elemento.");

                listaRutas = new ArrayList<Route>();

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);

                    if (recObj != null) {
                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject recDic = (JSONObject) recObj;

                            String nid = recDic.getString("nid");
                            String title = recDic.getString("title");
                            String body = recDic.getString("body");

                            Log.d("fillRouteList() sais:", " BODY: " + body);

                            String distance = recDic.getString("distance");
                            String image = recDic.getString("image");
                            String geomWKT = recDic.getString("geom");
                            String url = recDic.getString("map_tpk");

                            Log.d("fillRouteList() sais:", "URL tpk: " + url);

                            Route item = new Route();

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
                                if (item.getCategory().getName().equals("PR")) {
                                    int n = count_PR % 3;
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
                                        default:
                                            break;
                                    }
                                    count_PR++;
                                } else if (item.getCategory().getName().equals("GR")) {
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
                                } else if (item.getCategory().getName().equals("CR")) {
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
                                item.difficulty_tid = recDic.getString("difficulty");

                            listaRutas.add(item);
                        }
                    }
                } //for

            } //arrayRes


        } catch (Exception e) {
            Log.d("Milog", "Excepcion en lista rutas: " + e.toString());
            listaRutas = null;
        }
        return listaRutas;
    }

    public static Route fillRoute(String response) {
        Route route = null;
        try {
            Log.d("JmLog","La valeur de la reponse :"+response);
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
                if (!(dicRes.getString("alt_max").equals("null")) && !(dicRes.getString("alt_min").equals("null")))
                    route.setSlope(dicRes.getDouble("alt_max") - dicRes.getDouble("alt_min"));
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
                if (objDif != null && objDif.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicDif = (JSONObject) objDif;
                    String tid = dicDif.getString("tid");
                    String name = dicDif.getString("name");
                    RouteDifficultyTerm routeDifTerm = new RouteDifficultyTerm();
                    routeDifTerm.setTid(tid);
                    routeDifTerm.setName(name);
                    route.setDifficulty(routeDifTerm);
                }
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
                            Log.d("JmLog", "l'url est :" + url);
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            //Log.d("Jmlog", "L'objet images : " + dicRes.get("images"));
                            rf.setFileTitle(JSONManager.getString(dic, "title"));
                            rf.setFileBody(JSONManager.getString(dic, "body"));
                            rf.setCopyright(JSONManager.getString(dic, "copyright"));
                            arrayResourceImages.add(rf);
                        }
                    }
                }
                Log.d("JmLog","Taille de tab  l'image : "+arrayResourceImages.size());
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

            }else {
                Log.d("JmLog","Connexion route échouée !");
            }
        } catch (Exception e) {
            Log.d("Milog", "Excepcion cargar route: " + e.toString());
            route = null;
        }
        return route;
    }

    public static void cargarRoute(Application application, String nid) {
        MainApp app = (MainApp) application;

     HashMap<String, String> params = new HashMap<String, String>();
        params.put("nid", nid);

        app.clienteDrupal.customMethodCallPost("route/get_item",
                new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {
                        Log.d("Milog", "Exito al cargar route: " + response);

                        if (response != null && !response.equals("")) {
                            Route route = fillRoute(response);
                            //Log.d("Jmlog","ROUTE VALUE IMAGES ARRAY :"+route.getImages()+" route nid :"+route.getNid());
                            // Informar al delegate
                            if (Route.routesInterface != null) {
                                if (route != null)
                                   Route.routesInterface.seCargoRoute(route);
                                else {
                                    Log.d("Milog", "Antes de informar al delegate de un error");
                                    Route.routesInterface.producidoErrorAlCargarRoute("Error al cargar route");
                                }
                            }
                        }
                    }

                    public void onFailure(Throwable error) {
                        // Informar al delegate
                        if (Route.routesInterface != null) {
                            Log.d("Milog",
                                    "Antes de informar al delegate de un error: " + error.toString());
                            Route.routesInterface
                                    .producidoErrorAlCargarRoute(error
                                            .toString());
                        }
                    }
                }, params);

    }

    // GETTERS AND SETTERS

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RouteCategoryTerm getCategory() {
        return category;
    }

    public void setCategory(RouteCategoryTerm category) {
        this.category = category;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrlMap() {
        return url_map;
    }

    public void setUrlMap(String url) {
        this.url_map = url;
    }

    public String getDifficulty_tid() {
        return difficulty_tid;
    }

    public void setDifficulty_tid(String difficulty) {
        this.difficulty_tid = difficulty;
    }

    public RouteDifficultyTerm getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(RouteDifficultyTerm difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCircular() {
        return circular;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public ArrayList<ResourceFile> getImages() {
        return this.images;
    }

    public void setImages(ArrayList<ResourceFile> images) {
        this.images = images;
    }

    public ArrayList<ResourceFile> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<ResourceFile> videos) {
        this.videos = videos;
    }

    public ArrayList<ResourceFile> getAudios() {
        return audios;
    }

    public void setAudios(ArrayList<ResourceFile> audios) {
        this.audios = audios;
    }

    public ArrayList<ResourceLink> getEnlaces() {
        return enlaces;
    }

    public void setEnlaces(ArrayList<ResourceLink> enlaces) {
        this.enlaces = enlaces;
    }

    public void setPois(ArrayList<ResourcePoi> pois) {
        this.pois = pois;
    }

    public ArrayList<ResourcePoi> getPois() {
        return pois;
    }

    public ArrayList<TagTerm> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagTerm> tags) {
        this.tags = tags;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public double getRouteLengthMeters() {
        return routeLengthMeters;
    }

    public void setRouteLengthMeters(double routeLenghtMeters) {
        this.routeLengthMeters = routeLenghtMeters;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public String getLocalDirectoryMap() {
        if (local_directory_map == null) {
            return "UNABLE TO GET MAP'S LOCAL DIRECTORY";
        } else {
            return local_directory_map;
        }
    }

    public void setMapsLocalDirectory(String local_directory_map) {
        this.local_directory_map = local_directory_map;
    }
}
