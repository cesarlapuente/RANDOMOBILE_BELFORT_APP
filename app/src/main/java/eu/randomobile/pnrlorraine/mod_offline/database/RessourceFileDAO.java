package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
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

    static public List<ResourceFile> getAllResourceFilesStatic(SQLiteDatabase dbin) {
        List<ResourceFile> resourceFiles = new ArrayList<>();

        SQLiteDatabase db = dbin;

        String[] projection = {
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_FID,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_URL,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_BODY,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_MIME,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_TYPE,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_TITLE,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_COPYRIGHT,
                RessourceFileContract.RessourceFileEntry.COLUM_NAME_FILENAME,
        };

        Cursor cursor = db.query(
                RessourceFileContract.RessourceFileEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String fid = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_FID));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_URL));
            String body = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_BODY));
            String mime = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_MIME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_TYPE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_TITLE));
            String copyright = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_COPYRIGHT));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(RessourceFileContract.RessourceFileEntry.COLUM_NAME_FILENAME));

            resourceFiles.add(new ResourceFile(fid, name, url, body, mime, type, title, copyright));
        }
        cursor.close();

        return resourceFiles;
    }

    static public ArrayList<ResourceFile> getListResourceFiles(String listId, SQLiteDatabase dbin) {
        ArrayList<ResourceFile> resourceFiles = new ArrayList<>();
        List<ResourceFile> tmp = getAllResourceFilesStatic(dbin);
        List<String> ids = new ArrayList<>(Arrays.asList(listId.split(",")));

        for (ResourceFile rf : tmp) {
            if (ids.contains(rf.getFid())) {
                resourceFiles.add(rf);
            }
        }
        return resourceFiles;
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

    public List<ResourceFile> getAllResourceFiles() {
        return getAllResourceFilesStatic(db);
    }

}
