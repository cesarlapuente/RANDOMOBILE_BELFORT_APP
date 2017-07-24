package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public class RessourceLinkDAO {

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
    public RessourceLinkDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
    }

    public void insertListRessourceLink(List<ResourceLink> links) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (ResourceLink r : links) {
            ContentValues values = new ContentValues();
            values.put(RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_URL, r.getUrl());
            update = db.update(RessourceLinkContract.RessourceLinkEntry.TABLE_NAME, values, RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_TITLE + " = ?", new String[]{String.valueOf(r.getTitle())});
            if (update == 0) {
                values.put(RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_TITLE, r.getTitle());
                db.insert(RessourceLinkContract.RessourceLinkEntry.TABLE_NAME, null, values);
            }
        }
    }
}
