package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
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

    /**
     * Constructor of the DAO
     */
    public PoiDAO(Context context) {
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
        }

        return string.toString();
    }

    private String getListRessourceLink(List<ResourceLink> list) {
        StringBuilder string = new StringBuilder();

        for (ResourceLink rf : list) {
            string.append(rf.getTitle());
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
            values.put(PoiContract.PoiEntry.COLUM_NAME_CAT, p.getCategory().getTid());
            values.put(PoiContract.PoiEntry.COLUM_NAME_LON, p.getCoordinates().getLongitude());
            values.put(PoiContract.PoiEntry.COLUM_NAME_LAT, p.getCoordinates().getLatitude());
            values.put(PoiContract.PoiEntry.COLUM_NAME_IMAGE, p.getMainImage());
            values.put(PoiContract.PoiEntry.COLUM_NAME_IMAGES, getListRessourceFile(p.getImages()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_VIDEO, getListRessourceFile(p.getVideos()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_AUDIOS, getListRessourceFile(p.getAudios()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_ENLACE, getListRessourceLink(p.getEnlaces()));
            values.put(PoiContract.PoiEntry.COLUM_NAME_RATE, p.getVote().getEntity_id());
            update = db.update(PoiContract.PoiEntry.TABLE_NAME, values, PoiContract.PoiEntry.COLUM_NAME_NID + " = ?", new String[]{String.valueOf(p.getNid())});
            if (update == 0) {
                values.put(PoiContract.PoiEntry.COLUM_NAME_NID, p.getNid());
                db.insert(PoiContract.PoiEntry.TABLE_NAME, null, values);
            }
        }
    }
}
