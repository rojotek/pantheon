package tech.pegasys.pantheon.ethereum.stratum.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;

public class MiningSubscribe implements JsonRpcMethod {

  @Override
  public String getName() {
    return "mining.subscribe";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {
    String session = "session";
    return new JsonRpcSuccessResponse(request.getId(), session);
  }
}
