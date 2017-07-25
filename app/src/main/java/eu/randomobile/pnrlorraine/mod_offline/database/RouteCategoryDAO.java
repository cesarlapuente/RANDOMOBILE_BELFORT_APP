package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public class RouteCategoryDAO {

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
    public RouteCategoryDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    static public RouteCategoryTerm getRouteCategoryStatic(String id, SQLiteDatabase dbin) {
        RouteCategoryTerm term = new RouteCategoryTerm();
        SQLiteDatabase db = dbin;

        String[] projection = {
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION,
        };
        String selection = RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID + " = ?";
        String[] arg = {id};

        Cursor cursor = db.query(
                RouteCategoryContract.RouteCategoryEntry.TABLE_NAME,
                projection,
                selection,
                arg,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String tid = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION));
            term = new RouteCategoryTerm(tid, name, desc);
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

    public void insertListCategory(List<RouteCategoryTerm> list) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (RouteCategoryTerm r : list) {
            ContentValues values = new ContentValues();
            values.put(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME, r.getName());
            values.put(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION, r.getDescription());
            update = db.update(RouteCategoryContract.RouteCategoryEntry.TABLE_NAME, values, RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID + " = ?", new String[]{String.valueOf(r.getTid())});
            if (update == 0) {
                values.put(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID, r.getTid());
                db.insert(RouteCategoryContract.RouteCategoryEntry.TABLE_NAME, null, values);
            }
        }
    }

    public RouteCategoryTerm getRouteCategory(String id) {
        return getRouteCategoryStatic(id, db);
    }
}
