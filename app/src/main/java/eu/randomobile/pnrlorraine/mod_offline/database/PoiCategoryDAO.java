package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public class PoiCategoryDAO {

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
    public PoiCategoryDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    static public PoiCategoryTerm getPoiCategoryStatic(String id, SQLiteDatabase dbin) {
        PoiCategoryTerm term = new PoiCategoryTerm();
        SQLiteDatabase db = dbin;

        String[] projection = {
                PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_NAME,
        };
        String selection = PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_TID + " = ?";
        String[] arg = {id};

        Cursor cursor = db.query(
                PoiCategoryContract.PoiCategoryEntry.TABLE_NAME,
                projection,
                selection,
                arg,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String tid = cursor.getString(cursor.getColumnIndexOrThrow(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_TID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_NAME));
            term = new PoiCategoryTerm(tid, name);
        }

        cursor.close();

        return term;
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
    }

    public void insertListCategory(List<PoiCategoryTerm> list) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (PoiCategoryTerm r : list) {
            ContentValues values = new ContentValues();
            values.put(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_NAME, r.getName());
            values.put(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_DESCRIPTION, r.getDescription());
            values.put(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_ICON, r.getIcon());
            update = db.update(PoiCategoryContract.PoiCategoryEntry.TABLE_NAME, values, PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_TID + " = ?", new String[]{String.valueOf(r.getTid())});
            if (update == 0) {
                values.put(PoiCategoryContract.PoiCategoryEntry.COLUM_NAME_TID, r.getTid());
                db.insert(PoiCategoryContract.PoiCategoryEntry.TABLE_NAME, null, values);
            }
        }
    }

    public PoiCategoryTerm getPoiCategory(String id) {
        return getPoiCategoryStatic(id, db);
    }
}
