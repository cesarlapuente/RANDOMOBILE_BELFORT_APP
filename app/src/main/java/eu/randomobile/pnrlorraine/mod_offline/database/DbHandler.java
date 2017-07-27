package eu.randomobile.pnrlorraine.mod_offline.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * RandomobileBelfort-Android
 * Created by Thibault Nougues on 21/06/2017.
 */

class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    /**
     * The database name
     */
    private static final String DATABASE_NAME = "Belbort.db";


    DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PoiCategoryContract.SQL_CREATE_ENTRIES);
        db.execSQL(RouteCategoryContract.SQL_CREATE_ENTRIES);
        db.execSQL(RessourceFileContract.SQL_CREATE_ENTRIES);
        db.execSQL(RessourceLinkContract.SQL_CREATE_ENTRIES);
        db.execSQL(VoteContract.SQL_CREATE_ENTRIES);
        db.execSQL(RouteContract.SQL_CREATE_ENTRIES);
        db.execSQL(PoiContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PoiCategoryContract.SQL_DELETE_ENTRIES);
        db.execSQL(RouteCategoryContract.SQL_DELETE_ENTRIES);
        db.execSQL(RessourceFileContract.SQL_DELETE_ENTRIES);
        db.execSQL(RessourceLinkContract.SQL_DELETE_ENTRIES);
        db.execSQL(VoteContract.SQL_DELETE_ENTRIES);
        db.execSQL(RouteContract.SQL_DELETE_ENTRIES);
        db.execSQL(PoiContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
