package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public class RessourceFileDAO {

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
    public RessourceFileDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
    }

    public void insertListRessourceFile(List<ResourceFile> list) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (ResourceFile r : list) {
            ContentValues values = new ContentValues();
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_FILENAME, r.getFileName());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_URL, r.getFileUrl());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_BODY, r.getFileBody());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_MIME, r.getFileMime());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_TYPE, r.getFileType());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_TITLE, r.getFileTitle());
            values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_COPYRIGHT, r.getCopyright());
            update = db.update(RessourceFileContract.RessourceFileEntry.TABLE_NAME, values, RessourceFileContract.RessourceFileEntry.COLUM_NAME_FID + " = ?", new String[]{String.valueOf(r.getFid())});
            if (update == 0) {
                values.put(RessourceFileContract.RessourceFileEntry.COLUM_NAME_FID, r.getFid());
                db.insert(RessourceFileContract.RessourceFileEntry.TABLE_NAME, null, values);
            }
        }
    }

}
