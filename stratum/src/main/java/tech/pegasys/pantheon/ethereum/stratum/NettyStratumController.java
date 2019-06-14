/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.stratum;

import static com.google.common.base.Preconditions.checkState;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyStratumController {

  private static final Logger LOG = LogManager.getLogger();
  private static final int TIMEOUT_SECONDS = 30;

  private final EventLoopGroup boss = new NioEventLoopGroup(1);

  private final EventLoopGroup workers = new NioEventLoopGroup(1);


  private final ChannelFuture server;

//  private final int maxPeers;


  public NettyStratumController() {
    int stratumPort = 1234;
    server =
        new ServerBootstrap()
            .group(boss, workers)
            .channel(NioServerSocketChannel.class)
            .childHandler(inboundChannelInitializer())
            .bind(stratumPort);
    final CountDownLatch latch = new CountDownLatch(1);
    server.addListener(
        future -> {
          final InetSocketAddress socketAddress =
              (InetSocketAddress) server.channel().localAddress();
          final String message =
              String.format(
                  "Unable start up stratum on port:%s.  Check for port conflicts.", stratumPort);

          if (!future.isSuccess()) {
            LOG.error(message, future.cause());
          }
          checkState(socketAddress != null, message);
          LOG.info("Stratum service started and listening on {}", socketAddress);
          latch.countDown();
        });

    // Ensure ourPeerInfo has been set prior to returning from the constructor.
    try {
      if (!latch.await(1, TimeUnit.MINUTES)) {
        throw new RuntimeException("Timed out while waiting for network startup");
      }
    } catch (final InterruptedException e) {
      throw new RuntimeException("Interrupted before startup completed", e);
    }
  }

  /**
   * @return a channel initializer for inbound connections
   */
  public ChannelInitializer<SocketChannel> inboundChannelInitializer() {
    return new ChannelInitializer<SocketChannel>() {
      protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new StratumInboundMessageHandler());
      }
    };
//    return null;
//    return new ChannelInitializer<SocketChannel>() {
//      @Override
//      protected void initChannel(final SocketChannel ch) {
//        final CompletableFuture<PeerConnection> connectionFuture = new CompletableFuture<>();
//        ch.pipeline()
//            .addLast(
//                new TimeoutHandler<>(
//                    connectionFuture::isDone,
//                    TIMEOUT_SECONDS,
//                    () ->
//                        connectionFuture.completeExceptionally(
//                            new TimeoutException(
//                                "Timed out waiting to fully establish incoming connection"))),
//                new HandshakeHandlerInbound(
//                    keyPair,
//                    subProtocols,
//                    ourPeerInfo,
//                    connectionFuture,
//                    callbacks,
//                    connections,
//                    outboundMessagesCounter));
//
//        connectionFuture.thenAccept(
//            connection -> {
//
//              onConnectionEstablished(connection);
//              LOG.debug(
//                  "Successfully accepted connection from {}", connection);
//              logConnections();
//            });
//      }
//    };
  }


}
