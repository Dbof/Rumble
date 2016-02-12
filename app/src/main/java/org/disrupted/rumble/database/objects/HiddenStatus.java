package org.disrupted.rumble.database.objects;

/**
 * Created by davide on 12/02/16.
 */
public class HiddenStatus {
    private static final String TAG = "StatusMessage";

    public static final int STATUS_MAX_SIZE = 5000000; // limit 5Mb file. This is arbitrary..

    protected long dbid;
    protected String gid;
    protected byte[] status_bytes;


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
}
