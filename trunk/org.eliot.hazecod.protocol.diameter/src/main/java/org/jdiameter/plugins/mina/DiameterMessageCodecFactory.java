/**
 * 
 */
package org.jdiameter.plugins.mina;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.jdiameter.api.MetaData;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.parser.MessageParser;
import org.jdiameter.server.impl.MetaDataImpl;
import org.jdiameter.server.impl.StackImpl;

public class DiameterMessageCodecFactory extends DemuxingProtocolCodecFactory implements DiameterMessageCodec {

    public DiameterMessageCodecFactory() {
    	IContainer container = new StackImpl();
    	MetaData metadata  = new MetaDataImpl(container);
    	IMessageParser parser = new MessageParser(metadata);
        super.addMessageEncoder(IMessage.class, new MyMessageEncoder(parser));
        super.addMessageDecoder(new MyMessageDecoder(parser));
    }
}