package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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

    public RouteCategoryTerm getRouteCategory(String id) {
        RouteCategoryTerm term = new RouteCategoryTerm();
        db = mDbHandler.getWritableDatabase();

        String[] projection = {
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID,
        };
        String selection = RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP + " = ?";
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
            String idp = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP));
            term = new RouteCategoryTerm(tid, name, desc, idp);
        }

        cursor.close();

        return term;
    }

    public List<RouteCategoryTerm> getAllRouteCategory() {
        List<RouteCategoryTerm> categoryTerms = new ArrayList<>();

        SQLiteDatabase db = mDbHandler.getWritableDatabase();

        String[] projection = {
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION,
                RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME,
        };

        Cursor cursor = db.query(
                RouteCategoryContract.RouteCategoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String tid = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID));
            String idp = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP));
            String descr = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_DESCRIPTION));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_NAME));

            categoryTerms.add(new RouteCategoryTerm(tid, name, descr, idp));
        }
        cursor.close();

        return categoryTerms;
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
            values.put(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_TID, r.getTid());
            update = db.update(RouteCategoryContract.RouteCategoryEntry.TABLE_NAME, values, RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP + " = ?", new String[]{String.valueOf(r.getIdParent())});
            if (update == 0) {
                values.put(RouteCategoryContract.RouteCategoryEntry.COLUM_NAME_IDP, r.getIdParent());
                db.insert(RouteCategoryContract.RouteCategoryEntry.TABLE_NAME, null, values);
            }
        }
    }

}
