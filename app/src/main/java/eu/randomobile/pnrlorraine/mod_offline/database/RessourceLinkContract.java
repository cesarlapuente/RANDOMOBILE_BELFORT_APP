package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class RessourceLinkContract {


    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RessourceLinkEntry.TABLE_NAME;
    private static final String TEXT = " TEXT, ";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RessourceLinkEntry.TABLE_NAME + "(" +
                    RessourceLinkEntry.COLUM_NAME_TITLE + " TEXT PRIMARY KEY," +
                    RessourceLinkEntry.COLUM_NAME_IDP + TEXT +
                    RessourceLinkEntry.COLUM_NAME_URL + " TEXT)";
    private static final String INTEGER = " INTEGER, ";

    private RessourceLinkContract() {
    }

    static class RessourceLinkEntry implements BaseColumns {

        static final String TABLE_NAME = "ressourceLink";

        static final String COLUM_NAME_IDP = "idp";

        static final String COLUM_NAME_TITLE = "title";

        static final String COLUM_NAME_URL = "url";

        private RessourceLinkEntry() {
        }

    }

}
