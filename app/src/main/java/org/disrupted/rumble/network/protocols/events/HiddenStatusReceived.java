package org.disrupted.rumble.network.protocols.events;

import org.disrupted.rumble.database.objects.HiddenStatus;
import org.disrupted.rumble.network.events.NetworkEvent;

/**
 * This event holds every information known on a received transmission that happened successfully.
 * These information includes:
 * <p/>
 * - The received status bytes (as it was received)
 * - The group id
 * - The protocol used to transmit this status (rumble, firechat)
 * - The link layer used (bluetooth, wifi)
 * <p/>
 * These information will be used by different component to update some information :
 * - The CacheManager to update its list and the neighbour's queue as well
 * - The LinkLayerAdapter to update its internal metric that is used by getBestInterface
 * - The FragmentStatusList to provide a visual feedback to the user
 *
 * @author Davide Bove
 */
public class HiddenStatusReceived extends NetworkEvent {

    public HiddenStatus status;
    public String protocolID;
    public String linkLayerIdentifier;

    public HiddenStatusReceived(HiddenStatus status, String protocolID, String linkLayerIdentifier) {
        this.status = status;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
    }

    @Override
    public String shortDescription() {
        if (status != null)
            return status.toString();
        else
            return "";
    }
}
