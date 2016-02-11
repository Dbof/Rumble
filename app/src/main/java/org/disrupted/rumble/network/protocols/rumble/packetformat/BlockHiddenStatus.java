package org.disrupted.rumble.network.protocols.rumble.packetformat;

import org.disrupted.rumble.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.rumble.network.protocols.rumble.packetformat.exceptions.MalformedBlockPayload;
import org.disrupted.rumble.util.EncryptedOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dbof on 12/02/16.
 */
public class BlockHiddenStatus extends Block {
    public BlockHiddenStatus(BlockHeader header) {
        super(header);
    }

    @Override
    public long readBlock(InputStream in) throws MalformedBlockPayload, IOException, InputOutputStreamException {
        return 0;
    }

    @Override
    public long writeBlock(OutputStream out, EncryptedOutputStream eos) throws IOException, InputOutputStreamException {
        return 0;
    }

    @Override
    public void dismiss() {

    }
}
