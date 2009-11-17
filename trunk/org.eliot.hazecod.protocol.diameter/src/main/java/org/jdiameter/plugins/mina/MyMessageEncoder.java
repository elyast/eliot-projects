/**
 * 
 */
package org.jdiameter.plugins.mina;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

public class MyMessageEncoder implements MessageEncoder {

    private static final Set TYPES;
    protected IMessageParser parser;

    static {
        Set<Class> types = new HashSet<Class>();
        types.add(IMessage.class);
        TYPES = Collections.unmodifiableSet(types);
    }


    public MyMessageEncoder(IMessageParser parser) {
        this.parser = parser;
    }

    public Set<Class<?>> getMessageTypes() {
        return TYPES;
    }

    public void encode(IoSession ioSession, Object object, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buf = IoBuffer.allocate(16);
        buf.setAutoExpand(true);
        buf.put(parser.encodeMessage((IMessage) object));
        out.write(buf);
        buf.buf().position(0);  // reset position
    }
}