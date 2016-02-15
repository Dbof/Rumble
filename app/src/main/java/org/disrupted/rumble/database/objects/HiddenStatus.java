package org.disrupted.rumble.database.objects;

import org.disrupted.rumble.network.protocols.command.CommandSendPushStatus;
import org.disrupted.rumble.network.protocols.rumble.packetformat.BlockHeader;
import org.disrupted.rumble.network.protocols.rumble.packetformat.BlockHiddenStatus;
import org.disrupted.rumble.network.protocols.rumble.packetformat.BlockPushStatus;
import org.disrupted.rumble.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author davide
 */
public class HiddenStatus {
    private static final String TAG = "StatusMessage";

    public static final int STATUS_MAX_SIZE = 5000000; // limit 5Mb file. This is arbitrary..

    protected long dbid;
    protected String gid;
    protected byte[] status_bytes;
    private BlockHeader header;


    public HiddenStatus(String gid, byte[] status_bytes) {
        this(-1, gid, status_bytes);
    }

    public HiddenStatus(long db_id, String gid, byte[] status_bytes) {
        this.dbid = db_id;
        this.gid = gid;
    }

    public long getDbid() {
        return dbid;
    }

    public String getGid() {
        return gid;
    }

    public byte[] getStatus() {
        return status_bytes;
    }

    public void setDbid(long dbid) {
        this.dbid = dbid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenStatus that = (HiddenStatus) o;

        if (dbid != that.dbid) return false;
        return gid.equals(that.gid);

    }

    @Override
    public int hashCode() {
        int result = (int) (dbid ^ (dbid >>> 32));
        result = 31 * result + gid.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Group ID: ").append(this.gid).append("\n");
        b.append("Blob size: ").append(this.status_bytes.length).append(" bytes)");
        return b.toString();
    }

    public PushStatus convertToPushStatus() {
        BlockPushStatus bps = new BlockPushStatus(header);
        ByteArrayInputStream is = new ByteArrayInputStream(status_bytes);
        // TODO: this does not support encrypted statuses, where IV and key is required


        PushStatus res = null;
        try {
            // first read block header
            BlockHeader header_unused = BlockHeader.readBlockHeader(is);

            // then read block
            bps.readBlock(is);

            // save status
            res = bps.status;
            Log.d(TAG, "HiddenStatus was converted successfully!");
        } catch (Exception e) {
            Log.e(TAG, "convertToPushStatus failed: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
        return res;
    }

    public static HiddenStatus convertFromPushStatus(PushStatus status) {
        HiddenStatus res = null;

        BlockPushStatus bps = new BlockPushStatus(new CommandSendPushStatus(status));
        ByteArrayOutputStream os = new ByteArrayOutputStream(STATUS_MAX_SIZE);

        try {
            // the following writes the header and the block
            bps.writeBlock(os, null);
            res = new HiddenStatus(status.getGroup().getGid(), os.toByteArray());
            Log.d(TAG, "PushStatus was converted successfully!");
        } catch (Exception e) {
            Log.e(TAG, "convertFromPushStatus failed: " + e.getMessage());
        } finally {
            try {
                os.close();
            } catch (IOException ignore) {
            }
        }

        return res;
    }

    public void setHeader(BlockHeader header) {
        this.header = header;
    }
}
