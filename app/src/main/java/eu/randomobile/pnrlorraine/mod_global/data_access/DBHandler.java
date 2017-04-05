package eu.randomobile.pnrlorraine.mod_global.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Page;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Vote;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;

public class DBHandler extends SQLiteOpenHelper {
    MainApp app;

    // <---------->__LOCAL_DATABASE_CONFIGURATION_<---------->
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "belfortmain.db";
    // <---------->__TABLE_ROUTES_CONFIGURATION____<---------->
    private static final String TABLE_ROUTES = "routes";
    public static final String COLUMN_ROUTE_NID = "id";
    public static final String COLUMN_ROUTE_TITLE = "title";
    public static final String COLUMN_ROUTE_CATEGORY_TID = "category_tid";
    public static final String COLUMN_ROUTE_CATEGORY_NAME = "category_name";
    public static final String COLUMN_ROUTE_BODY = "body";
    public static final String COLUMN_ROUTE_DIFFICULTY = "difficulty";
    public static final String COLUMN_ROUTE_DIFFICULTY_TID = "difficulty_tid";
    public static final String COLUMN_ROUTE_CIRCULAR = "circular";
    public static final String COLUMN_ROUTE_DISTANCE = "distanceMeters";
    public static final String COLUMN_ROUTE_LENGTH = "routeLengthMeters";
    public static final String COLUMN_ROUTE_TIME = "estimatedTime";
    public static final String COLUMN_ROUTE_SLOPE = "slope";
    public static final String COLUMN_ROUTE_MAIN_PICTURE = "mainImage";
    public static final String COLUMN_ROUTE_TRACK = "track";
    public static final String COLUMN_ROUTE_MAP_URL = "url_map";
    public static final String COLUMN_ROUTE_MAP_DIRECTORY = "local_directory_map";
    public static final String COLUMN_ROUTE_POIS_LIST = "pois";
    public static final String COLUMN_ROUTE_TAGS_LIST = "tags";
    // <---------->__TABLE_PAGES_CONFIGURATION____<---------->
    private static final String TABLE_PAGES = "pages";
    public static final String COLUMN_NID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    // <---------->__TABLE_MAPS_CONFIGURATION____<---------->
    private static final String TABLE_MAPS = "maps";
    public static final String COLUMN_ROUTE_ID = "id";
    public static final String COLUMN_MAP_LOCAL_DIRECTORY = "directory";
    // <---------->__CONFIGURATION_END____________<---------->

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, MainApp app) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        this.app = app;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_routes = "CREATE TABLE " + TABLE_ROUTES +
                "(" +
                COLUMN_ROUTE_NID + " INTEGER PRIMARY KEY, " +
                COLUMN_ROUTE_TITLE + " TEXT, " +
                COLUMN_ROUTE_CATEGORY_TID + " TEXT, " +
                COLUMN_ROUTE_CATEGORY_NAME + " TEXT, " +
                COLUMN_ROUTE_BODY + " TEXT, " +
                COLUMN_ROUTE_DIFFICULTY + " TEXT, " +
                COLUMN_ROUTE_DIFFICULTY_TID + " TEXT, " +
                COLUMN_ROUTE_CIRCULAR + " TEXT, " +
                COLUMN_ROUTE_DISTANCE + " TEXT, " +
                COLUMN_ROUTE_LENGTH + " TEXT, " +
                COLUMN_ROUTE_TIME + " TEXT, " +
                COLUMN_ROUTE_SLOPE + " TEXT, " +
                COLUMN_ROUTE_MAIN_PICTURE + " TEXT, " +
                COLUMN_ROUTE_TRACK + " TEXT, " +
                COLUMN_ROUTE_MAP_URL + " TEXT, " +
                COLUMN_ROUTE_MAP_DIRECTORY + " TEXT, " +
                COLUMN_ROUTE_POIS_LIST + " TEXT " +
                ");";
        db.execSQL(query_routes);

        String query_pages = "CREATE TABLE " + TABLE_PAGES +
                "(" +
                COLUMN_NID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_BODY + " TEXT " +
                ");";
        db.execSQL(query_pages);

        String query_maps = "CREATE TABLE " + TABLE_MAPS +
                "(" +
                COLUMN_ROUTE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_MAP_LOCAL_DIRECTORY + " TEXT " +
                ");";
        db.execSQL(query_maps);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (app.getNetStatus() != 0) {
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_ROUTES);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_PAGES);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_MAPS);

            onCreate(db);

        } else {
            // No hay red. No se pueden eliminar las tablas.
        }
    }

    // <-------------------->_ROUTES_METHODS_<-------------------->

    /**
     * This method receives a Route object and adds it to the local database if not exist or
     * replaces it in the local database if it already exists.
     *
     * @param route (Route) Route object
     */
    public void addOrReplaceRoute(Route route) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROUTE_NID, route.getNid());
        values.put(COLUMN_ROUTE_TITLE, route.getTitle());
        values.put(COLUMN_ROUTE_CATEGORY_TID, route.getCategory().getTid());
        values.put(COLUMN_ROUTE_CATEGORY_NAME, route.getCategory().getTid());
        values.put(COLUMN_ROUTE_BODY, route.getBody());

        // values.put(COLUMN_ROUTE_DIFFICULTY, route.getDifficulty().getTid());

        values.put(COLUMN_ROUTE_DIFFICULTY_TID, route.getDifficulty_tid());
        values.put(COLUMN_ROUTE_DISTANCE, route.getDistanceMeters());
        values.put(COLUMN_ROUTE_LENGTH, route.getRouteLengthMeters());
        values.put(COLUMN_ROUTE_TIME, route.getEstimatedTime());
        values.put(COLUMN_ROUTE_SLOPE, route.getSlope());
        values.put(COLUMN_ROUTE_MAIN_PICTURE, route.getMainImage());
        values.put(COLUMN_ROUTE_TRACK, route.getTrack());
        values.put(COLUMN_ROUTE_MAP_URL, route.getUrlMap());
        values.put(COLUMN_ROUTE_MAP_DIRECTORY, route.getLocalDirectoryMap());

        String poisList = "";
        for (int i = 0; i < route.getPois().size(); i++) {
            poisList += route.getPois().get(i).getNid() + ",";
        }

        Log.d("********************", poisList);

        values.put(COLUMN_ROUTE_POIS_LIST, poisList);

        /*
        route.getPois().get()
        values.put(COLUMN_ROUTE_POIS_LIST, );
        */

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_ROUTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Route identifier
     */
    public void deleteRoute(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_ROUTES + " WHERE " + COLUMN_NID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method take all Route objects in the local database, put them in an Especies ArrayList
     * and return it.
     *
     * @return (ArrayList<Route>) List of Routes.
     */
    public ArrayList<Route> getRouteList() {
        SQLiteDatabase db = getWritableDatabase();

        String[] columns = new String[]{COLUMN_ROUTE_NID, COLUMN_ROUTE_TITLE, COLUMN_ROUTE_CATEGORY_TID, COLUMN_ROUTE_CATEGORY_NAME, COLUMN_ROUTE_BODY,
                COLUMN_ROUTE_DIFFICULTY_TID, COLUMN_ROUTE_DISTANCE, COLUMN_ROUTE_LENGTH, COLUMN_ROUTE_TIME, COLUMN_ROUTE_SLOPE, COLUMN_ROUTE_MAIN_PICTURE,
                COLUMN_ROUTE_TRACK, COLUMN_ROUTE_MAP_URL, COLUMN_ROUTE_MAP_DIRECTORY, COLUMN_ROUTE_POIS_LIST};

        Cursor c = db.query(TABLE_ROUTES, columns, null, null, null, null, COLUMN_ROUTE_ID);

        int iNid = c.getColumnIndex(COLUMN_ROUTE_NID);
        int iTitle = c.getColumnIndex(COLUMN_ROUTE_TITLE);
        int iCategory_Tid = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_TID);
        int iCategory_Name = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_NAME);
        int iBody = c.getColumnIndex(COLUMN_ROUTE_BODY);
        //
        int iDifficulty_Tid = c.getColumnIndex(COLUMN_ROUTE_DIFFICULTY_TID);
        int iDistance = c.getColumnIndex(COLUMN_ROUTE_DISTANCE);
        int iLenght = c.getColumnIndex(COLUMN_ROUTE_LENGTH);
        int iTime = c.getColumnIndex(COLUMN_ROUTE_TIME);
        int iSlope = c.getColumnIndex(COLUMN_ROUTE_SLOPE);
        int iMain_Picture = c.getColumnIndex(COLUMN_ROUTE_MAIN_PICTURE);
        int iTrack = c.getColumnIndex(COLUMN_ROUTE_TRACK);
        int iMap_URL = c.getColumnIndex(COLUMN_ROUTE_MAP_URL);
        int iMap_Directory = c.getColumnIndex(COLUMN_ROUTE_MAP_DIRECTORY);
        int iPoi_List = c.getColumnIndex(COLUMN_ROUTE_POIS_LIST);

        Context ctx = app.getApplicationContext();

        ArrayList<Route> result = new ArrayList<Route>();

        int count_GR = 0;
        int count_PR = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Route item = new Route();

            item.setNid(c.getString(iNid));
            item.setTitle(c.getString(iTitle));

            RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
            routeCatTerm.setTid(c.getString(iCategory_Tid));
            routeCatTerm.setName(c.getString(iCategory_Name));
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

            item.setBody(c.getString(iBody));
            item.setDifficulty_tid(c.getString(iDifficulty_Tid));

            double distanceKMDouble = Double.valueOf(c.getString(iDistance));
            double distanceMDouble = distanceKMDouble * 1000;
            item.setDistanceMeters(distanceMDouble);

            item.setRouteLengthMeters(Double.valueOf(c.getString(iLenght)));
            item.setEstimatedTime(Double.valueOf(c.getString(iTime)));
            item.setSlope(Double.valueOf(c.getString(iSlope)));
            item.setMainImage(c.getString(iMain_Picture));
            item.setTrack(c.getString(iTrack));

            item.setUrlMap(c.getString(iMap_URL));
            item.setMapsLocalDirectory(c.getString(iMap_Directory));

            String routeListTemp = c.getString(iPoi_List);
            String routeList[] = routeListTemp.split(",");

            ArrayList<ResourcePoi> listaPois = new ArrayList<>();

            for (int i = 0; i < routeList.length; i++) {
                ResourcePoi poi = new ResourcePoi();

                String temp = routeList[i];
                Log.d("****_****", temp);

                if (!(temp.equals(""))) {
                    if (!(temp.equals(","))) {
                        Log.d("********************", temp);
                        poi.setNid(Integer.parseInt(temp));
                    }
                }

                listaPois.add(poi);
            }

            item.setPois(listaPois);

            result.add(item);
        }

        db.close();

        return result;
    }


    // <-------------------->_PAGES_METHODS_<-------------------->

    /**
     * This method receives a Page object and adds it to the local database if not exist or
     * replaces it in the local database if it already exists.
     *
     * @param page (Page) Page object
     */
    public void addOrReplacePage(Page page) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_NID, page.getNid());
        values.put(COLUMN_TITLE, page.getTitle());
        values.put(COLUMN_BODY, page.getBody());

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_PAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    /**
     * This method receives a Page identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Page identifier
     */
    public void deletePage(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_PAGES + " WHERE " + COLUMN_NID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method receives a Page identifier, find that identification in the local database and
     * returns the Page object whose identifier matches the identifier received (if any).
     *
     * @param id (int) Page identifier
     * @return (Page) Or null if not found
     */
    public Page getPageById(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PAGES + " WHERE " + COLUMN_NID + " = " + String.valueOf(id);
        Cursor c = db.rawQuery(selectQuery, null);

        // Identifiers
        int iNid = c.getColumnIndex(COLUMN_NID);
        int iTitle = c.getColumnIndex(COLUMN_TITLE);
        int iBody = c.getColumnIndex(COLUMN_BODY);

        Page result = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Page page = new Page(Integer.parseInt(c.getString(iNid)), c.getString(iTitle), c.getString(iBody));
            result = page;
        }

        db.close();

        return result;
    }

    // <-------------------->_MAPS_METHODS_<-------------------->

    /**
     * @param id                 (int) Route id.
     * @param mapsLocalDirectory (String) Local directory where route map is.
     */
    public void addOrReplaceMap(int id, String mapsLocalDirectory) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROUTE_ID, id);
        values.put(COLUMN_MAP_LOCAL_DIRECTORY, mapsLocalDirectory);

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_MAPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Route identifier
     */
    public void deleteMap(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_MAPS + " WHERE " + COLUMN_ROUTE_ID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * returns the route map's local directory whose identifier matches the identifier received (if any).
     *
     * @param id (int) Route identifier
     * @return (String) Or null if not found
     */
    public String getRouteMapsLocalDirectory(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MAPS + " WHERE " + COLUMN_ROUTE_ID + " = " + String.valueOf(id);
        Cursor c = db.rawQuery(selectQuery, null);

        // Identifiers
        int colum_ID = c.getColumnIndex(COLUMN_ROUTE_ID);
        int colum_Diretory = c.getColumnIndex(COLUMN_MAP_LOCAL_DIRECTORY);

        String result = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(colum_Diretory);
        }

        db.close();

        return result;
    }

    // <-------------------->_END_OF_FILE_<-------------------->
}
