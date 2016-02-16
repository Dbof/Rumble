package org.disrupted.rumble.database.events;

/**
 * @author davide
 */
public class HiddenStatusDeletedEvent extends StatusDatabaseEvent {

    public final long dbid;

    public HiddenStatusDeletedEvent(long dbid) {
        this.dbid = dbid;
    }

    @Override
    public String shortDescription() {
        return "dbid=" + dbid;
    }
}
