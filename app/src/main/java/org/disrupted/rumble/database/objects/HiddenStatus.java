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
        this.status_bytes = status_bytes;
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
        ByteArrayInputStream is = new ByteArrayInputStream(status_bytes);

        PushStatus res = null;
        try {
            // first read block header
            BlockHeader header_unused = BlockHeader.readBlockHeader(is);
            if (header_unused.isEncrypted())
                return null; // TODO: does not support encrypted statuses, where IV and key is required
            BlockPushStatus bps = new BlockPushStatus(header_unused);

            // then read block
            bps.readBlock(is);

            // save status
            bps.status.setGroup(new Group(bps.group_id_base64, bps.group_id_base64, null));
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
        if (status.getGroup().isPrivate()) {
            // TODO
            // encrypted groups are not supported,
            // because both the key and the IV byte value is needed to decrypt messages.
            // If IVs are sent to every user on a route, security only really depends on key,
            // which makes the encryption too vulnerable.
            Log.e(TAG, "[!] Private groups are not supported yet. " +
                    "Cannot send private message as hidden status.");
            return null;
        }

        HiddenStatus res = null;

        BlockPushStatus bps = new BlockPushStatus(new CommandSendPushStatus(status));
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            // the following writes the header and the block
            bps.writeBlock(os, null);
            os.flush();

            Log.d(TAG, "Size of created status: " + os.toByteArray().length + " bytes");
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
