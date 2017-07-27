package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<ResourceLink> getAllResourceLink() {
        List<ResourceLink> resourceLinks = new ArrayList<>();

        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_URL,
                RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_TITLE,
                RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_IDP,
        };

        Cursor cursor = db.query(
                RessourceLinkContract.RessourceLinkEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String url = cursor.getString(cursor.getColumnIndexOrThrow(RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_URL));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_TITLE));
            String idp = cursor.getString(cursor.getColumnIndexOrThrow(RessourceLinkContract.RessourceLinkEntry.COLUM_NAME_IDP));

            resourceLinks.add(new ResourceLink(url, title, idp));
        }
        cursor.close();

        return resourceLinks;
    }

    public ArrayList<ResourceLink> getListResourceLinks(String listId) {
        ArrayList<ResourceLink> resourceLinks = new ArrayList<>();
        List<ResourceLink> tmp = getAllResourceLink();
        List<String> ids = new ArrayList<>(Arrays.asList(listId.split(",")));

        for (ResourceLink rf : tmp) {
            if (ids.contains(rf.getTitle())) {
                resourceLinks.add(rf);
            }
        }
        return resourceLinks;
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
