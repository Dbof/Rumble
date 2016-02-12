package org.disrupted.rumble.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.disrupted.rumble.database.objects.HiddenStatus;

import java.util.ArrayList;

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

    private ArrayList<HiddenStatus> getStatuses() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if(cursor == null)
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

}