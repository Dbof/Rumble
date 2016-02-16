package org.disrupted.rumble.database.events;

import org.disrupted.rumble.database.objects.HiddenStatus;

/**
 * @author davide
 */
public class HiddenStatusInsertedEvent extends StatusDatabaseEvent {


    public final HiddenStatus status;

    public HiddenStatusInsertedEvent(HiddenStatus status){
        this.status = status;
    }

    @Override
    public String shortDescription() {
        if(status != null)
            return status.toString();
        else
            return "";
    }
}
