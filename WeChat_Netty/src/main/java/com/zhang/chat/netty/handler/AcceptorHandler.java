package com.zhang.chat.netty.handler;

import com.zhang.chat.netty.pool.ConnPool;
import com.zhang.chat.netty.protocol.MessageHolder;
import com.zhang.chat.netty.protocol.ProtocolHeader;
import com.zhang.chat.netty.queue.TaskQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * 最终接收数据的Handler，将待处理数据放入阻塞队列中，由服务模块take and deal.
 *
 * @author Yohann.
 */
public class AcceptorHandler extends ChannelHandlerAdapter {
    private static final Logger logger = Logger.getLogger(AcceptorHandler.class);

    private final BlockingQueue<MessageHolder> taskQueue;

    public AcceptorHandler() {
        taskQueue = TaskQueue.getQueue();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String query = ConnPool.query(ctx.channel());
//        ConnPool.remove(query);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageHolder) {
            MessageHolder messageHolder = (MessageHolder) msg;
            // 指定Channel
            messageHolder.setChannel(ctx.channel());

            // 添加到任务队列
            boolean offer = taskQueue.offer(messageHolder);
            logger.info("TaskQueue添加任务: taskQueue=" + taskQueue.size());
            if (!offer) {
                // 服务器繁忙
                logger.warn("服务器繁忙，拒绝服务");
                // 繁忙响应
                response(ctx.channel(), messageHolder.getSign());
            }
        } else {
            throw new IllegalArgumentException("msg is not instance of MessageHolder");
        }
    }

    /**
     * 服务器繁忙响应
     *
     * @param channel
     * @param sign
     */
    private void response(Channel channel, byte sign) {
        MessageHolder messageHolder = new MessageHolder();
        messageHolder.setSign(ProtocolHeader.RESPONSE);
        messageHolder.setType(sign);
        messageHolder.setStatus(ProtocolHeader.SERVER_BUSY);
        messageHolder.setBody("");
        channel.writeAndFlush(messageHolder);
    }
}
