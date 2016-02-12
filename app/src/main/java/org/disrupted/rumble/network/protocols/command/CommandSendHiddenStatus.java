package org.disrupted.rumble.network.protocols.command;

import org.disrupted.rumble.database.objects.HiddenStatus;

/**
 * Created by davide on 12/02/16.
 */
public class CommandSendHiddenStatus extends Command {

    private HiddenStatus status;

    public CommandSendHiddenStatus(HiddenStatus status) {
        this.status = status;
    }

    public HiddenStatus getStatus() {
        return status;
    }

    @Override
    public CommandID getCommandID() {
        return CommandID.SEND_HIDDEN_STATUS;
    }
}
