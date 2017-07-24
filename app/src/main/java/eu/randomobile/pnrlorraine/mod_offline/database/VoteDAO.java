package eu.randomobile.pnrlorraine.mod_offline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.randomobile.pnrlorraine.mod_global.model.Vote;

/**
 * RandomobileBelfort-Android
 * Created by Thibault on 21/07/2017.
 */

public class VoteDAO {

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
    public VoteDAO(Context context) {
        mDbHandler = new DbHandler(context);
    }

    /**
     * Method that close the connection to the database
     */
    public void destroy() {
        mDbHandler.close();
    }

    public void insertListVote(List<Vote> votes) {
        db = mDbHandler.getWritableDatabase();
        int update = -1;

        for (Vote v : votes) {
            ContentValues values = new ContentValues();
            values.put(VoteContract.VoteEntry.COLUM_NAME_NUMVOTE, v.getNumVotes());
            values.put(VoteContract.VoteEntry.COLUM_NAME_VALUE, v.getValue());
            update = db.update(VoteContract.VoteEntry.TABLE_NAME, values, VoteContract.VoteEntry._ID + " = ?", new String[]{String.valueOf(v.getEntity_id())});
            if (update == 0) {
                values.put(VoteContract.VoteEntry._ID, v.getEntity_id());
                db.insert(VoteContract.VoteEntry.TABLE_NAME, null, values);
            }
        }
    }
}
