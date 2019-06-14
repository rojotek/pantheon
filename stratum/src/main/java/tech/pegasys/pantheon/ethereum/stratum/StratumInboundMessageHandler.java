package tech.pegasys.pantheon.ethereum.stratum;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequestId;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcError;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.stratum.methods.StratumJsonRpcMethodsFactory;

public class StratumInboundMessageHandler extends JsonObjectDecoder {
  private static final Logger LOG = LogManager.getLogger();
  private final Map<String, JsonRpcMethod> methods;

  public StratumInboundMessageHandler() {
    methods = new StratumJsonRpcMethodsFactory().methods();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inBuffer = (ByteBuf) msg;

    String received = inBuffer.toString(CharsetUtil.UTF_8);
    System.out.println("Server received: " + received);
    JsonObject requestJson = new JsonObject(received);
    Object id = null;
    final JsonRpcRequest request;
    try {
      id = new JsonRpcRequestId(requestJson.getValue("id")).getValue();
      request = requestJson.mapTo(JsonRpcRequest.class);
      System.out.println("JSON-RPC request " +request);
    } catch (final IllegalArgumentException exception) {
//      errorResponse(id, JsonRpcError.INVALID_REQUEST);
      exception.printStackTrace();
      return;
    }
    // Handle notifications
    if (request.isNotification()) {
      // Notifications aren't handled so create empty result for now.

    }

    System.out.println("JSON-RPC request " +request.getMethod());
    // Find method handler
    final JsonRpcMethod method = methods.get(request.getMethod());
    if (method == null) {
//      errorResponse(id, JsonRpcError.METHOD_NOT_FOUND);
      return;
    }
    final JsonRpcResponse response = method
        .response(request);
    ObjectMapper mapper = new ObjectMapper();

    ctx.write(Unpooled.copiedBuffer(mapper.writeValueAsString(response), CharsetUtil.UTF_8));
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
//    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//        .addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
