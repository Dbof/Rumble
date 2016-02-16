package org.disrupted.rumble.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.disrupted.rumble.database.events.HiddenStatusInsertedEvent;
import org.disrupted.rumble.database.events.StatusInsertedEvent;
import org.disrupted.rumble.database.objects.HiddenStatus;
import org.disrupted.rumble.network.protocols.events.HiddenStatusReceived;
import org.disrupted.rumble.util.Log;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by davide on 12/02/16.
 */
public class HiddenStatusDatabase extends Database {
    private static final String TAG = "HiddenStatusDatabase";

    public static final String TABLE_NAME = "hidden_status";
    public static final String ID = "_id";
    public static final String GROUP_ID = "gid";  // the name of the group it belongs to
    public static final String STATUS = "status_bytes";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + ID + " INTEGER PRIMARY KEY, "
            + GROUP_ID + " INTEGER, "
            + STATUS + " BLOB"
            + " );";


    public HiddenStatusDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    public HiddenStatus getStatus(long id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            cursor = database.query(TABLE_NAME, null, ID + " = ?", new String[]{Long.toString(id)}, null, null, null);
            if (cursor == null)
                return null;
            if (cursor.moveToFirst() && !cursor.isAfterLast())
                return cursorToStatus(cursor);
            else
                return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /*
     * General querying with options
     */
    public boolean getStatuses(DatabaseExecutor.ReadableQueryCallback callback) {
        return DatabaseFactory.getDatabaseExecutor(context).addQuery(
                new DatabaseExecutor.ReadableQuery() {
                    @Override
                    public Object read() {
                        return getStatuses();
                    }
                }, callback);
    }

    private Object getStatuses() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor == null)
            return null;
        try {
            ArrayList<HiddenStatus> ret = new ArrayList<HiddenStatus>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ret.add(cursorToStatus(cursor));
            }
            return ret;
        } finally {
            cursor.close();
        }
    }

    private HiddenStatus cursorToStatus(Cursor cursor) {
        if (cursor == null)
            return null;
        if (cursor.isAfterLast())
            return null;

        long statusDBID = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        String gid = cursor.getString(cursor.getColumnIndexOrThrow(GROUP_ID));
        byte[] status_bytes = cursor.getBlob(cursor.getColumnIndexOrThrow(STATUS));

        return new HiddenStatus(statusDBID, gid, status_bytes);
    }

    public long insertStatus(HiddenStatus status) {
        ContentValues contentValues = new ContentValues();

        long group_DBID = DatabaseFactory.getGroupDatabase(context).getGroupDBID(status.getGid());

        if (group_DBID < 0)
            return -1;

        contentValues.put(GROUP_ID, status.getGid());
        contentValues.put(STATUS, status.getStatus());

        long statusID = databaseHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        if (statusID >= 0) {
            status.setDbid(statusID);
            EventBus.getDefault().post(new HiddenStatusInsertedEvent(status));
        }

        return statusID;
    }

    /*
    * Delete a status per ID or UUID
    */
    public boolean deleteStatus(long id) {
        SQLiteDatabase wd = databaseHelper.getWritableDatabase();
        int rows = wd.delete(TABLE_NAME, ID_WHERE, new String[]{Long.toString(id)});
        return rows > 0;
    }
}