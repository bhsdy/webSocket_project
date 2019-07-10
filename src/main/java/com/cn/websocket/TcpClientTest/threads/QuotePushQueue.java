package com.cn.websocket.TcpClientTest.threads;

import com.cn.websocket.entity.ChannelManager;
import com.cn.websocket.entity.MessageQueue;
import com.cn.websocket.entity.PushElementDTO;
import com.cn.websocket.entity.QuoteDTO;
import com.cn.websocket.entity.constants.PushCmdConstants;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class QuotePushQueue extends MessageQueue<QuoteDTO> {

	@Autowired
	private ChannelManager channelManager;

	@Override
	protected void execute(QuoteDTO quote) {
		PushElementDTO<QuoteDTO> pushElement = new PushElementDTO<>();
		pushElement.setCmd(PushCmdConstants.pushQuoteCmd);
		pushElement.setData(quote);
		List<Channel> channelList = channelManager.getQuoteChannelList(quote.getCommodityNo() + quote.getContractNo());
		for (Channel channel : channelList) {
			if(channel.isActive()) {
				if(channel.isWritable()) {
					channel.writeAndFlush(pushElement);
				}
			} 
		}
		channelList = channelManager.getQuoteChannelList("All");
		for (Channel channel : channelList) {
			if(channel.isActive()) {
				if(channel.isWritable()) {
					channel.writeAndFlush(pushElement);
				}
			} 
		}
		log.info(quote.toString());
	}

}
