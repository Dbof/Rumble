package org.disrupted.rumble.network.protocols.rumble.packetformat;

import android.util.Base64;

import org.disrupted.rumble.database.objects.HiddenStatus;
import org.disrupted.rumble.database.objects.PushStatus;
import org.disrupted.rumble.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.rumble.network.protocols.command.CommandSendHiddenStatus;
import org.disrupted.rumble.network.protocols.rumble.packetformat.exceptions.MalformedBlockPayload;
import org.disrupted.rumble.util.EncryptedOutputStream;
import org.disrupted.rumble.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A BlockHiddenStatus holds a PushStatus from an unknown group
 * <p/>
 * +-------------------------------------------+
 * |               Group ID                    |  8 byte  Group UID
 * +-------------------------------------------+
 * |                                           |  Variable (header.getBlockLength())
 * |                                           |
 * |                                           |
 * |       Bytes containing status             |
 * |                                           |
 * |                                           |
 * +-------------------------------------------+
 *
 * @author Davide Bove
 */
public class BlockHiddenStatus extends Block {
    public static final String TAG = "BlockHiddenStatus";

    private static final int BUFFER_SIZE = 1024;


    /* Field Byte size */
    private static final int FIELD_GROUP_ID_SIZE = 8;

    /* Block Size Boundaries */
    private static final int MIN_PAYLOAD_SIZE = (
            FIELD_GROUP_ID_SIZE);
    private static final int MAX_PAYLOAD_SIZE = (MIN_PAYLOAD_SIZE + HiddenStatus.STATUS_MAX_SIZE);

    /* Block Attributes */
    public HiddenStatus status;


    public BlockHiddenStatus(BlockHeader header) {
        super(header);
    }

    public BlockHiddenStatus(CommandSendHiddenStatus command) {
        super(new BlockHeader());
        this.header.setBlockType(BlockHeader.BLOCKTYPE_HIDDEN_STATUS);
        this.header.setTransaction(BlockHeader.TRANSACTION_TYPE_PUSH);
        this.status = command.getStatus();
    }


    public void sanityCheck() throws MalformedBlockPayload {
        if (header.getBlockType() != BlockHeader.BLOCKTYPE_HIDDEN_STATUS)
            throw new MalformedBlockPayload("Block type BLOCK_FILE expected", 0);
        if ((header.getBlockLength() < MIN_PAYLOAD_SIZE) || (header.getBlockLength() > MAX_PAYLOAD_SIZE))
            throw new MalformedBlockPayload("wrong payload size: " + header.getBlockLength(), 0);
    }

    @Override
    public long readBlock(InputStream in) throws MalformedBlockPayload, IOException, InputOutputStreamException {
        sanityCheck();

        /* read the block */
        long readleft = header.getBlockLength();
        byte[] blockbuffer = new byte[MIN_PAYLOAD_SIZE];
        int count = in.read(blockbuffer, 0, MIN_PAYLOAD_SIZE);
        if (count < 0)
            throw new IOException("end of stream reached");
        if (count < MIN_PAYLOAD_SIZE)
            throw new MalformedBlockPayload("read less bytes than expected: " + count, count);

        BlockDebug.d(TAG, "BlockHiddenStatus received (" + count + " bytes): ");

        ByteBuffer byteBuffer = ByteBuffer.wrap(blockbuffer);
        byte[] gid = new byte[FIELD_GROUP_ID_SIZE];
        byteBuffer.get(gid, 0, FIELD_GROUP_ID_SIZE);
        readleft -= FIELD_GROUP_ID_SIZE;

        // read blob
        byte[] buffer = new byte[(int) readleft];

        // read all into the buffer
        int bytesread = in.read(buffer, 0, (int) readleft);
        if (bytesread < 0)
            throw new IOException("End of stream reached before reading was complete");
        readleft -= bytesread;

        gid = Base64.encode(gid, Base64.NO_WRAP);

        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, bytesread);
        Log.d(TAG, "bytesread = " + bytesread + " - buffer size: " + bb.array().length + " bytes");

        status = new HiddenStatus(new String(gid), bb.array());
        status.setHeader(header);

        Log.d(TAG, "HiddenStatus received (" + bytesread + " bytes)");
        return header.getBlockLength();
    }

    @Override
    public long writeBlock(OutputStream out, EncryptedOutputStream eos) throws IOException, InputOutputStreamException {
        /* preparing some buffer and calculate the block size */
        byte[] group_id = Base64.decode(status.getGid(), Base64.NO_WRAP);
        byte[] hs = status.getStatus();

        int length = MIN_PAYLOAD_SIZE + hs.length;
        Log.d(TAG, "Size of status: " + length + " bytes");

        /* prepare the block buffer */
        ByteBuffer blockBuffer = ByteBuffer.allocate(length);
        blockBuffer.put(group_id, 0, FIELD_GROUP_ID_SIZE);
        blockBuffer.put(hs, 0, hs.length);

        /* send the status */
        header.setPayloadLength(length);
        header.writeBlockHeader(out);
        out.write(blockBuffer.array(), 0, length);

        Log.d(TAG, "HiddenStatus sent (" + length + " bytes)");
        return header.getBlockLength() + BlockHeader.BLOCK_HEADER_LENGTH;
    }

    @Override
    public void dismiss() {
        status = null;
    }
}
