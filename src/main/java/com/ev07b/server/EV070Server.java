package com.ev07b.server;

import com.ev07b.codec.EV07BFrameDecoder;
import com.ev07b.codec.EV07BEncoder;
import com.ev07b.handler.EV07BBusinessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EV070Server {
    public static void main(String[] args) throws Exception {
        int port = getPort();
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new EV07BFrameDecoder());
                     p.addLast(new EV07BEncoder());
                     p.addLast(new EV07BBusinessHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind("0.0.0.0", port).sync();
            System.out.println("EV07B server listening on 0.0.0.0:" + port);
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static int getPort() {
        String p = System.getenv("PORT");
        if (p != null) {
            try { return Integer.parseInt(p); } catch (Exception ignored) {}
        }
        return 5050;
    }
}
