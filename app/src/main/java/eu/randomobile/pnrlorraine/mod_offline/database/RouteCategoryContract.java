package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class RouteCategoryContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RouteCategoryEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RouteCategoryEntry.TABLE_NAME + "(" +
                    RouteCategoryEntry.COLUM_NAME_TID + " TEXT PRIMARY KEY," +
                    RouteCategoryEntry.COLUM_NAME_NAME + TEXT +
                    RouteCategoryEntry.COLUM_NAME_DESCRIPTION + TEXT +
                    RouteCategoryEntry.COLUM_NAME_COLOR + " TEXT)";
    private static final String INTEGER = " INTEGER, ";

    private RouteCategoryContract() {
    }

    static class RouteCategoryEntry implements BaseColumns {

        static final String TABLE_NAME = "routeCategory";

        static final String COLUM_NAME_TID = "tid";

        static final String COLUM_NAME_NAME = "name";

        static final String COLUM_NAME_DESCRIPTION = "description";

        static final String COLUM_NAME_COLOR = "color";

        private RouteCategoryEntry() {
        }

    }

}
