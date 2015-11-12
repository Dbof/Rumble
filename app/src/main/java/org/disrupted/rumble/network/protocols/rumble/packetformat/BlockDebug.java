/*
 * Copyright (C) 2014 Disrupted Systems
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

package org.disrupted.rumble.network.protocols.rumble.packetformat;

import android.util.Log;

/**
 * @author Marlinski
 */
public class BlockDebug {
    public static final boolean DEBUG = true;
    public static final boolean ERROR = true;


    public static void d(String TAG, String debugMessage) {
        if(DEBUG) {
            Log.d(TAG, debugMessage);
        }
    }
    public static void d(String TAG, String debugMessage, Exception e) {
        if(DEBUG) {
            e.printStackTrace();
            Log.d(TAG, debugMessage);
        }
    }

    public static void e(String TAG, String debugMessage) {
        if(DEBUG && ERROR) {
            Log.e(TAG, debugMessage);
        }
    }

    public static void e(String TAG, String debugMessage, Exception e) {
        if(DEBUG && ERROR) {
            e.printStackTrace();
            Log.e(TAG, debugMessage);
        }
    }
}
