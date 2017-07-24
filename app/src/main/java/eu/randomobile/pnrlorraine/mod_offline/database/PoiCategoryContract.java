package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class PoiCategoryContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PoiCategoryEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PoiCategoryEntry.TABLE_NAME + "(" +
                    PoiCategoryEntry.COLUM_NAME_TID + " TEXT PRIMARY KEY," +
                    PoiCategoryEntry.COLUM_NAME_NAME + TEXT +
                    PoiCategoryEntry.COLUM_NAME_DESCRIPTION + TEXT +
                    PoiCategoryEntry.COLUM_NAME_ICON + " TEXT)";
    private static final String INTEGER = " INTEGER, ";

    private PoiCategoryContract() {
    }

    static class PoiCategoryEntry implements BaseColumns {

        static final String TABLE_NAME = "poiCategory";

        static final String COLUM_NAME_TID = "tid";

        static final String COLUM_NAME_NAME = "name";

        static final String COLUM_NAME_DESCRIPTION = "description";

        static final String COLUM_NAME_ICON = "icon";

        private PoiCategoryEntry() {
        }

    }

}
