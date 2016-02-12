/*
 * Copyright (C) 2014 Lucien Loiseau
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.disrupted.rumble.app.RumbleApplication;
import org.disrupted.rumble.database.objects.Group;

import java.util.ArrayList;

/**
 * @author Lucien Loiseau
 */
public class ContactGroupDatabase extends Database {

    private static final String TAG = "ContactGroupDatabase";

    public static final String TABLE_NAME = "group_subscriptions";
    public static final String UDBID = "_udbid";
    public static final String GDBID = "_gdbid";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + UDBID + " INTEGER, "
            + GDBID + " INTEGER, "
            + " UNIQUE( " + UDBID + " , " + GDBID + "), "
            + " FOREIGN KEY ( " + UDBID + " ) REFERENCES " + ContactDatabase.TABLE_NAME + " ( " + ContactDatabase.ID + " ), "
            + " FOREIGN KEY ( " + GDBID + " ) REFERENCES " + GroupDatabase.TABLE_NAME + " ( " + GroupDatabase.ID + " )"
            + " );";


    public ContactGroupDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public void deleteEntriesMatchingContactID(long contactID) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, UDBID + " = ?", new String[]{Long.toString(contactID)});
    }

    public void deleteEntriesMatchingGroupID(long groupID) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, GDBID + " = ?", new String[]{Long.toString(groupID)});
    }

    public long insertContactGroup(long contactID, long groupID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UDBID, contactID);
        contentValues.put(GDBID, groupID);
        try {
            return databaseHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_FAIL);
        } catch (SQLiteConstraintException ce) {
            return -1;
        }
    }

    public boolean getGroups(final String contact_uid, DatabaseExecutor.ReadableQueryCallback callback) {
        return DatabaseFactory.getDatabaseExecutor(context).addQuery(
                new DatabaseExecutor.ReadableQuery() {
                    @Override
                    public Object read() {
                        return getGroups(contact_uid);
                    }
                }, callback);
    }

    private ArrayList<Group> getGroups(String contactUID) {
        long contactID = DatabaseFactory.getContactDatabase(RumbleApplication.getContext()).getContactDBID(contactUID);

        Cursor cursor = null;
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            cursor = database.query(TABLE_NAME, null, UDBID + " = ?", new String[]{Long.toString(contactID)}, null, null, null);
            if (cursor == null)
                return null;
            ArrayList<Group> ret = new ArrayList<Group>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long group_id = cursor.getLong(cursor.getColumnIndexOrThrow(GDBID));
                Group g = DatabaseFactory.getGroupDatabase(RumbleApplication.getContext()).getGroup(group_id);
                ret.add(g);
            }
            return ret;
        } finally {
            cursor.close();
        }
    }
}
