package tech.pegasys.pantheon.ethereum.stratum.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;

public class MiningSubmit implements JsonRpcMethod {

  @Override
  public String getName() {
    return "mining.submit";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {
    return null;
  }
}
