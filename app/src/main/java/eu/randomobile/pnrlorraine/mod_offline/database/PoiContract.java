package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public final class PoiContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PoiEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";

    private static final String INTEGER = " INTEGER, ";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PoiEntry.TABLE_NAME + "(" +
                    PoiEntry.COLUM_NAME_NID + " TEXT PRIMARY KEY," +
                    PoiEntry.COLUM_NAME_TITLE + TEXT +
                    PoiEntry.COLUM_NAME_BODY + TEXT +
                    PoiEntry.COLUM_NAME_DISTANCE + INTEGER +
                    PoiEntry.COLUM_NAME_CAT + TEXT +
                    PoiEntry.COLUM_NAME_LON + INTEGER +
                    PoiEntry.COLUM_NAME_LAT + INTEGER +
                    PoiEntry.COLUM_NAME_IMAGE + TEXT +
                    PoiEntry.COLUM_NAME_IMAGES + TEXT +
                    PoiEntry.COLUM_NAME_VIDEO + TEXT +
                    PoiEntry.COLUM_NAME_AUDIOS + TEXT +
                    PoiEntry.COLUM_NAME_ENLACE + TEXT +
                    PoiEntry.COLUM_NAME_RATE + " TEXT)";

    private PoiContract() {

    }

    static class PoiEntry implements BaseColumns {

        static final String TABLE_NAME = "poiList";

        static final String COLUM_NAME_NID = "nid";

        static final String COLUM_NAME_TITLE = "title";

        static final String COLUM_NAME_BODY = "body";

        static final String COLUM_NAME_DISTANCE = "distance";

        static final String COLUM_NAME_CAT = "cat";

        static final String COLUM_NAME_LAT = "lat";

        static final String COLUM_NAME_LON = "lon";

        static final String COLUM_NAME_IMAGE = "image";

        static final String COLUM_NAME_IMAGES = "images";

        static final String COLUM_NAME_VIDEO = "video";

        static final String COLUM_NAME_AUDIOS = "audio";

        static final String COLUM_NAME_ENLACE = "enlace";

        static final String COLUM_NAME_RATE = "rate";

        private PoiEntry() {
        }

    }

}
