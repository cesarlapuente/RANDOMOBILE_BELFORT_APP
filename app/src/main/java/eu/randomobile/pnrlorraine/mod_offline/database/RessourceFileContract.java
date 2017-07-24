package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class RessourceFileContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RessourceFileEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RessourceFileEntry.TABLE_NAME + "(" +
                    RessourceFileEntry.COLUM_NAME_FID + " TEXT PRIMARY KEY," +
                    RessourceFileEntry.COLUM_NAME_FILENAME + TEXT +
                    RessourceFileEntry.COLUM_NAME_URL + TEXT +
                    RessourceFileEntry.COLUM_NAME_BODY + TEXT +
                    RessourceFileEntry.COLUM_NAME_MIME + TEXT +
                    RessourceFileEntry.COLUM_NAME_TYPE + TEXT +
                    RessourceFileEntry.COLUM_NAME_TITLE + TEXT +
                    RessourceFileEntry.COLUM_NAME_COPYRIGHT + " TEXT)";
    private static final String INTEGER = " INTEGER, ";

    private RessourceFileContract() {
    }

    static class RessourceFileEntry implements BaseColumns {

        static final String TABLE_NAME = "ressourceFile";

        static final String COLUM_NAME_FID = "fid";

        static final String COLUM_NAME_FILENAME = "filename";

        static final String COLUM_NAME_URL = "url";

        static final String COLUM_NAME_BODY = "body";

        static final String COLUM_NAME_MIME = "mime";

        static final String COLUM_NAME_TYPE = "type";

        static final String COLUM_NAME_TITLE = "title";

        static final String COLUM_NAME_COPYRIGHT = "copyright";

        private RessourceFileEntry() {
        }

    }
}
