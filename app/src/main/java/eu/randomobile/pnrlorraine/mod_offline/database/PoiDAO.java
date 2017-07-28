package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;

/**
 * RandomobileBelfort-Android
 * Created by Thibault N on 21/07/2017.
 */

public class PoiDAO {
    /**
     * DbHandler to access the database
     */
    private DbHandler mDbHandler;

    /**
     * Database object
     */
    private SQLiteDatabase db;

    private RessourceLinkDAO linkDAO;
    private RessourceFileDAO fileDAO;
    private PoiDAO poiDAO;
    private PoiCategoryDAO categoryDAO;
    private VoteDAO voteDAO;

    /**
     * Constructor of the DAO
     */
    public PoiDAO(Context context) {
        mDbHandler = new DbHandler(context);
        /*linkDAO = new RessourceLinkDAO(context);
        fileDAO = new RessourceFileDAO(context);
        poiDAO = new PoiDAO(context);
        categoryDAO = new PoiCategoryDAO(context);
        voteDAO = new VoteDAO(context);*/
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
    }

    public Poi getPoi(String nid) {
        Poi poi = new Poi();

        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                PoiContract.PoiEntry.COLUM_NAME_NID,
                PoiContract.PoiEntry.COLUM_NAME_TITLE,
                PoiContract.PoiEntry.COLUM_NAME_CAT,
                PoiContract.PoiEntry.COLUM_NAME_BODY,
                PoiContract.PoiEntry.COLUM_NAME_DISTANCE,
                PoiContract.PoiEntry.COLUM_NAME_NUMBER,
                PoiContract.PoiEntry.COLUM_NAME_IMAGE,
                PoiContract.PoiEntry.COLUM_NAME_LON,
                PoiContract.PoiEntry.COLUM_NAME_LAT,
                PoiContract.PoiEntry.COLUM_NAME_ALT,
        };

        String selection = PoiContract.PoiEntry.COLUM_NAME_NID + " = ?";
        String[] arg = {nid};

        Cursor cursor = db.query(
                PoiContract.PoiEntry.TABLE_NAME,
                projection,
                selection,
                arg,
                null,
                null,
                null
        );

        if (cursor.moveToNext()) {
            poi.setNid(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_NID)));
            poi.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_TITLE)));
            poi.setCat(cursor.getInt(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_CAT)));
            poi.setBody(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_BODY)));
            poi.setDistanceMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_DISTANCE)));
            poi.setNumber(cursor.getInt(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_NUMBER)));
            poi.setMainImage(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_IMAGE)));
            Double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_LON));
            Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_LAT));
            Double alt = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_ALT));
            poi.setCoordinates(new GeoPoint(lat, lon, alt));
        }

        cursor.close();

        return poi;
    }

    private String getListRessourceFile(List<ResourceFile> list) {
        StringBuilder string = new StringBuilder();

        for (ResourceFile rf : list) {
            string.append(rf.getFid());
            string.append(",");
        }

        return string.toString();
    }

    private String getListRessourceLink(List<ResourceLink> list) {
        StringBuilder string = new StringBuilder();

        for (ResourceLink rf : list) {
            string.append(rf.getTitle());
            string.append(",");
        }

        return string.toString();
    }


    public void insertListPoi(List<Poi> pois) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (Poi p : pois) {


            ContentValues values = new ContentValues();
            values.put(PoiContract.PoiEntry.COLUM_NAME_TITLE, p.getTitle());
            values.put(PoiContract.PoiEntry.COLUM_NAME_BODY, p.getBody());
            values.put(PoiContract.PoiEntry.COLUM_NAME_DISTANCE, p.getDistanceMeters());
            values.put(PoiContract.PoiEntry.COLUM_NAME_CAT, p.getCat());
            values.put(PoiContract.PoiEntry.COLUM_NAME_LON, p.getCoordinates().getLongitude());
            values.put(PoiContract.PoiEntry.COLUM_NAME_LAT, p.getCoordinates().getLatitude());
            values.put(PoiContract.PoiEntry.COLUM_NAME_ALT, p.getCoordinates().getAltitude());
            values.put(PoiContract.PoiEntry.COLUM_NAME_IMAGE, p.getMainImage());
            /*values.put(PoiContract.PoiEntry.COLUM_NAME_IMAGES, getListRessourceFile(p.getImages()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_VIDEO, getListRessourceFile(p.getVideos()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_AUDIOS, getListRessourceFile(p.getAudios()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_ENLACE, getListRessourceLink(p.getEnlaces()));*/
            values.put(PoiContract.PoiEntry.COLUM_NAME_NUMBER, p.getNumber());
            update = db.update(PoiContract.PoiEntry.TABLE_NAME, values, PoiContract.PoiEntry.COLUM_NAME_NID + " = ?", new String[]{String.valueOf(p.getNid())});
            if (update == 0) {
                values.put(PoiContract.PoiEntry.COLUM_NAME_NID, p.getNid());
                db.insert(PoiContract.PoiEntry.TABLE_NAME, null, values);
            }
        }
    }

    public ArrayList<ResourcePoi> getResourcePois(String listId) {
        ArrayList<ResourcePoi> pois = new ArrayList<>();
        List<Poi> tmp = getAllPois();
        List<String> ids = new ArrayList<>(Arrays.asList(listId.split(",")));

        if (tmp != null && !tmp.isEmpty()) {
            for (Poi rf : tmp) {
                if (ids.contains(rf.getNid())) {
                    pois.add(new ResourcePoi(rf.getBody(), rf.getTitle(),
                            rf.getCoordinates().getLongitude(), rf.getCoordinates().getLatitude(),
                            rf.getCat(), Integer.valueOf(rf.getNid()), rf.getNumber()));
                }
            }
        }
        return pois;
    }

    public List<Poi> getAllPois() {
        List<Poi> pois = new ArrayList<>();
        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                PoiContract.PoiEntry.COLUM_NAME_NID,
                PoiContract.PoiEntry.COLUM_NAME_TITLE,
                PoiContract.PoiEntry.COLUM_NAME_CAT,
                PoiContract.PoiEntry.COLUM_NAME_BODY,
                PoiContract.PoiEntry.COLUM_NAME_DISTANCE,
                PoiContract.PoiEntry.COLUM_NAME_NUMBER,
                PoiContract.PoiEntry.COLUM_NAME_IMAGE,
                PoiContract.PoiEntry.COLUM_NAME_LON,
                PoiContract.PoiEntry.COLUM_NAME_LAT,
                PoiContract.PoiEntry.COLUM_NAME_ALT,
        };

        Cursor cursor = db.query(
                PoiContract.PoiEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Poi p = new Poi();
            p.setNid(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_NID)));
            p.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_TITLE)));
            p.setCat(cursor.getInt(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_CAT)));
            p.setBody(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_BODY)));
            p.setDistanceMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_DISTANCE)));
            p.setNumber(cursor.getInt(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_NUMBER)));
            p.setMainImage(cursor.getString(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_IMAGE)));
            Double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_LON));
            Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_LAT));
            Double alt = cursor.getDouble(cursor.getColumnIndexOrThrow(PoiContract.PoiEntry.COLUM_NAME_ALT));
            p.setCoordinates(new GeoPoint(lat, lon, alt));
            pois.add(p);
        }
        cursor.close();

        return pois;
    }
}
