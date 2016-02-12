package org.disrupted.rumble.network.protocols.events;

import org.disrupted.rumble.database.objects.Contact;
import org.disrupted.rumble.database.objects.HiddenStatus;
import org.disrupted.rumble.network.events.NetworkEvent;

import java.util.Set;

/**
 * This event holds every information known on a transmission that happened successfully. These
 * information includes:
 * <p/>
 * - The sent status (as it was sent)
 * - The receiver(s) (or an estimation of it in the case of Multicast IP)
 * - The protocol used to transmit this status (rumble, firechat)
 * - The link layer used (bluetooth, wifi)
 * <p/>
 * These information will be used by different component to update some informations :
 * - The CacheManager to update its list and the neighbour's queue as well
 * - The LinkLayerAdapter to update its internal metric that is used by getBestInterface
 * - The FragmentStatusList to provide a visual feedback to the user
 *
 * @author Davide Bove
 */
public class HiddenStatusSent extends NetworkEvent {

    public HiddenStatus status;
    public Set<Contact> recipients;
    public String protocolID;
    public String linkLayerIdentifier;

    public HiddenStatusSent(HiddenStatus status, Set<Contact> recipients, String protocolID, String linkLayerIdentifier) {
        this.status = status;
        this.recipients = recipients;
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
