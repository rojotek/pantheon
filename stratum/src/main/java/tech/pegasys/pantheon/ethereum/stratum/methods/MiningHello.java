package tech.pegasys.pantheon.ethereum.stratum.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;
import tech.pegasys.pantheon.ethereum.stratum.messages.outbound.HelloResult;

public class MiningHello implements JsonRpcMethod {

  @Override
  public String getName() {
    return "mining.hello";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {

    HelloResult helloResult = new HelloResult(HelloResult.STRATUM_V2,
        "plain", 0L, 30L, 5L, "XXXX");
    System.out.println("mining.hello");
    return new JsonRpcSuccessResponse(request.getId(), helloResult);

  }
}
