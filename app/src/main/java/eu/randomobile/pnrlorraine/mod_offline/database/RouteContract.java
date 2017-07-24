package eu.randomobile.pnrlorraine.mod_offline.database;

import android.provider.BaseColumns;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 20/07/2017.
 */

public final class RouteContract {

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    private static final String TEXT = " TEXT, ";

    private static final String INTEGER = " INTEGER, ";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RouteEntry.TABLE_NAME + "(" +
                    RouteEntry.COLUM_NAME_NID + " TEXT PRIMARY KEY," +
                    RouteEntry.COLUM_NAME_TITLE + TEXT +
                    RouteEntry.COLUM_NAME_DIFFICULTY + TEXT +
                    RouteEntry.COLUM_NAME_DISTANCE + INTEGER +
                    RouteEntry.COLUM_NAME_GEOM + TEXT +
                    RouteEntry.COLUM_NAME_ROUTE_DISTANCE + INTEGER +
                    RouteEntry.COLUM_NAME_TIME + INTEGER +
                    RouteEntry.COLUM_NAME_POIS + TEXT +
                    RouteEntry.COLUM_NAME_CAT + TEXT +
                    RouteEntry.COLUM_NAME_RATE + TEXT +
                    RouteEntry.COLUM_NAME_MAP + TEXT +
                    RouteEntry.COLUM_NAME_IMAGE + TEXT +
                    RouteEntry.COLUM_NAME_IMAGES + TEXT +
                    RouteEntry.COLUM_NAME_SLOPE + INTEGER +
                    RouteEntry.COLUM_NAME_VIDEO + TEXT +
                    RouteEntry.COLUM_NAME_AUDIOS + TEXT +
                    RouteEntry.COLUM_NAME_ENLACE + TEXT +
                    RouteEntry.COLUM_NAME_BODY + " TEXT)";

    private RouteContract() {

    }

    static class RouteEntry implements BaseColumns {

        static final String TABLE_NAME = "routeList";

        static final String COLUM_NAME_NID = "nid";

        static final String COLUM_NAME_TITLE = "title";

        static final String COLUM_NAME_BODY = "body";

        static final String COLUM_NAME_DIFFICULTY = "difficulty";

        static final String COLUM_NAME_DISTANCE = "distance";

        static final String COLUM_NAME_GEOM = "geom";

        static final String COLUM_NAME_ROUTE_DISTANCE = "routeDistance";

        static final String COLUM_NAME_TIME = "time";

        static final String COLUM_NAME_POIS = "pois";

        static final String COLUM_NAME_CAT = "cat";

        static final String COLUM_NAME_RATE = "rate";

        static final String COLUM_NAME_IMAGE = "image";

        static final String COLUM_NAME_IMAGES = "images";

        static final String COLUM_NAME_MAP = "map";

        static final String COLUM_NAME_SLOPE = "slope";

        static final String COLUM_NAME_VIDEO = "video";

        static final String COLUM_NAME_AUDIOS = "audio";

        static final String COLUM_NAME_ENLACE = "enlace";

        static final String COLUM_NAME_COLOR = "color";

        private RouteEntry() {
        }
    }
}
