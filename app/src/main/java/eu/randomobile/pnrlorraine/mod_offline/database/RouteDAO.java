package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;
import eu.randomobile.pnrlorraine.mod_global.model.ResourcePoi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 20/07/2017.
 */

public class RouteDAO {

    /**
     * DbHandler to access the database
     */
    private DbHandler mDbHandler;

    /**
     * Database object
     */
    private SQLiteDatabase db;

    /**
     * Constructor of the DAO
     */
    public RouteDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
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


    public void insertListRoute(List<Route> routes) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (Route r : routes) {

            StringBuilder pois = new StringBuilder();

            for (ResourcePoi rp : r.getPois()) {
                pois.append(String.valueOf(rp.getNid()));
                pois.append(",");
            }


            ContentValues values = new ContentValues();
            values.put(RouteContract.RouteEntry.COLUM_NAME_TITLE, r.getTitle());
            values.put(RouteContract.RouteEntry.COLUM_NAME_BODY, r.getBody());
            values.put(RouteContract.RouteEntry.COLUM_NAME_DIFFICULTY, r.getDifficulty_tid());
            values.put(RouteContract.RouteEntry.COLUM_NAME_DISTANCE, r.getDistanceMeters());
            values.put(RouteContract.RouteEntry.COLUM_NAME_GEOM, r.getTrack());
            values.put(RouteContract.RouteEntry.COLUM_NAME_ROUTE_DISTANCE, r.getRouteLengthMeters());
            values.put(RouteContract.RouteEntry.COLUM_NAME_TIME, r.getEstimatedTime());
            values.put(RouteContract.RouteEntry.COLUM_NAME_POIS, pois.toString());
            //values.put(RouteContract.RouteEntry.COLUM_NAME_CAT, r.getCategory().getTid());
            //values.put(RouteContract.RouteEntry.COLUM_NAME_NUMBER, r.getVote().getEntity_id());
            values.put(RouteContract.RouteEntry.COLUM_NAME_IMAGE, r.getMainImage());
            //values.put(RouteContract.RouteEntry.COLUM_NAME_IMAGES, getListRessourceFile(r.getImages()));
            values.put(RouteContract.RouteEntry.COLUM_NAME_MAP, r.getUrlMap());
            values.put(RouteContract.RouteEntry.COLUM_NAME_SLOPE, r.getSlope());
            values.put(RouteContract.RouteEntry.COLUM_NAME_COLOR, r.getColor());
            //values.put(RouteContract.RouteEntry.COLUM_NAME_VIDEO, getListRessourceFile(r.getVideos()));
            //values.put(RouteContract.RouteEntry.COLUM_NAME_AUDIOS, getListRessourceFile(r.getAudios()));
            //values.put(RouteContract.RouteEntry.COLUM_NAME_ENLACE, getListRessourceLink(r.getEnlaces()));
            update = db.update(RouteContract.RouteEntry.TABLE_NAME, values, RouteContract.RouteEntry.COLUM_NAME_NID + " = ?", new String[]{String.valueOf(r.getNid())});
            if (update == 0) {
                values.put(RouteContract.RouteEntry.COLUM_NAME_NID, r.getNid());
                db.insert(RouteContract.RouteEntry.TABLE_NAME, null, values);
            }
        }
    }

    public Route getRoute(String nid) {
        Route r = new Route();
        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                RouteContract.RouteEntry.COLUM_NAME_NID,
                RouteContract.RouteEntry.COLUM_NAME_TITLE,
                RouteContract.RouteEntry.COLUM_NAME_BODY,
                RouteContract.RouteEntry.COLUM_NAME_DIFFICULTY,
                RouteContract.RouteEntry.COLUM_NAME_DISTANCE,
                RouteContract.RouteEntry.COLUM_NAME_GEOM,
                RouteContract.RouteEntry.COLUM_NAME_ROUTE_DISTANCE,
                RouteContract.RouteEntry.COLUM_NAME_TIME,
                RouteContract.RouteEntry.COLUM_NAME_POIS,
                RouteContract.RouteEntry.COLUM_NAME_COLOR,
                //RouteContract.RouteEntry.COLUM_NAME_CAT,
                //RouteContract.RouteEntry.COLUM_NAME_NUMBER,
                RouteContract.RouteEntry.COLUM_NAME_IMAGE,
                //RouteContract.RouteEntry.COLUM_NAME_IMAGES,
                RouteContract.RouteEntry.COLUM_NAME_MAP,
                RouteContract.RouteEntry.COLUM_NAME_SLOPE,
                /*RouteContract.RouteEntry.COLUM_NAME_VIDEO,
                RouteContract.RouteEntry.COLUM_NAME_AUDIOS,
                RouteContract.RouteEntry.COLUM_NAME_ENLACE,*/
        };

        String selection = RouteContract.RouteEntry.COLUM_NAME_NID + " = ?";
        String[] arg = {nid};

        Cursor cursor = db.query(
                RouteContract.RouteEntry.TABLE_NAME,
                projection,
                selection,
                arg,
                null,
                null,
                null
        );


        if (cursor.moveToNext()) {
            r.setNid(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_NID)));
            r.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_TITLE)));
            r.setBody(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_BODY)));
            r.setDifficulty_tid(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_DIFFICULTY)));
            r.setDistanceMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_DISTANCE)));
            r.setTrack(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_GEOM)));
            r.setRouteLengthMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_ROUTE_DISTANCE)));
            r.setEstimatedTime(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_TIME)));
            r.setIdsPois(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_POIS)));
            r.setMainImage(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_IMAGE)));
            r.setUrlMap(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_MAP)));
            r.setSlope(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_SLOPE)));
            r.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_COLOR)));
        }

        cursor.close();

        return r;
    }

    public ArrayList<Route> getAllRoute() {
        ArrayList<Route> routes = new ArrayList<>();
        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                RouteContract.RouteEntry.COLUM_NAME_NID,
                RouteContract.RouteEntry.COLUM_NAME_TITLE,
                RouteContract.RouteEntry.COLUM_NAME_BODY,
                RouteContract.RouteEntry.COLUM_NAME_DIFFICULTY,
                RouteContract.RouteEntry.COLUM_NAME_DISTANCE,
                RouteContract.RouteEntry.COLUM_NAME_GEOM,
                RouteContract.RouteEntry.COLUM_NAME_ROUTE_DISTANCE,
                RouteContract.RouteEntry.COLUM_NAME_TIME,
                RouteContract.RouteEntry.COLUM_NAME_POIS,
                RouteContract.RouteEntry.COLUM_NAME_COLOR,
                //RouteContract.RouteEntry.COLUM_NAME_CAT,
                //RouteContract.RouteEntry.COLUM_NAME_NUMBER,
                RouteContract.RouteEntry.COLUM_NAME_IMAGE,
                //RouteContract.RouteEntry.COLUM_NAME_IMAGES,
                RouteContract.RouteEntry.COLUM_NAME_MAP,
                RouteContract.RouteEntry.COLUM_NAME_SLOPE,
                /*RouteContract.RouteEntry.COLUM_NAME_VIDEO,
                RouteContract.RouteEntry.COLUM_NAME_AUDIOS,
                RouteContract.RouteEntry.COLUM_NAME_ENLACE,*/
        };

        Cursor cursor = db.query(
                RouteContract.RouteEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Route r = new Route();
            r.setNid(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_NID)));
            r.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_TITLE)));
            r.setBody(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_BODY)));
            r.setDifficulty_tid(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_DIFFICULTY)));
            r.setDistanceMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_DISTANCE)));
            r.setTrack(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_GEOM)));
            r.setRouteLengthMeters(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_ROUTE_DISTANCE)));
            r.setEstimatedTime(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_TIME)));
            r.setIdsPois(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_POIS)));
            //r.setCategory(RouteCategoryDAO.getRouteCategoryStatic(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_CAT)), db));
            //r.setVote(VoteDAO.getVoteStatic(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_NUMBER)), db));
            r.setMainImage(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_IMAGE)));
            //r.setImages(RessourceFileDAO.getListResourceFiles(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_IMAGES)), db));
            r.setUrlMap(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_MAP)));
            r.setSlope(cursor.getDouble(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_SLOPE)));
            r.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_COLOR)));
            //r.setVideos(RessourceFileDAO.getListResourceFiles(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_VIDEO)), db));
            //r.setAudios(RessourceFileDAO.getListResourceFiles(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_AUDIOS)), db));
            //r.setEnlaces(RessourceLinkDAO.getListResourceLinks(cursor.getString(cursor.getColumnIndexOrThrow(RouteContract.RouteEntry.COLUM_NAME_ENLACE)), db));

            routes.add(r);
        }
        cursor.close();


        return routes;
    }

}
