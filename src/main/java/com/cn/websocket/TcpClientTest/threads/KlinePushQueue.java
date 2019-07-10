package com.cn.websocket.TcpClientTest.threads;

import com.cn.websocket.entity.ChannelManager;
import com.cn.websocket.entity.KlineDTO;
import com.cn.websocket.entity.MessageQueue;
import com.cn.websocket.entity.PushElementDTO;
import com.cn.websocket.entity.constants.PushCmdConstants;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KlinePushQueue extends MessageQueue<KlineDTO> {

	@Autowired
	private ChannelManager channelManager;

	@Override
	protected void execute(KlineDTO kline) {
		PushElementDTO<KlineDTO> pushElement = new PushElementDTO<>();
		pushElement.setCmd(PushCmdConstants.pushKlineCmd);
		pushElement.setData(kline);
		List<Channel> channelList = channelManager
				.getKlineChannelList(kline.getCommodityNo() + kline.getContractNo() + kline.getType());
		for (Channel channel : channelList) {
			if(channel.isActive()) {
				if(channel.isWritable()) {
					channel.writeAndFlush(pushElement);
				}
			}
		}
		log.info(kline.toString());
	}

}
