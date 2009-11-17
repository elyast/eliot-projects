/**
 * 
 */
package org.jdiameter.plugins.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

public class MyMessageDecoder implements MessageDecoder {

    private int dataLength;
    protected IMessageParser parser;

    public MyMessageDecoder(IMessageParser parser) {
        this.parser = parser;
    }

    public MessageDecoderResult decodable(IoSession ioSession, IoBuffer in) {

        if (in.remaining() < 4) {
            return MessageDecoderResult.NEED_DATA;
        }

        int tmp = in.getInt();
        byte vers = (byte) (tmp >> 24);
        if (vers != 1) return MessageDecoderResult.NOT_OK;
        dataLength = (tmp & 0xFFFFFF);

        return MessageDecoderResult.OK;

    }

    public MessageDecoderResult decode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput output) throws Exception {
        if (in.remaining() < dataLength)
            return MessageDecoderResult.NEED_DATA;
        try {
            byte[] data = new byte[dataLength];
            in.get(data);
            IMessage message = parser.createMessage(prepareBuffer(data));
            output.write(message);
            return MessageDecoderResult.OK;
        } catch (Exception exc) {
            return MessageDecoderResult.NOT_OK;
        }
    }

    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
    }

    protected java.nio.ByteBuffer prepareBuffer(byte[] bytes) {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }
}