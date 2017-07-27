package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class VoteContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + VoteEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";

    private static final String INTEGER = " INTEGER, ";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VoteEntry.TABLE_NAME + "(" +
                    VoteEntry.COLUM_NAME_IDP + " TEXT PRIMARY KEY," +
                    VoteEntry.COLUM_NAME_NUMVOTE + INTEGER +
                    VoteEntry._ID + TEXT +
                    VoteEntry.COLUM_NAME_VALUE + " INTEGER)";

    private VoteContract() {
    }

    static class VoteEntry implements BaseColumns {

        static final String TABLE_NAME = "vote";

        static final String COLUM_NAME_NUMVOTE = "numvote";
        static final String COLUM_NAME_IDP = "idp";

        static final String COLUM_NAME_VALUE = "value";

        private VoteEntry() {
        }
    }

}
